package com.workshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant dateCreated = Instant.now();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant dateModified = Instant.now();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "question_id", columnDefinition = "CHAR(36)")
    private String questionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private Account createdBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToOne
    @JoinColumn(name = "modified_by", referencedColumnName = "id")
    private Account modifiedBy;
}
