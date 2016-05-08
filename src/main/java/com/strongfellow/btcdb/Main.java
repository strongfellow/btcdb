package com.strongfellow.btcdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        logger.info("hi there");
        Connection c = DriverManager.getConnection("jdbc:sqlite:test.db");
        logger.info("connected, yeah baby");
    }
}
