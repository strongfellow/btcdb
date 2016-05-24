
WITH "tmp"("hash", "index", "hash160") AS (VALUES :pks)
INSERT OR IGNORE INTO
"p2pkh_scripts"("txout_id", "public_key_id")
SELECT "txouts"."txout_id", "public_keys"."public_key_id"
FROM "txouts"
JOIN "transactions" USING("transaction_id")
JOIN "tmp" ON "transactions"."hash" = "tmp"."hash" AND "txouts"."index" = "tmp"."index"
JOIN "public_keys" USING("hash160")
