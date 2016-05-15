WITH "tmp"("index", "hash") AS (VALUES :txouts)
INSERT OR IGNORE
INTO "txouts"("transaction", "index")
SELECT "transactions"."id", "tmp"."index"
FROM "tmp" JOIN "transactions" USING("hash")
