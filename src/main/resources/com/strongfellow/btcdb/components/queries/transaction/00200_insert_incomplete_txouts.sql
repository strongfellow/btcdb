WITH "tmp"("hash", "index") AS (VALUES :txouts)
INSERT OR IGNORE
INTO "txouts"("transaction", "index")
SELECT "transactions"."id", "tmp"."index"
FROM "transactions" JOIN "tmp" ON "transactions"."hash" = "tmp"."hash"
