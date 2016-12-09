package com.workshop.service;

import com.workshop.controller.AccountController;
import com.workshop.controller.SessionController;
import com.workshop.entity.Account;
import com.workshop.entity.Session;
import com.workshop.repository.SessionRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(Session session, Account account1) {
        session.setUser1(account1);
        session.setStatus(Session.SessionStatus.CREATED);

        return sessionRepository.save(session);
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    public Resource<Session> createResource(Session session) {
        Resource<Session> resource = new Resource<>(session);

        if (Objects.nonNull(session.getId())) {
            resource.add(linkTo(SessionController.class).slash("sessions").slash(session.getId()).withRel("session"));
        }

        if (Objects.nonNull(session.getUser1())) {
            resource.add(linkTo(AccountController.class).slash("accounts").slash(session.getUser1().getId()).withRel("user1"));
        }

        if (Objects.nonNull(session.getUser2())) {
            resource.add(linkTo(AccountController.class).slash("accounts").slash(session.getUser2().getId()).withRel("user2"));
        }

        return resource;
    }

    public Resources createResource(List<Session> sessions) {
        List<Resource<Session>> resourcesList = sessions.stream()
                .map(this::createResource)
                .collect(Collectors.toList());

        Link selfRel = ControllerLinkBuilder
                .linkTo(SessionController.class)
                .slash("sessions")
                .withSelfRel();

        return new Resources<>(resourcesList, selfRel);
    }
}
