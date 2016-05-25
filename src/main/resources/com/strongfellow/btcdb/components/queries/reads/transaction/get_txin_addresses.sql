
SELECT "txins"."index" AS "row", "public_keys"."hash160" AS "address"
FROM "transactions"
JOIN "txins" USING("transaction_id")
JOIN "spends" USING("txin_id")
JOIN "txouts" USING("txout_id")
LEFT JOIN "public_key_scripts" USING("txout_id")
LEFT JOIN "public_keys" USING("public_key_id")
JOIN "transactions" AS "tx" ON "tx"."transaction_id" = "txouts"."transaction_id"
LEFT JOIN "transactions_in_blocks" USING("transaction_id")
WHERE "transactions"."hash" = :hash
AND ("transactions_in_blocks"."index" != 0 OR "txins"."index" != 0)

UNION ALL

SELECT "txins"."index" AS "row", "public_keys"."hash160" AS "address"
FROM "transactions"
JOIN "txins" USING("transaction_id")
JOIN "spends" USING("txin_id")
JOIN "txouts" USING("txout_id")
LEFT JOIN "p2pkh_scripts" USING("txout_id")
LEFT JOIN "public_keys" USING("public_key_id")
JOIN "transactions" AS "tx" ON "tx"."transaction_id" = "txouts"."transaction_id"
LEFT JOIN "transactions_in_blocks" USING("transaction_id")
WHERE "transactions"."hash" = :hash
AND ("transactions_in_blocks"."index" != 0 OR "txins"."index" != 0)
