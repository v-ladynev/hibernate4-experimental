package com.github.experimental.strategy;

import java.io.Serializable;
import java.util.Locale;

import org.hibernate.AssertionFailure;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.internal.util.StringHelper;

/**
 * An improved naming strategy that prefers embedded underscores to mixed case names
 *
 * @see DefaultNamingStrategy the default strategy
 * @author Gavin King
 */
public class ImprovedNamingStrategy implements NamingStrategy, Serializable {

    /**
     * A convenient singleton instance
     */
    public static final NamingStrategy INSTANCE = new ImprovedNamingStrategy();

    /**
     * Return the unqualified class name, mixed case converted to underscores
     */
    @Override
    public String classToTableName(String className) {
        return addUnderscores(StringHelper.unqualify(className));
    }

    /**
     * Return the full property path with underscore seperators, mixed case converted to underscores
     */
    @Override
    public String propertyToColumnName(String propertyName) {
        return addUnderscores(StringHelper.unqualify(propertyName));
    }

    /**
     * Convert mixed case to underscores
     */
    @Override
    public String tableName(String tableName) {
        return addUnderscores(tableName);
    }

    /**
     * Convert mixed case to underscores
     */
    @Override
    public String columnName(String columnName) {
        System.out.println(columnName);
        return addUnderscores(columnName);
    }

    protected static String addUnderscores(String name) {
        StringBuilder buf = new StringBuilder(name.replace('.', '_'));
        for (int i = 1; i < buf.length() - 1; i++) {
            if (Character.isLowerCase(buf.charAt(i - 1)) && Character.isUpperCase(buf.charAt(i))
                    && Character.isLowerCase(buf.charAt(i + 1))) {
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable,
            String associatedEntity, String associatedEntityTable, String propertyName) {
        /*
        System.out.println(String.format(
                "collectionTableName: ownerEntity: %s, "
                        + "ownerEntityTable: %s, associatedEntity: %s, associatedEntityTable: %s, "
                        + "propertyName: %s",
                ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable,
                propertyName));
        */
        return tableName(ownerEntityTable + '_' + propertyToColumnName(propertyName));
    }

    /**
     * Return the argument
     */
    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        /*System.out.println(String.format("joinKeyColumnName: joinedColumn: %s, joinedTable: %s",
                joinedColumn, joinedTable));*/
        return columnName(joinedColumn);
    }

    /**
     * Return the property name or propertyTableName
     */
    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName,
            String propertyTableName, String referencedColumnName) {
        /*
        System.out.println(String.format(
                "foreignKeyColumnName:  propertyName: %s\n propertyEntityName: %s\n propertyTableName: %s\n referencedColumnName: %s\n",
                propertyName, propertyEntityName, propertyTableName, referencedColumnName));
        */
        String header = propertyName != null ? StringHelper.unqualify(propertyName)
                : propertyTableName;
        if (header == null) {
            throw new AssertionFailure("NamingStrategy not properly filled");
        }
        return columnName(header); // + "_" + referencedColumnName not used for backward
                                   // compatibility
    }

    /**
     * Return the column name or the unqualified property name
     */
    @Override
    public String logicalColumnName(String columnName, String propertyName) {

        String result = StringHelper.isNotEmpty(columnName) ? columnName
                : StringHelper.unqualify(propertyName);

        // System.out.println("logicalColumnName: " + propertyName + " = " + result);

        return result;
    }

    /**
     * Returns either the table name if explicit or if there is an associated table, the
     * concatenation of owner entity table and associated table otherwise the concatenation of owner
     * entity table and the unqualified property name
     */
    @Override
    public String logicalCollectionTableName(String tableName, String ownerEntityTable,
            String associatedEntityTable, String propertyName) {

        // System.out.println("logicalCollectionTableName: " + tableName + " " + propertyName);

        if (tableName != null) {
            return tableName;
        } else {
            // use of a stringbuffer to workaround a JDK bug
            return new StringBuffer(ownerEntityTable).append("_")
                    .append(associatedEntityTable != null ? associatedEntityTable
                            : StringHelper.unqualify(propertyName))
                    .toString();
        }
    }

    /**
     * Return the column name if explicit or the concatenation of the property name and the
     * referenced column
     */
    @Override
    public String logicalCollectionColumnName(String columnName, String propertyName,
            String referencedColumn) {

        // System.out.println("logicalCollectionColumnName");

        return StringHelper.isNotEmpty(columnName) ? columnName
                : StringHelper.unqualify(propertyName) + "_" + referencedColumn;
    }
}
