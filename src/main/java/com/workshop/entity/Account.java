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
@Table(name = "accounts", schema = "jcombat")
public class Account {
    @Id
    @GeneratedValue(generator = "uuid", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(columnDefinition = "CHAR(36)")
    private String id;

    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private Instant dateCreated;
    private Instant dateModified;
    private Boolean enabled;

    @OneToMany(mappedBy = "account")
    private Set<Authority> authorities;
}
