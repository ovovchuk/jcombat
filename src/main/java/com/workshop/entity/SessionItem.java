package com.workshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session_items", catalog = "jcombat")
public class SessionItem {
    @Id
    @GeneratedValue(generator = "uuid", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(columnDefinition = "CHAR(36)")
    private String id;

    private Byte userHealth1;
    private Byte userHealth2;

    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Question question;

    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Answer answer;

    @OneToOne
    @JoinColumn(name = "answered_by", referencedColumnName = "id")
    private Account answeredBy;
    private Instant dateCreated;
}
