
SELECT "coinbase"."coinbase" AS "coinbase"
FROM "coinbase"
JOIN "txins" USING("txin_id")
JOIN "transactions" USING("transaction_id")
JOIN "transactions_in_blocks" using("transaction_id")
JOIN "blocks" USING("block_id")
WHERE "blocks"."hash" = :hash
  AND "transactions_in_blocks"."index" = 0
  AND "txins"."index" = 0
