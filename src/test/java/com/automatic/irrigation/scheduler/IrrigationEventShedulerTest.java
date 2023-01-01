package com.automatic.irrigation.scheduler;

import com.automatic.irrigation.constants.RequestURI;
import com.automatic.irrigation.constants.Status;
import com.automatic.irrigation.dto.PlotDTO;
import com.automatic.irrigation.dto.SensorDTO;
import com.automatic.irrigation.dto.TimeslotDTO;
import com.automatic.irrigation.service.TimeslotService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IrrigationEventShedulerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IrrigationEventScheduler irrigationEventScheduler;
    @Autowired
    private TimeslotService timeslotService;
    
    private static ClientAndServer mockServer;

    @Value("${local.server.port}")
    private int port;

    private String plotApiUrl;
    private String sensorApiUrl;
    private String timeslotApiUrl;

    @BeforeEach
    public void setup() {
        restTemplate
                .getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        createExpectationForSensorRequest();
        plotApiUrl = "http://localhost:" + port + RequestURI.API + RequestURI.V1 + RequestURI.PLOTS;
        sensorApiUrl = "http://localhost:" + port + RequestURI.API + RequestURI.V1 + RequestURI.SENSORS;
        timeslotApiUrl = "http://localhost:" + port + RequestURI.API + RequestURI.V1 + RequestURI.TIMESLOTS;
    }

    @BeforeAll
    public static void startServer() {
        mockServer = startClientAndServer(1080);
    }

    @Test
    @Sql(scripts = "classpath:CleanupDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void start() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        // Add a Plot
        PlotDTO plotDTO = createPlotDTO();
        ResponseEntity<Object> result = restTemplate.postForEntity(plotApiUrl, plotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        PlotDTO plot = modelMapper.map(result.getBody(), PlotDTO.class);

        // Add a Timeslot
        TimeslotDTO timeslotDTO = createTimeslotDTO();
        timeslotDTO.setPlotId(plot.getId());
        result = restTemplate.postForEntity(timeslotApiUrl, timeslotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        TimeslotDTO timeslot = modelMapper.map(result.getBody(), TimeslotDTO.class);

        // Add a Sensor
        SensorDTO sensorDTO = createSensorDTO();
        sensorDTO.setPlotId(plot.getId());
        result = restTemplate.postForEntity(sensorApiUrl, sensorDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Get Plot by ID
        ResponseEntity<Object> response = restTemplate.getForEntity(plotApiUrl + "/" + plot.getId(), Object.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        irrigationEventScheduler.start();

        Awaitility.await().atMost(60, TimeUnit.SECONDS).until(waitForStatusChange());
        
        // Get Timeslot by ID
        response = restTemplate.getForEntity(timeslotApiUrl + "/" + timeslot.getId(), Object.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        TimeslotDTO slotResponse = modelMapper.map(response.getBody(), TimeslotDTO.class);
        Assertions.assertEquals(Status.DONE, slotResponse.getStatus());
    }

    private void createExpectationForSensorRequest() {
        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/v1/sensor")
                                .withHeader("\"Content-type\", \"application/json\""),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-age=86400"))
                                .withBody("{ message: 'Request received. Irrigation has been started'}")
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    private Callable<Boolean> waitForStatusChange() {
        return () -> {
            List<TimeslotDTO> slots = timeslotService.getAllSlots();
            if (!slots.isEmpty() && slots.get(0).getStatus() == Status.DONE) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        };
    }

    private SensorDTO createSensorDTO() {
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName("Sensor 01");
        sensorDTO.setPlotId("ab2c56fd-ad9e-4c5c-a5ee-2561041d4390");
        return sensorDTO;
    }

    private PlotDTO createPlotDTO() {
        PlotDTO plotDTO = new PlotDTO();
        plotDTO.setName("Plot 01");
        return plotDTO;
    }

    private TimeslotDTO createTimeslotDTO() {
        TimeslotDTO timeslotDTO = new TimeslotDTO();
        timeslotDTO.setName("Timeslot 01");
        timeslotDTO.setAmountOfWater(10L);
        timeslotDTO.setStartTime(Instant.now());
        timeslotDTO.setEndTime(Instant.now().plus(1, ChronoUnit.HOURS));
        timeslotDTO.setStatus(Status.CONFIGURED);
        return timeslotDTO;
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }
}
