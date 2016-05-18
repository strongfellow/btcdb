
SELECT "parent"."hash" AS "parent", "chain"."height" AS "height"
FROM "blocks" AS "child"
JOIN "chain" ON "child"."block_id" = "chain"."block_id"
JOIN "blocks" AS "parent" on "parent"."block_id" = "chain"."parent"
WHERE "child"."hash" = :hash
