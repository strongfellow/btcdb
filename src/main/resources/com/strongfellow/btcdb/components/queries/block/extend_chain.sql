WITH "root"("block_id", "parent", "height", "tip")
  AS (SELECT "block_id", "parent", "height", "tip"
      FROM "chain"
      WHERE "chain"."tip" = :tip
      ORDER BY "height" ASC)
INSERT OR REPLACE INTO "chain"("block_id", "parent", "height", "tip")
SELECT "chain"."block_id", "chain"."parent", "chain"."height", "root"."tip"
FROM "root"
JOIN "chain" AS "parent" ON "root"."parent" = "parent"."block_id"
JOIN "chain" AS "root_tip" ON "root"."tip" = "root_tip"."block_id"
JOIN "chain" AS "parent_tip" ON "parent"."tip" = "parent_tip"."block_id"
JOIN "chain" ON "chain"."tip" = "parent"."tip"
WHERE "chain"."height" < "root"."height"
  AND "parent_tip"."height"< "root_tip"."height"
