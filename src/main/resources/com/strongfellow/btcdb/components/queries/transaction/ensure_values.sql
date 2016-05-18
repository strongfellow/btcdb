
WITH "tmp"("hash", "index", "value") AS (VALUES :values)
INSERT OR IGNORE INTO "values"("txout_id", "value")
SELECT "txouts"."txout_id", "tmp"."value"
FROM "tmp"
JOIN "transactions" ON "transactions"."hash" = "tmp"."hash"
JOIN "txouts" ON
  ("txouts"."transaction_id" = "transactions"."transaction_id" AND "txouts"."index" = "tmp"."index")
