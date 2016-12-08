package com.workshop.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;


@RestController
public class SessionController {

    @MessageMapping("/hello")
    @SendTo("/sessions/greet")
    public Map greet(Map t) throws Exception {
        return Collections.singletonMap("key", "test");
    }
}
