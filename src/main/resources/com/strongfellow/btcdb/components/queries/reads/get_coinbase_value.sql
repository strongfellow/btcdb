
SELECT "values"."value" AS "coinbase"
FROM "values"
JOIN "txouts" USING("txout_id")
JOIN "transactions" USING("transaction_id")
JOIN "transactions_in_blocks" USING("transaction_id")
JOIN "blocks" USING("block_id")
WHERE "blocks"."hash" = :hash
  AND "transactions_in_blocks"."index" = 0
  AND "txouts"."index" = 0
