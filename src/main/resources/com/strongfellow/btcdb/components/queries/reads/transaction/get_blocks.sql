
SELECT "blocks"."hash" AS "block", "chain"."height" AS "height"
FROM "transactions"
JOIN "transactions_in_blocks" USING("transaction_id")
JOIN "blocks" USING("block_id")
JOIN "chain" USING("block_id")
WHERE "transactions"."hash" = :hash
