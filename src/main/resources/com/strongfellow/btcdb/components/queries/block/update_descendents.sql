
INSERT OR REPLACE INTO "chain"("block_id", "parent", "height", "tip")
SELECT "child"."block_id", "parent"."block_id", "parent"."height" + 1, "child"."tip"
FROM "chain" AS "child"
JOIN "chain" AS "parent"
ON "child"."parent" = "parent"."block_id"
WHERE "parent"."height" = :height AND "child"."height" != (:height + 1)
