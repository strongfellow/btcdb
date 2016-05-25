
SELECT "txouts"."index" AS "row",
       "public_keys"."hash160" AS "address"
FROM "transactions"
JOIN "txouts" USING("transaction_id")
LEFT JOIN public_key_scripts USING("txout_id")
LEFT JOIN "public_keys" USING("public_key_id")
WHERE "transactions"."hash" = :hash

UNION ALL
SELECT "txouts"."index" AS "row",
       "public_keys"."hash160" AS "address"
FROM "transactions"
JOIN "txouts" USING("transaction_id")
LEFT JOIN p2pkh_scripts USING("txout_id")
LEFT JOIN "public_keys" USING("public_key_id")
WHERE "transactions"."hash" = :hash

