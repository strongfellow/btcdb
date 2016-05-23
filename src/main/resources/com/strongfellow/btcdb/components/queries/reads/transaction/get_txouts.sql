
SELECT "txouts"."index" AS "row", "public_keys"."hash160" AS "address", "values"."value" AS "value", "tx"."hash" AS "tx", "txins"."index"  AS "index"
FROM "transactions"
JOIN "txouts" USING("transaction_id")
JOIN "values" USING("txout_id")
LEFT JOIN "public_key_scripts" USING("txout_id")
LEFT JOIN "public_keys" USING("public_key_id")
LEFT JOIN "spends" USING("txout_id")
LEFT JOIN "txins" USING("txin_id")
LEFT JOIN "transactions" AS "tx" ON "txins"."transaction_id" = "tx"."transaction_id"
WHERE "transactions"."hash" = :hash
