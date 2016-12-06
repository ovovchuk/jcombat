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
@Table(name = "questions", schema = "jcombat")
public class Question {
    @Id
    @GeneratedValue(generator = "uuid", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(columnDefinition = "CHAR(36)")
    private String id;
    private String question;

    @OneToMany
    @OrderBy("answerPos")
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Set<Answer> answers;

    private Instant dateCreated;
    private Instant dateModified;

    @OneToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private Account createdBy;

    @OneToOne
    @JoinColumn(name = "modified_by", referencedColumnName = "id")
    private Account modifiedBy;

    private Boolean isActive;
}
