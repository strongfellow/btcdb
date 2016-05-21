
SELECT SUM("values"."value") AS "sumOfTxins"
FROM "values"
JOIN "txouts" USING("txout_id")
JOIN "spends" USING("txout_id")
JOIN "txins" USING("txin_id")
JOIN "transactions"
  ON "txins"."transaction_id" = "transactions"."transaction_id"
JOIN "transactions_in_blocks"
  ON "transactions"."transaction_id" = "transactions_in_blocks"."transaction_id"
JOIN "blocks" USING("block_id")
WHERE "blocks"."hash" = :hash
  AND ("transactions_in_blocks"."index" != 0 OR "txins"."index" != 0)
  