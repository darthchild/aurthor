package com.darthchild.aurthor.books;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;
    private Double price;

}
