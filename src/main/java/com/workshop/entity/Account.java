package com.workshop.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"authorities"})
@ToString(exclude = {"authorities"})
@Table(name = "accounts", schema = "jcombat")
public class Account implements Serializable {
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

    @OneToMany
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Set<Authority> authorities;
}
