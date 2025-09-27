package com.darthchild.aurthor.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(
        name = "authorities",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "authority"})}
)
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User user;

    @Column(length = 50, nullable = false)
    private String authority;

}

