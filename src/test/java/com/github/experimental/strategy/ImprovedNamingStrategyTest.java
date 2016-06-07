package com.github.experimental.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImprovedNamingStrategyTest {

    private static final Class<?>[] PERISTENTS = new Class<?>[] { AuthorTable.class,
            BookTable.class };

    private static Configuration configuration;

    @BeforeClass
    public static void setUp() {
        configuration = StrategyTestUtils.createConfiguration(ImprovedNamingStrategy.INSTANCE,
                PERISTENTS);
        StrategyTestUtils.logSchemaUpdate(configuration);
    }

    @Test
    public void classToTableName() {
        Table authorTable = getTable(AuthorTable.class);
        assertThat(authorTable.getName()).isEqualTo("improved_naming_strategy_test$author_table");

        Table bookTable = getTable(BookTable.class);
        assertThat(bookTable.getName()).isEqualTo("improved_naming_strategy_test$book_table");
    }

    @Test
    public void propertyToColumnName() {
        assertThat(getAuthorColumn("authorPid")).isEqualTo("author_pid");
    }

    private static Table getTable(Class<?> persistent) {
        PersistentClass result = configuration.getClassMapping(persistent.getName());
        assertThat(result).isNotNull();
        return result.getTable();
    }

    private static String getAuthorColumn(String propertyName) {
        PersistentClass binding = configuration.getClassMapping(AuthorTable.class.getName());
        assertThat(binding).isNotNull();
        Column result = StrategyTestUtils.getColumn(binding, propertyName);
        assertThat(result).isNotNull();
        return result.getName();
    }

    @Entity
    public static class AuthorTable {

        @Id
        private Integer authorPid;

        @ManyToMany
        private List<BookTable> booksOne;

        @ManyToMany
        private List<BookTable> booksTwo;

    }

    @Entity
    public static class BookTable {

        @Id
        private Integer bookPid;

    }

}
