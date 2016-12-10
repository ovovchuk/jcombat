package com.workshop.repository;

import com.workshop.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    public Page<Session> findAllByStatus(Session.SessionStatus sessionStatus, Pageable pageable);
}
