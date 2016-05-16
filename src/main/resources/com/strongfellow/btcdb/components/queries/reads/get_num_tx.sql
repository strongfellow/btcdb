
SELECT COUNT(*) AS "count"
FROM "transactions_in_blocks"
JOIN "blocks" ON "transactions_in_blocks"."block" = "blocks"."id"
WHERE "blocks"."hash" = :hash
