package com.workshop.dto;

import lombok.Data;

@Data
public class PayloadDTO {
    private String username;
    private String sessionId;
    private String questionId;
    private String answerId;
}
