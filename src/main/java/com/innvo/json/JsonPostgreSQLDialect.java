package com.innvo.json;


import java.sql.Types;

import org.hibernate.dialect.PostgreSQL9Dialect;

/**
 * Wrap default PostgreSQL9Dialect with 'json' type.
 *
 * @author ali
 */
public class JsonPostgreSQLDialect extends PostgreSQL9Dialect {

    public JsonPostgreSQLDialect() {

        super();

        this.registerColumnType(Types.JAVA_OBJECT, "json");
    }
}