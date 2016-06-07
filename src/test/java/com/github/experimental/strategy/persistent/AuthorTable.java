package com.github.experimental.strategy.persistent;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

//@Entity
public class AuthorTable {

    @Id
    private Integer authorPid;

    @OneToMany
    @JoinColumn
    private List<BookTable> books;

}
