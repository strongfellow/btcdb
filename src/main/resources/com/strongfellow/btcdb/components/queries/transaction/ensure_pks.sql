
WITH "tmp"("hash", "index", "pk") AS (VALUES :pks)
INSERT OR IGNORE INTO
"public_key_scripts"("txout_id", "public_key")
SELECT "txouts"."txout_id", "tmp"."pk"
FROM "txouts"
JOIN "transactions" USING("transaction_id")
JOIN "tmp" ON "transactions"."hash" = "tmp"."hash" AND "txouts"."index" = "tmp"."index"
