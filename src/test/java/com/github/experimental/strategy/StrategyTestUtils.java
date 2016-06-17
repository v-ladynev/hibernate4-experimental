package com.github.experimental.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.Target;

import com.github.fluent.hibernate.internal.util.InternalUtils;

/**
 *
 * @author V.Ladynev
 */
public final class StrategyTestUtils {

    private StrategyTestUtils() {

    }

    public static String getColumnName(Configuration configuration, Class<?> persistent,
            String propertyName) {
        PersistentClass binding = configuration.getClassMapping(persistent.getName());
        assertThat(binding).isNotNull();
        Column result = StrategyTestUtils.getColumn(binding, propertyName);
        assertThat(result).isNotNull();
        return result.getName();
    }

    public static Column getColumn(PersistentClass persistentClass, String propertyName) {
        Property property = persistentClass.getProperty(propertyName);
        assertThat(property).isNotNull();
        return (Column) property.getColumnIterator().next();
    }

    public static List<String> getComponentColumnNames(PersistentClass persistentClass,
            String componentPropertyName) {
        Property componentBinding = persistentClass.getProperty(componentPropertyName);
        assertThat(componentBinding).isNotNull();

        Component component = (Component) componentBinding.getValue();
        return getColumNames(component.getColumnIterator());
    }

    public static List<String> getColumNames(Table table) {
        return getColumNames(table.getColumnIterator());
    }

    private static List<String> getColumNames(Iterator<?> columnIterator) {
        ArrayList<String> result = InternalUtils.CollectionUtils.newArrayList();

        while (columnIterator.hasNext()) {
            Column column = (Column) columnIterator.next();
            result.add(column.getQuotedName());
        }

        return result;
    }

    public static List<String> getUniqueConstraintNames(Table table) {
        ArrayList<String> result = InternalUtils.CollectionUtils.newArrayList();

        Iterator<UniqueKey> iterator = table.getUniqueKeyIterator();

        while (iterator.hasNext()) {
            UniqueKey uniqueKey = iterator.next();
            result.add(uniqueKey.getName());
        }

        return result;
    }

    public static Table getTable(Configuration configuration, Class<?> persistent) {
        PersistentClass result = configuration.getClassMapping(persistent.getName());
        assertThat(result).isNotNull();
        return result.getTable();
    }

    public static Table getCollectionTable(Configuration configuration, Class<?> persistent,
            String propertyName) {
        Collection result = configuration
                .getCollectionMapping(persistent.getName() + "." + propertyName);
        assertThat(result).isNotNull();
        return result.getCollectionTable();
    }

    public static Configuration createConfiguration(NamingStrategy strategy,
            Class<?>... annotatedClasses) {
        Configuration result = new Configuration();

        for (Class<?> annotatedClass : annotatedClasses) {
            result.addAnnotatedClass(annotatedClass);
        }

        if (strategy != null) {
            result.setNamingStrategy(strategy);
        }

        result.buildMappings();

        return result;
    }

    public static void logSchemaUpdate(Configuration configuration) {
        SchemaUpdate schemaUpdate = new SchemaUpdate(configuration);
        schemaUpdate.setDelimiter(";");
        schemaUpdate.setFormat(true);
        schemaUpdate.execute(Target.SCRIPT);
    }

}
