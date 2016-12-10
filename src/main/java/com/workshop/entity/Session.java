package com.workshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Locale;
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

    @NotNull
    private String name;

    @OneToOne
    @JoinColumn(name = "user_id1", referencedColumnName = "id")
    private Account user1;

    @OneToOne
    @JoinColumn(name = "user_id2", referencedColumnName = "id")
    private Account user2;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant dateCreated = Instant.now();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant dateEnd = Instant.now();

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @OneToMany
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private Set<SessionItem> items;

    public enum SessionStatus {
        CREATED, STARTED, CLOSED;

        public static SessionStatus fromString(String status) {
            try {
                return SessionStatus.valueOf(status.toUpperCase(Locale.US));
            } catch (IllegalArgumentException e) {
                String msg = "Session status should be one of CREATED, STARTED, CLOSED (case insensitive)";
                throw new IllegalArgumentException(msg, e);
            }
        }
    }
}

