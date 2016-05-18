
UPDATE "chain"
SET "height" = (:height + 1)
WHERE "chain"."block_id" IN
  (SELECT "child"."block_id"
   FROM "chain" AS "child"
   JOIN "chain" AS "parent" ON "child"."parent" = "parent"."block_id"
   WHERE "parent"."height" = :height AND "child"."height" IS NULL)
