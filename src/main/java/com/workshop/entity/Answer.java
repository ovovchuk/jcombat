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
@Table(name = "answers", schema = "jcombat")
public class Answer {
    @Id
    @GeneratedValue(generator = "uuid", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(columnDefinition = "CHAR(36)")
    private String id;
    private Character answerPos;
    private String answer;
    private Boolean isCorrect;
    private Instant dateCreated;
    private Instant dateModified;

    @OneToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private Account createdBy;

    @OneToOne
    @JoinColumn(name = "modified_by", referencedColumnName = "id")
    private Account modifiedBy;
}
