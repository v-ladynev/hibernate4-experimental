package com.github.experimental.strategy.persistent;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Book {

    @Id
    private Long pid;

}