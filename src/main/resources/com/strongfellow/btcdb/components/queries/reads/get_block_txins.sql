

SELECT "transactions_in_blocks"."index" AS "transaction_index",
       "txins"."index" AS "txin_index",
       "values"."value" AS "value",
       "tx"."hash" AS "tx",
       "txouts"."index" AS "index",
       COALESCE("p"."hash160", "q"."hash160") AS "address"

FROM "transactions"
JOIN "txins" USING("transaction_id")
JOIN "spends" USING("txin_id")
JOIN "txouts" USING("txout_id")
JOIN "values" USING("txout_id")
JOIN "transactions" AS "tx" ON "tx"."transaction_id" = "txouts"."transaction_id"
JOIN "transactions_in_blocks" USING("transaction_id")
JOIN "blocks" USING("block_id")
LEFT JOIN "public_key_scripts" USING("txout_id")
LEFT JOIN "public_keys" AS "p" USING("public_key_id")
LEFT JOIN "p2pkh_scripts" USING("txout_id")
LEFT JOIN "public_keys" AS "q" ON "p2pkh_scripts"."public_key_id" = "q"."public_key_id"
WHERE "blocks"."hash" = :hash   
AND ("transactions_in_blocks"."index" != 0 OR "txins"."index" != 0)
ORDER BY "transaction_index" ASC, "txin_index" ASC
