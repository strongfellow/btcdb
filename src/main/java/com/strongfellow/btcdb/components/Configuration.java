package com.strongfellow.btcdb.components;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.strongfellow.btcdb.logic.BlockReader;
import com.strongfellow.btcdb.logic.BlockWriter;
import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

@org.springframework.context.annotation.Configuration
@EnableTransactionManagement
@EnableMetrics
public class Configuration {

    private static final MediaType BLOCK_MEDIATYPE = new MediaType("strongfellow", "block");
    private static final MediaType TRANSACTION_MEDIATYPE = new MediaType("strongfellow", "transaction");

    @Bean
    public DataSource dataSource() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:test.db");
        dataSource.setAutoCommit(false);
        return dataSource;
    }

    @Bean
    public HttpMessageConverters customConverters() {
        HttpMessageConverter<Block> blockConverter = new AbstractHttpMessageConverter<Block>(BLOCK_MEDIATYPE) {

            @Override
            protected Block readInternal(Class<? extends Block> clazz, HttpInputMessage msg)
                    throws IOException, HttpMessageNotReadableException {
                BlockReader reader = new BlockReader(msg.getBody());
                Block block = reader.readBlock();
                return block;
            }

            @Override
            protected boolean supports(Class<?> cls) {
                return Block.class.equals(cls);
            }

            @Override
            protected void writeInternal(Block block, HttpOutputMessage msg)
                    throws IOException, HttpMessageNotWritableException {
                BlockWriter.write(block, msg.getBody());
            }
        };

        HttpMessageConverter<Transaction> transactionConverter = new AbstractHttpMessageConverter<Transaction>(TRANSACTION_MEDIATYPE) {

            @Override
            protected Transaction readInternal(Class<? extends Transaction> cls, HttpInputMessage msg)
                    throws IOException, HttpMessageNotReadableException {
                BlockReader reader = new BlockReader(msg.getBody());
                Transaction transaction = reader.readTransaction();
                return transaction;
            }

            @Override
            protected boolean supports(Class<?> cls) {
                return Transaction.class.equals(cls);
            }

            @Override
            protected void writeInternal(Transaction transaction, HttpOutputMessage msg)
                    throws IOException, HttpMessageNotWritableException {
                BlockWriter.writeTransaction(transaction, msg.getBody());
            }
        };

        return new HttpMessageConverters(blockConverter, transactionConverter);
    }
}
