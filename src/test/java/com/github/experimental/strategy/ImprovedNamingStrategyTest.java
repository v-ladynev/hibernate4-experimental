package com.github.experimental.strategy;

import static com.github.experimental.strategy.StrategyTestUtils.createConfiguration;
import static com.github.experimental.strategy.StrategyTestUtils.getCollectionTable;
import static com.github.experimental.strategy.StrategyTestUtils.getColumNames;
import static com.github.experimental.strategy.StrategyTestUtils.getColumnName;
import static com.github.experimental.strategy.StrategyTestUtils.getForeignKeyConstraintNames;
import static com.github.experimental.strategy.StrategyTestUtils.getIndexNames;
import static com.github.experimental.strategy.StrategyTestUtils.getTable;
import static com.github.experimental.strategy.StrategyTestUtils.getUniqueConstraintNames;
import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.experimental.strategy.persistent.AuthorTable;
import com.github.experimental.strategy.persistent.Book;
import com.github.experimental.strategy.persistent.Customer;
import com.github.experimental.strategy.persistent.ValuedCustomer;

public class ImprovedNamingStrategyTest {

    private static final Class<?>[] ENTITIES = new Class<?>[] { AuthorTable.class, Book.class,
            Customer.class, ValuedCustomer.class };

    private static Configuration configuration;

    @BeforeClass
    public static void setUp() {
        configuration = createConfiguration(ImprovedNamingStrategy.INSTANCE, ENTITIES);
        StrategyTestUtils.logSchemaUpdate(configuration);
    }

    @Test
    public void classToTableName() {
        Table authorTable = getTable(configuration, AuthorTable.class);
        assertThat(authorTable.getName()).isEqualTo("author_table");

        Table bookTable = getTable(configuration, Book.class);
        assertThat(bookTable.getName()).isEqualTo("book");
    }

    @Test
    public void propertyToColumnName() {
        Table authorTable = getTable(configuration, AuthorTable.class);
        assertThat(getColumNames(authorTable)).containsOnly("author_pid", "author_info",
                "unique_field", "best_book", "author_type", "author_version");

        String bookPid = getColumnName(configuration, Book.class, "pid");
        assertThat(bookPid).isEqualTo("pid");
    }

    @Test
    public void collectionTableName() {
        Table booksOne = getCollectionTable(configuration, AuthorTable.class, "booksOne");
        assertThat(booksOne.getName()).isEqualTo("author_table_books_one");

        Table booksTwo = getCollectionTable(configuration, AuthorTable.class, "booksTwo");
        assertThat(booksTwo.getName()).isEqualTo("author_table_books_two");
    }

    @Test
    public void joinTableColumnNames() {
        Table booksOne = getCollectionTable(configuration, AuthorTable.class, "booksOne");
        assertThat(getColumNames(booksOne)).containsOnly("author_table", "books_one");

        Table booksTwo = getCollectionTable(configuration, AuthorTable.class, "booksTwo");
        assertThat(getColumNames(booksTwo)).containsOnly("author_table", "books_two");
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

        assertThat(bookTitles.getName()).isEqualTo("author_table_book_titles");

        assertThat(getColumNames(bookTitles)).containsOnly("author_table", "book_titles");
    }

    @Test
    public void elementCollectionEmbedded() {
        Table elementCollectionAuthorInfo = getCollectionTable(configuration, AuthorTable.class,
                "elementCollectionAuthorInfo");

        assertThat(elementCollectionAuthorInfo.getName())
                .isEqualTo("author_table_element_collection_author_info");

        assertThat(getColumNames(elementCollectionAuthorInfo)).containsOnly("author_table",
                "best_book", "author_info");
    }

    @Test
    public void discriminatorColumn() {
        Table customerTable = getTable(configuration, Customer.class);
        assertThat(getColumNames(customerTable)).containsOnly("pid", "dtype");
    }

    @Test
    public void orderColumn() {
        Table booksFour = getCollectionTable(configuration, AuthorTable.class, "booksOrdered");

        assertThat(booksFour.getName()).isEqualTo("author_table_books_ordered");

        assertThat(getColumNames(booksFour)).containsOnly("author_table", "books_ordered",
                "books_ordered_order");
    }

    @Test
    public void mapKey() {
        Table booksMap = getCollectionTable(configuration, AuthorTable.class, "booksMap");

        assertThat(booksMap.getName()).isEqualTo("author_table_books_map");

        assertThat(getColumNames(booksMap)).containsOnly("author_table", "books_map",
                "books_map_key");
    }

    @Test
    public void foreignKeyConstrain() {
        Table bookTable = getTable(configuration, Book.class);
        assertThat(getForeignKeyConstraintNames(bookTable))
                .containsOnly("FK_rtsneql14vg7ay4erx1r32ddx");
    }

    @Test
    public void uniqueConstrain() {
        Table authorTable = getTable(configuration, AuthorTable.class);
        assertThat(getUniqueConstraintNames(authorTable))
                .containsOnly("UK_6hlrw9c61t7i0x23bij5cd6jc");
    }

    @Test
    public void index() {
        Table authorTable = getTable(configuration, AuthorTable.class);
        assertThat(getIndexNames(authorTable)).containsOnly("UK_adyveyc8inhw5t0pkq279r0w4");
    }

}
