
SELECT "chain"."height"
FROM "blocks" JOIN "chain" USING("block_id")
WHERE "blocks"."hash" = :hash
