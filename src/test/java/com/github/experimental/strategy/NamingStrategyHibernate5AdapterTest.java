package com.github.experimental.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.experimental.strategy.persistent.BookTable;

public class NamingStrategyHibernate5AdapterTest {

    private static final Class<?>[] PERISTENTS = new Class<?>[] { AuthorTable.class,
            BookTable.class };

    private static Configuration configuration;

    @BeforeClass
    public static void setUp() {
        configuration = StrategyTestUtils.createConfiguration(ImprovedNamingStrategy.INSTANCE,
                PERISTENTS);
    }

    @Test
    public void classToTableName() {
        Table table = getAuthorTable();
        assertThat(table.getName()).isEqualTo("author_table");
    }

    @Test
    public void propertyToColumnName() {
        assertThat(getAuthorColumn("authorPid")).isEqualTo("author_pid");
    }

    private static Table getAuthorTable() {
        PersistentClass binding = configuration.getClassMapping(AuthorTable.class.getName());

        System.out.println(AuthorTable.class.getCanonicalName());

        assertThat(binding).isNotNull();
        return binding.getTable();
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

        @OneToMany
        @JoinColumn
        private List<BookTable> books;

    }

}
