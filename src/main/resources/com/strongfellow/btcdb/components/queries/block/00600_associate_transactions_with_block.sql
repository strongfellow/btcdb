WITH "tmp"("index", "tx") AS (VALUES :tx_hashes)
INSERT OR IGNORE INTO "transactions_in_blocks"("transaction", "block", "index")
SELECT "transactions"."id", "blocks"."id", "tmp"."index"
FROM "blocks" JOIN "transactions" JOIN "tmp" ON "transactions"."hash" = "tmp"."tx"
WHERE "blocks"."hash" = :hash
