package com.strongfellow.btcdb.components;

import java.io.IOException;

public class WriteQueries {

    private final QueryCache queryCache;

    public WriteQueries(QueryCache qc) {
        this.queryCache = qc;
    }

    public String associateTransactionsWithBlocks() throws IOException {
        return queryCache.loadQuery("block/associate_transactions_with_block");
    }

    public String ensureBlocks() throws IOException {
        return queryCache.loadQuery("block/ensure_blocks");
    }

    public String ensureTransactions() throws IOException {
        return queryCache.loadQuery("block/ensure_transactions");
    }

    public String ensureTransactionDetails() throws IOException {
        return queryCache.loadQuery("transaction/ensure_transaction_details");
    }

    public String insertBlockchain() throws IOException {
        return queryCache.loadQuery("block/insert_blockchain");
    }

    public String insertBlocksDetails() throws IOException {
        return queryCache.loadQuery("block/insert_blocks_details");
    }

    public String updateDescendents() throws IOException {
        return queryCache.loadQuery("block/update_descendents");
    }

    public String ensureSpends() throws IOException {
        return queryCache.loadQuery("transaction/ensure_spends");
    }

    public String ensureTxins() throws IOException {
        return queryCache.loadQuery("transaction/ensure_txins");
    }

    public String ensureTxouts() throws IOException {
        return queryCache.loadQuery("transaction/ensure_txouts");
    }
    public String ensureValues() throws IOException {
        return queryCache.loadQuery("transaction/ensure_values");
    }

    public String insertCoinbase() throws IOException {
        return queryCache.loadQuery("block/insert_coinbase");
    }

    public String insertPublicKeys() throws IOException {
        return queryCache.loadQuery("transaction/insert_public_key_hashes");
    }

    public String insertPublicKeyTxouts() throws IOException {
        return queryCache.loadQuery("transaction/ensure_pks");
    }

    public String insertPublicKeyHashTxouts() throws IOException {
        return queryCache.loadQuery("transaction/ensure_pkhashes");
    }

    public String updateDepths() throws IOException {
        return queryCache.loadQuery("block/update_depths");
    }

    public String extendChain() throws IOException {
        return queryCache.loadQuery("block/extend_chain");
    }

}
