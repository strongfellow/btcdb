package com.strongfellow.btcdb.components;

import java.io.IOException;

public class ReadQueries {

    private final QueryCache queryCache;

    public ReadQueries(QueryCache qc) {
        this.queryCache = qc;
    }

    public String getBlockDetails() throws IOException {
        return queryCache.loadQuery("reads/get_block_details");
    }

    public String getChildren() throws IOException {
        return queryCache.loadQuery("reads/get_children");
    }

    public String getNumTx() throws IOException {
        return queryCache.loadQuery("reads/get_num_tx");
    }

    public String getParent() throws IOException {
        return queryCache.loadQuery("reads/get_parent");
    }

    public String getSumOfTxIns() throws IOException {
        return queryCache.loadQuery("reads/get_sum_of_txins");
    }
    public String getSumOfTxOuts() throws IOException {
        return queryCache.loadQuery("reads/get_sum_of_txouts");
    }
    public String getCoinbaseValue() throws IOException {
        return queryCache.loadQuery("reads/get_coinbase_value");
    }

    public String getCoinbaseScript() throws IOException {
        return queryCache.loadQuery("reads/get_coinbase_script");
    }

    public String getTransactionSummary() throws IOException {
        return queryCache.loadQuery("reads/transaction/get_transaction_summary");
    }

    public String getTxouts() throws IOException {
        return queryCache.loadQuery("reads/transaction/get_txouts");
    }

    public String getTxins() throws IOException {
        return queryCache.loadQuery("reads/transaction/get_txins");
    }

    public String getBlockHeight() throws IOException {
        return queryCache.loadQuery("reads/get_block_height");
    }

    public String getOutputAddresses() throws IOException {
        return queryCache.loadQuery("reads/transaction/get_txout_addresses");
    }

    public String getInputAddresses() throws IOException {
        return queryCache.loadQuery("reads/transaction/get_txin_addresses");
    }

    public String getBlocksForTransaction() throws IOException {
        return queryCache.loadQuery("reads/transaction/get_blocks");
    }

    public String chooseTip() throws IOException {
        return queryCache.loadQuery("reads/choose_tip");
    }

    public String getTip() throws IOException {
        return queryCache.loadQuery("reads/get_tip");
    }

    public String getTransactionsForBlock() throws IOException {
        return queryCache.loadQuery("reads/get_transactions_for_block");
    }

}
