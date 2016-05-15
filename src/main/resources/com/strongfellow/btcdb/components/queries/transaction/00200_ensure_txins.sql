
WITH "tmp"("hash", "index", "sequence") AS (VALUES :txins)
INSERT OR IGNORE
INTO "txins"("transaction", "index", "sequence")
SELECT "transactions"."id", "tmp"."index", "tmp"."sequence"
FROM "transactions" JOIN "tmp" USING("hash")
