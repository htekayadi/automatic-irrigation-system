package com.automatic.irrigation.dto;

import lombok.Data;

@Data
public class EmailDTO {
    
    private String to;
    private String from;
    private String fromName;
    private String subject;
    private String body;
    private boolean html;
    
}
