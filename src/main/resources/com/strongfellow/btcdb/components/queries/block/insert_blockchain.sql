
INSERT OR IGNORE INTO "chain"("block_id", "parent", "height", "tip")
SELECT "child"."block_id", "parent"."block_id", 0, "child"."block_id"
FROM "blocks" AS "child" JOIN "blocks" AS "parent"
WHERE "child"."hash" = :hash AND "parent"."hash" = :parent
