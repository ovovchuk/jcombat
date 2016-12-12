package com.workshop.controller;

import com.workshop.dto.PayloadDTO;
import com.workshop.entity.Account;
import com.workshop.entity.Session;
import com.workshop.service.AccountService;
import com.workshop.service.QuestionService;
import com.workshop.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@Slf4j
@RestController
public class SessionController {
    private final static String WS_DESTINATION_FORMAT = "/session/%s";
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> monitors = new HashMap<>();
    private final SessionService sessionService;
    private final AccountService accountService;
    private final QuestionService questionService;

    public SessionController(SimpMessagingTemplate messagingTemplate,
                             SessionService sessionService,
                             AccountService accountService,
                             QuestionService questionService) {
        this.messagingTemplate = messagingTemplate;
        this.sessionService = sessionService;
        this.accountService = accountService;
        this.questionService = questionService;
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

    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public Resources<?> getSessions(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "dateCreated") String[] sort) {
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sort);

        Page<Session> sessions = sessionService.findAll(pageRequest);

        Resources<Resource<Session>> resources = sessionService.convertToResource(sessions, page);
        resources.add(linkTo(methodOn(SessionController.class).getSessions(page, size, sort)).withSelfRel());

        return resources;
    }

    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    public Resource<Session> saveSession(@Valid @RequestBody Session session, Principal principal) {
        Account account = accountService.findByUsername(principal.getName());
        Session savedSession = sessionService.createSession(session, account);

        Resource<Session> resource = sessionService.convertToResource(savedSession);
        resource.add(linkTo(methodOn(SessionController.class).saveSession(session, principal)).withSelfRel());

        return resource;
    }

    @RequestMapping(value = "/sessions/{id}", method = RequestMethod.GET)
    public Resource<Session> getById(@PathVariable String id) {
        Session session = sessionService.findOne(id);

        return sessionService.convertToResource(session);
    }

    @RequestMapping(value = "/sessions/status/{status:created|started|closed}", method = RequestMethod.GET)
    public Resources<Resource<Session>> getByStatus(
            @PathVariable String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "dateCreated") String[] sort) {
        Session.SessionStatus sessionStatus = Session.SessionStatus.fromString(status);
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sort);
        Page<Session> sessions = sessionService.findAllByStatus(sessionStatus, pageRequest);

        Resources<Resource<Session>> resources = sessionService.convertToResource(sessions, page);
        resources.add(linkTo(methodOn(SessionController.class).getByStatus(status, page, size, sort)).withSelfRel());

        return resources;
    }

    @RequestMapping(value = "/sessions/{id}/{user:user[12]}", method = RequestMethod.GET)
    public Resource<Account> getSessionUser(@PathVariable String id, @PathVariable String user) {
        Session session = sessionService.findOne(id);

        if (user.equals("user1")) {
            return accountService.convertToResource(session.getUser1());
        } else {
            return accountService.convertToResource(session.getUser2());
        }
    }

    @RequestMapping(value = "/sessions/{id}/join", method = RequestMethod.PUT)
    public Resource<Session> joinSession(@RequestBody Session session) {
        return sessionService.convertToResource(sessionService.joinSession(session));
    }

    private String getMonitor(String sessionId) {
        if (monitors.containsKey(sessionId)) {
            return monitors.get(sessionId);
        }

        monitors.put(sessionId, sessionId);

        return sessionId;
    }
}
