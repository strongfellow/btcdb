
SELECT *
FROM
(SELECT "child"."hash" AS "child"
 FROM "blocks" AS "child"
 JOIN "blockchain_links" ON "child"."id" = "blockchain_links"."child"
 JOIN "blocks" AS "parent" on "parent"."id" = "blockchain_links"."parent"
 WHERE "parent"."hash" = :hash)
CROSS JOIN
(SELECT "parent"."hash" AS "parent"
 FROM "blocks" AS "child"
 JOIN "blockchain_links" ON "child"."id" = "blockchain_links"."child"
 JOIN "blocks" AS "parent" on "parent"."id" = "blockchain_links"."parent"
 WHERE "child"."hash" = :hash)
