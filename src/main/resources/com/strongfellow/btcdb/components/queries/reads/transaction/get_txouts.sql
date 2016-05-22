
SELECT "public_key_scripts"."public_key" AS "address", "values"."value" AS "value"
FROM "transactions"
JOIN "txouts" USING("transaction_id")
JOIN "values" USING("txout_id")
LEFT JOIN "public_key_scripts" USING("txout_id")
WHERE "transactions"."hash" = :hash
