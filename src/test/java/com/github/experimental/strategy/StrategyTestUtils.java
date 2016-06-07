package com.github.experimental.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;

import com.github.fluent.hibernate.internal.util.InternalUtils;

/**
 *
 * @author V.Ladynev
 */
public final class StrategyTestUtils {

    private StrategyTestUtils() {

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

    public static List<String> getColumNames(Iterator<?> columnIterator) {
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

    public static Configuration createConfiguration(NamingStrategy strategy,
            Class<?>... annotatedClasses) {
        Configuration result = new Configuration();

        for (Class<?> annotatedClass : annotatedClasses) {
            result.addAnnotatedClass(annotatedClass);
        }

        if (strategy != null) {
            result.setNamingStrategy(strategy);
        }

        result.buildMapping();

        return result;
    }

}
