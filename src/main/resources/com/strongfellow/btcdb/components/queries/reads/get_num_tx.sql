
SELECT COUNT(*) AS "count"
FROM "transactions_in_blocks"
JOIN "blocks" USING("block_id")
WHERE "blocks"."hash" = :hash
