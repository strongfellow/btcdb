
WITH "t1"("tip", "height") AS

(SELECT "children"."block_id" AS "tip", "children"."height" AS "height"
FROM "chain" AS "parent"
JOIN "blocks" USING("block_id")
JOIN "chain" AS "children" ON "children"."parent" = "parent"."block_id"
JOIN "chain" AS "tips" ON "children"."tip" = "tips"."block_id"
WHERE "hash" = :hash
ORDER BY "height" DESC, "tip" LIMIT 1
),
"t2"("tip", "height") AS
(
SELECT "chain"."block_id" AS "tip", "chain"."height" AS "height"
FROM "chain" JOIN "blocks" USING("block_id")
WHERE "blocks"."hash" = :hash
)

SELECT tip, height FROM t1
UNION
SELECT tip, height from t2
ORDER BY height DESC, tip LIMIT 1
