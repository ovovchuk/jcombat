package com.workshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authorities", schema = "jcombat")
public class Authority implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    @JsonIgnore
    private Account account;
    private String authority;
}
