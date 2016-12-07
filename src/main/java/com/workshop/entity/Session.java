package com.workshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(generator = "uuid", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(columnDefinition = "CHAR(36)")
    private String id;

    private String name;

    @OneToOne
    @JoinColumn(name = "user_id1", referencedColumnName = "id")
    private Account user1;

    @OneToOne
    @JoinColumn(name = "user_id2", referencedColumnName = "id")
    private Account user2;

    private Instant dateCreated;
    private Instant dateEnd;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @OneToMany
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private Set<SessionItem> items;

    enum SessionStatus {
        CREATED, STARTED, CLOSED
    }
}

