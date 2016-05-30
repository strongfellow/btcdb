

SELECT "transactions"."hash" AS "tx"
FROM "transactions"
JOIN "transactions_in_blocks" USING("transaction_id")
JOIN "blocks" USING("block_id")
WHERE "blocks"."hash" = :hash
ORDER BY "transactions_in_blocks"."index" ASC
