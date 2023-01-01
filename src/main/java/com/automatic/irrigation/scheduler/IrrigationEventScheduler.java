package com.automatic.irrigation.scheduler;

import com.automatic.irrigation.config.SchedulerConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class IrrigationEventScheduler {
    
    @Autowired
    private SchedulerConfig schedulerConfig;
    @Autowired
    private IrrigationEventTask irrigationEventTask;

    private ScheduledExecutorService executorService;

    @PostConstruct
    private void init() {
        executorService = Executors.newScheduledThreadPool(schedulerConfig.getSchedulerThreadPoolSize());
    }

    public void start() {
        try {
            executorService.scheduleAtFixedRate(
                    irrigationEventTask,
                    schedulerConfig.getSchedulerInitialDelay(),
                    schedulerConfig.getSchedulerPeriod(),
                    schedulerConfig.getSchedulerTimeUnit()
            );
        } catch (Exception exception) {
            log.error("Failed while scheduling the task", exception);
        }
        log.info("Irrigation event scheduler has been started");
    }

    @PreDestroy
    private void cleanup() throws InterruptedException {
        if (executorService != null) {
            executorService.shutdown();
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
            log.info("Irrigation event scheduler has been stopped");
        }
    }
}
