package com.automatic.irrigation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class SchedulerConfig {
    private static final Logger logger = LogManager.getLogger(SchedulerConfig.class);

    @Value("${irrigation.scheduler.threadPoolSize:1}")
    private Integer schedulerThreadPoolSize;

    @Value("${irrigation.scheduler.initialDelay:60}")
    private Integer schedulerInitialDelay;

    @Value("${irrigation.scheduler.period:300}")
    private Integer schedulerPeriod;

    @Value("${irrigation.scheduler.timeUnit:SECONDS}")
    private String schedulerTimeUnit;

    @Value("${irrigation.scheduler.retry:5}")
    private Integer schedulerRetry;

    public Integer getSchedulerThreadPoolSize() {
        return schedulerThreadPoolSize;
    }

    public Integer getSchedulerInitialDelay() {
        return schedulerInitialDelay;
    }

    public Integer getSchedulerPeriod() {
        return schedulerPeriod;
    }

    public TimeUnit getSchedulerTimeUnit() {
        try {
            return TimeUnit.valueOf(schedulerTimeUnit);
        } catch (IllegalArgumentException exception) {
            logger.error("Illegal argument of the scheduler timeUnit filed. Assigning default value : SECONDS", exception);
            return TimeUnit.SECONDS;
        }
    }

    public Integer getSchedulerRetry() {
        return schedulerRetry;
    }
}
