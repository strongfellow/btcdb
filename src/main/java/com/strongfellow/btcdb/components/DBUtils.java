package com.strongfellow.btcdb.components;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class DBUtils {

    public static DataSource getSqliteDataSource(String path) {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        String url = StringUtils.join(new String[] {"jdbc", "sqlite", path}, ':');
        dataSource.setUrl(url);
        dataSource.setAutoCommit(false);
        return dataSource;
    }

}
