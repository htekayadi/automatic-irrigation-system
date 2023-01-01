package com.automatic.irrigation.service.impl;

import com.automatic.irrigation.dto.EmailDTO;
import com.automatic.irrigation.service.AlertService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AlertServiceImpl implements AlertService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public void sendAlert(String sensorId) {
        log.info("Sensor device {} is not available", sensorId);

        String message = String.format("Sensor Device %s is not available", sensorId);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(adminEmail);
        emailDTO.setSubject("Sensor Device Not Available Alert");
        emailDTO.setBody(message);

        sendMail(emailDTO);
    }

    private void sendMail(EmailDTO emailDTO) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(emailDTO.getTo());
        msg.setSubject(emailDTO.getSubject());
        msg.setText(emailDTO.getBody());

        javaMailSender.send(msg);
    }
}
