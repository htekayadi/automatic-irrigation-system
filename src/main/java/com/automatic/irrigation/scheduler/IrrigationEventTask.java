package com.automatic.irrigation.scheduler;

import com.automatic.irrigation.config.SchedulerConfig;
import com.automatic.irrigation.constants.Status;
import com.automatic.irrigation.dto.PlotDTO;
import com.automatic.irrigation.dto.SensorDTO;
import com.automatic.irrigation.dto.TimeslotDTO;
import com.automatic.irrigation.service.AlertService;
import com.automatic.irrigation.service.PlotService;
import com.automatic.irrigation.service.SensorService;
import com.automatic.irrigation.service.TimeslotService;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Scope("prototype")
@Log4j2
public class IrrigationEventTask implements Runnable {

    @Autowired
    private PlotService plotService;
    @Autowired
    private TimeslotService slotService;
    @Autowired
    private SensorService sensorService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private SchedulerConfig schedulerConfig;

    @Value("${irrigation.sensor.device.url}")
    private String sensorDeviceUrl;

    @Override
    public void run() {
        log.info("Started irrigation event thread");
        try {
            List<PlotDTO> plotDTOList = plotService.getAllPlots();
            for (PlotDTO plot : plotDTOList) {
                processPlot(plot);
            }
        } catch (Exception exception) {
            log.error("Error occurred during the task execution.", exception);
        }
        log.info("Finished irrigation event thread");
    }

    private void processPlot(PlotDTO plotDTO) {
        log.info("Started processing for the plot with ID : {}", plotDTO.getId());
        Set<String> timeslotIds = plotDTO.getTimeslotIds();
        if (timeslotIds != null) {
            for (String timeslotId : timeslotIds) {
                TimeslotDTO timeslotDTO = getSlot(timeslotId);
                if (timeslotDTO != null) {
                    processTimeslot(timeslotDTO, plotDTO);
                }
            }
        }
        log.info("Finished processing for the plot with ID : {}", plotDTO.getId());
    }

    private void processTimeslot(TimeslotDTO timeslotDTO, PlotDTO plotDTO) {
        log.info("Started processing for the slot with id: {}", timeslotDTO.getId());
        Instant currentTime = Instant.now();
        if (currentTime.isAfter(timeslotDTO.getStartTime()) && currentTime.isBefore(timeslotDTO.getEndTime())) {
            if (timeslotDTO.getStatus() == Status.CONFIGURED) {
                log.info("Sending request to sensor to start irrigation for slot: {}", timeslotDTO);
                processSensor(plotDTO, timeslotDTO);
            } else {
                log.info("Irrigation is already in progress for the slot: {}", timeslotDTO);
            }
        } else {
            // update status of slot if not being irrigated
            slotService.updateStatus(timeslotDTO.getId(), Status.CONFIGURED);
            log.info("Skip the slot: {}", timeslotDTO);
        }
        log.info("Finished processing for the slot with id: {}", timeslotDTO.getId());
    }

    private void processSensor(PlotDTO plotDTO, TimeslotDTO timeslotDTO) {
        SensorDTO sensorDTO = getSensor(plotDTO.getSensorId());
        if (sensorDTO != null) {
            try {
                boolean result = sendRequestToSensorDevice(sensorDTO, timeslotDTO);
                if (result) {
                    slotService.updateStatus(timeslotDTO.getId(), Status.DONE);
                }
            } catch (IOException e) {
                log.error("Error occurred while sending request to sensor", e);
            }
        }
    }

    private boolean sendRequestToSensorDevice(SensorDTO sensorDTO, TimeslotDTO timeslotDTO) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(sensorDeviceUrl);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("startTime", timeslotDTO.getStartTime().toString()));
        params.add(new BasicNameValuePair("endTime", timeslotDTO.getEndTime().toString()));
        params.add(new BasicNameValuePair("amountOfWater", timeslotDTO.getAmountOfWater().toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = client.execute(httpPost);
        if (response.getCode() == 200)
            return true;
        client.close();

        return false;
    }

    private TimeslotDTO getSlot(String slotId) {
        try {
            return slotService.getSlot(slotId);
        } catch (Exception exception) {
            log.error("Unable to retrieve slot details", exception);
            return null;
        }
    }

    private SensorDTO getSensor(String sensorId) {
        Integer retry = schedulerConfig.getSchedulerRetry();
        while (retry > 0) {
            try {
                return sensorService.getSensor(sensorId);
            } catch (Exception exception) {
                log.error("Unable to retrieve sensor details", exception);
                retry--;
            }
        }
        log.error("Max retry limit exceeded. Sensor not available.");
        alertService.sendAlert("Max retry limit has been exceeded. Sensor is not available.");
        return null;
    }
}
