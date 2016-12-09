package com.workshop.controller;

import com.workshop.dto.PayloadDTO;
import com.workshop.entity.Account;
import com.workshop.entity.Session;
import com.workshop.exception.AccountNotFoundException;
import com.workshop.repository.AccountRepository;
import com.workshop.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
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
    private final AccountRepository accountRepository;
    private final SessionService sessionService;

    public SessionController(SimpMessagingTemplate messagingTemplate,
                             AccountRepository accountRepository,
                             SessionService sessionService) {
        this.messagingTemplate = messagingTemplate;
        this.accountRepository = accountRepository;
        this.sessionService = sessionService;
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
    public PagedResources<?> getSessions(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                         @RequestParam(value = "size", required = false, defaultValue = "15") int size,
                                         @RequestParam(value = "sort", required = false, defaultValue = "dateCreated") String[] sort) {
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sort);

        Page<Session> sessions = sessionService.findAll(pageRequest);

        return sessionService.createPagedResource(sessions, page);
    }

    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    public Resource<Session> saveSession(@Valid @RequestBody Session session, Principal principal) {
        Session savedSession = sessionService.createSession(session, getAccountFromPrincipal(principal));

        Resource<Session> resource = sessionService.createResource(savedSession);
        resource.add(linkTo(methodOn(SessionController.class).saveSession(session, principal)).withSelfRel());

        return resource;
    }

    private Account getAccountFromPrincipal(Principal principal) {
        return accountRepository.findByUsername(principal.getName()).orElseThrow(() -> {
            String msg = String.format("Account with username %s not found", principal.getName());

            return new AccountNotFoundException(msg);
        });
    }

    private String getMonitor(String sessionId) {
        if (monitors.containsKey(sessionId)) {
            return monitors.get(sessionId);
        }

        monitors.put(sessionId, sessionId);

        return sessionId;
    }
}
