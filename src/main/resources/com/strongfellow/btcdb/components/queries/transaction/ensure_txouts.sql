WITH "tmp"("index", "hash") AS (VALUES :txouts)
INSERT OR IGNORE
INTO "txouts"("transaction_id", "index")
SELECT "transactions"."transaction_id", "tmp"."index"
FROM "tmp" JOIN "transactions" USING("hash")
