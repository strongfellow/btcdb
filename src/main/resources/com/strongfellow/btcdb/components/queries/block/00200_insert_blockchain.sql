
INSERT OR IGNORE
INTO "blockchain_links"("parent", "child")
SELECT "parent"."id", "child"."id"
FROM "blocks" AS "parent" JOIN "blocks" AS "child"
WHERE "parent"."hash" = :previous AND "child"."hash" = :hash
