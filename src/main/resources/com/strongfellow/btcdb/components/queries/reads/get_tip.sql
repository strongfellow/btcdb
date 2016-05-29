
SELECT "tip_blocks"."hash" AS "tip", ("tips"."height" - "chain"."height") AS "depth"
FROM "chain"
JOIN "blocks" USING("block_id")
JOIN "chain" AS "tips" ON "chain"."tip" = "tips"."block_id"
JOIN "blocks" AS "tip_blocks" ON "tips"."block_id" = "tip_blocks"."block_id"
WHERE "blocks"."hash" = :hash
