
WITH "tmp"("input_hash", "input_index", "output_hash", "output_index") AS (VALUES :spends)
INSERT OR IGNORE INTO "spends"("txin_id", "txout_id")
SELECT "txins"."txin_id", "txouts"."txout_id"
FROM "tmp"
JOIN "transactions" "t1" ON "t1"."hash" = "tmp"."input_hash"
JOIN "txins"  ON "txins"."transaction_id" = "t1"."transaction_id" AND "txins"."index" = "tmp"."input_index"
JOIN "transactions" "t2" ON "t2"."hash" = "tmp"."output_hash"
JOIN "txouts" ON "txouts"."transaction_id" = "t2"."transaction_id" AND "txouts"."index" = "tmp"."output_index"
