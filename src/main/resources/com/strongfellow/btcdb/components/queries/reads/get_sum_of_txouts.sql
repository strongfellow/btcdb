
SELECT sum("value") AS "sum" FROM "blocks"
JOIN "transactions_in_blocks" USING("block_id")
JOIN "txouts" USING("transaction_id")
JOIN "values" USING("txout_id")
WHERE "blocks"."hash" = :hash
