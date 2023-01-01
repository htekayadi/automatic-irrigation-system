package com.automatic.irrigation;

import com.automatic.irrigation.scheduler.IrrigationEventScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class AutomaticIrrigationApplication {

    @Autowired
    private IrrigationEventScheduler irrigationEventScheduler;

    public static void main(String[] args) {
        SpringApplication.run(AutomaticIrrigationApplication.class, args);
    }

    @PostConstruct
    private void startEventScheduler() {
        if (irrigationEventScheduler != null) irrigationEventScheduler.start();
    }
}

