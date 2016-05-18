
WITH "tmp"("hash", "index", "sequence") AS (VALUES :txins)
INSERT OR IGNORE
INTO "txins"("transaction_id", "index", "sequence")
SELECT "transactions"."transaction_id", "tmp"."index", "tmp"."sequence"
FROM "transactions" JOIN "tmp" USING("hash")
