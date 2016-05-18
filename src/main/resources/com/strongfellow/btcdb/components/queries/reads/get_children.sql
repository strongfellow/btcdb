
SELECT "child"."hash" AS "child"
FROM "blocks" AS "child" JOIN "chain" USING("block_id")
JOIN "blocks" AS "parent" on "parent"."block_id" = "chain"."parent"
WHERE "parent"."hash" = :hash
