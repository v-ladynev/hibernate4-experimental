package com.github.experimental.strategy;

import static com.github.experimental.strategy.StrategyTestUtils.createConfiguration;
import static com.github.experimental.strategy.StrategyTestUtils.getCollectionTable;
import static com.github.experimental.strategy.StrategyTestUtils.getColumNames;
import static com.github.experimental.strategy.StrategyTestUtils.getColumnName;
import static com.github.experimental.strategy.StrategyTestUtils.getTable;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImprovedNamingStrategyTest {

    private static final Class<?>[] PERISTENTS = new Class<?>[] { AuthorTable.class, Book.class };

    private static Configuration configuration;

    @BeforeClass
    public static void setUp() {
        configuration = createConfiguration(ImprovedNamingStrategy.INSTANCE, PERISTENTS);
        StrategyTestUtils.logSchemaUpdate(configuration);
    }

    @Test
    public void classToTableName() {
        Table authorTable = getTable(configuration, AuthorTable.class);
        assertThat(authorTable.getName()).isEqualTo("improved_naming_strategy_test$author_table");

        Table bookTable = getTable(configuration, Book.class);
        assertThat(bookTable.getName()).isEqualTo("improved_naming_strategy_test$book");
    }

    @Test
    public void propertyToColumnName() {
        Table authorTable = getTable(configuration, AuthorTable.class);
        assertThat(getColumNames(authorTable)).containsOnly("author_pid", "info", "best_book",
                "type", "version");

        String bookPid = getColumnName(configuration, Book.class, "pid");
        assertThat(bookPid).isEqualTo("pid");
    }

    @Test
    public void collectionTableName() {
        Table booksOne = getCollectionTable(configuration, AuthorTable.class, "booksOne");
        assertThat(booksOne.getName())
                .isEqualTo("improved_naming_strategy_test$author_table_books_one");

        Table booksTwo = getCollectionTable(configuration, AuthorTable.class, "booksTwo");
        assertThat(booksTwo.getName())
                .isEqualTo("improved_naming_strategy_test$author_table_books_two");
    }

    @Test
    public void joinTableColumnNames() {
        Table booksOne = getCollectionTable(configuration, AuthorTable.class, "booksOne");
        assertThat(getColumNames(booksOne))
                .containsOnly("improved_naming_strategy_test$author_table", "books_one");

        Table booksTwo = getCollectionTable(configuration, AuthorTable.class, "booksTwo");
        assertThat(getColumNames(booksTwo))
                .containsOnly("improved_naming_strategy_test$author_table", "books_two");
    }

    @Test
    public void foreignKeyColumnName() {
        Table booksOne = getCollectionTable(configuration, AuthorTable.class, "booksOne");
        assertThat(getColumNames(booksOne)).contains("books_one");

        Table booksTwo = getCollectionTable(configuration, AuthorTable.class, "booksTwo");
        assertThat(getColumNames(booksTwo)).contains("books_two");

        Table bookTable = getTable(configuration, Book.class);
        assertThat(getColumNames(bookTable)).contains("books_three");
    }

    @Test
    public void elementCollection() {
        Table bookTitles = getCollectionTable(configuration, AuthorTable.class, "bookTitles");

        assertThat(bookTitles.getName())
                .isEqualTo("improved_naming_strategy_test$author_table_book_titles");

        assertThat(getColumNames(bookTitles))
                .containsOnly("improved_naming_strategy_test$author_table", "book_titles");
    }

    @Test
    public void elementCollectionEmbedded() {
        Table elementCollectionAuthorInfo = getCollectionTable(configuration, AuthorTable.class,
                "elementCollectionAuthorInfo");

        assertThat(elementCollectionAuthorInfo.getName()).isEqualTo(
                "improved_naming_strategy_test$author_table_element_collection_author_info");

        assertThat(getColumNames(elementCollectionAuthorInfo))
                .containsOnly("improved_naming_strategy_test$author_table", "best_book", "info");
    }

    @Entity
    public static class AuthorTable {

        @Id
        private Long authorPid;

        @Embedded
        private AuthorInfo authorInfo;

        @ManyToMany
        private List<Book> booksOne;

        @ManyToMany
        private List<Book> booksTwo;

        @OneToMany
        @JoinColumn
        private List<Book> booksThree;

        @ElementCollection
        private List<String> bookTitles;

        @Enumerated
        private AuthorType type;

        @Version
        private Integer version;

        @ElementCollection
        @Embedded
        private List<AuthorInfo> elementCollectionAuthorInfo;

    }

    @Entity
    public static class Book {

        @Id
        private Long pid;

    }

    @Embeddable
    public static class AuthorInfo {

        @Column
        private String info;

        @OneToOne
        private Book bestBook;

    }

    public enum AuthorType {
        FAMOUS, NOT_FAMOUS
    }

}
