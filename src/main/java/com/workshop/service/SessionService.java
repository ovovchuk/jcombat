package com.workshop.service;

import com.workshop.controller.AccountController;
import com.workshop.controller.SessionController;
import com.workshop.entity.Account;
import com.workshop.entity.Session;
import com.workshop.exception.SessionNotFoundException;
import com.workshop.repository.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(Session session, Account account) {
        session.setUser1(account);
        session.setStatus(Session.SessionStatus.CREATED);

        return sessionRepository.save(session);
    }

    public Session joinSession(Session session) {
        session.setStatus(Session.SessionStatus.STARTED);

        if (session.getUser1().equals(session.getUser2())) {
            throw new IllegalArgumentException("Joining to own session is not allowed");
        }

        return sessionRepository.save(session);
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    public Page<Session> findAll(Pageable pageable) {
        return sessionRepository.findAll(pageable);
    }

    public Page<Session> findAllByStatus(Session.SessionStatus sessionStatus, Pageable pageable) {
        return sessionRepository.findAllByStatus(sessionStatus, pageable);
    }

    public Session findOne(String id) {
        return Optional.ofNullable(sessionRepository.findOne(id))
                .orElseThrow(() -> {
                    String msg = String.format("Session with id %s not found", id);
                    return new SessionNotFoundException(msg);
                });
    }

    public Resource<Session> convertToResource(Session session) {
        Resource<Session> resource = new Resource<>(session);

        if (Objects.nonNull(session.getId())) {
            resource.add(linkTo(SessionController.class).slash("sessions").slash(session.getId()).withRel("session"));
        }

        if (Objects.nonNull(session.getUser1())) {
            resource.add(linkTo(SessionController.class)
                    .slash("sessions")
                    .slash(session.getId())
                    .slash("user1")
                    .withRel("user1"));
        }

        if (Objects.nonNull(session.getUser2())) {
            resource.add(linkTo(SessionController.class)
                    .slash("sessions")
                    .slash(session.getId())
                    .slash("user2")
                    .withRel("user2"));
        }

        return resource;
    }

    public Resources<Resource<Session>> convertToResource(Page<Session> sessions, int page) {
        List<Resource<Session>> resources = sessions.getContent().stream()
                .map(this::convertToResource)
                .collect(Collectors.toList());

        PagedResources.PageMetadata pageMetadata =
                new PagedResources.PageMetadata(sessions.getSize(), page,
                        sessions.getTotalElements(), sessions.getTotalPages());

        return new PagedResources<>(resources, pageMetadata);
    }
}
