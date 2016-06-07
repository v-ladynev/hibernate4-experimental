package com.github.experimental.strategy.persistent;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BookTable {

    @Id
    private Integer bookPid;

}
