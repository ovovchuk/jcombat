package com.workshop.controller;

import com.workshop.dto.PayloadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Slf4j
@RestController
public class SessionController {
    private final static String WS_DESTINATION_FORMAT = "/session/%s";
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> monitors = new HashMap<>();

    public SessionController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/combat")
    public void greet(PayloadDTO payloadDTO) throws Exception {
        synchronized (getMonitor(payloadDTO.getSessionId())) {
            log.info(payloadDTO.toString());

            Thread.sleep(10000);
            String destination = String.format(WS_DESTINATION_FORMAT, payloadDTO.getSessionId());
            this.messagingTemplate.convertAndSend(destination,"Hello session " + payloadDTO.getSessionId());
        }
    }

    private String getMonitor(String sessionId) {
        if (monitors.containsKey(sessionId)) {
            return monitors.get(sessionId);
        }

        monitors.put(sessionId, sessionId);

        return sessionId;
    }
}
