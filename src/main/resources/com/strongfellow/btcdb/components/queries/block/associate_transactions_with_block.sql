
WITH "tmp"("index", "hash") AS (VALUES :tx_hashes)
INSERT OR IGNORE INTO "transactions_in_blocks"("transaction_id", "block_id", "index")
SELECT "transactions"."transaction_id", "blocks"."block_id", "tmp"."index"
FROM "transactions" JOIN "tmp" USING("hash")
JOIN "blocks"
WHERE "blocks"."hash" = :hash
