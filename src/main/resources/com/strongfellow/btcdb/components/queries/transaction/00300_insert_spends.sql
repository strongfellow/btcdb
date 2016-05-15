
WITH "tmp"("input_hash", "input_index", "output_hash", "output_index") AS (VALUES :spends)
INSERT OR IGNORE
INTO "spends"("txin", "txout")
SELECT "txins"."id", "txouts"."id"
FROM "tmp"
JOIN "transactions" "t1" ON "t1"."hash" = "tmp"."input_hash"
JOIN "txins"  ON "txins"."transaction" = "t1"."id" AND "txins"."index" = "tmp"."input_index"
JOIN "transactions" "t2" ON "tmp"."output_hash" = "t2"."hash"
JOIN "txouts" ON "txouts"."transaction" = "t2"."id" AND "txouts"."index" = "tmp"."output_index"
