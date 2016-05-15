
WITH "tmp"("hash", "index", "value") AS (VALUES :txouts)
INSERT OR IGNORE INTO "values"("txout", "value")
SELECT "txouts"."id", "tmp"."value"
FROM "tmp"
JOIN "transactions" ON "transactions"."hash" = "tmp"."hash"
JOIN "txouts" ON
  ("txouts"."transaction" = "transactions"."id" AND "txouts"."index" = "tmp"."index")
