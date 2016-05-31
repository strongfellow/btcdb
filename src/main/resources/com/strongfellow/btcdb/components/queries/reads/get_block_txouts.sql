

SELECT "transactions_in_blocks"."index" AS "transaction_index",
       "txouts"."index" AS "txout_index",
       "values"."value" AS "value",
       "tx"."hash" AS "tx",
       "txins"."index"  AS "index",
       COALESCE("p"."hash160", "q"."hash160") AS "address"
FROM "transactions"
JOIN "transactions_in_blocks" USING("transaction_id")
JOIN "blocks" USING("block_id")
JOIN "txouts" USING("transaction_id")
JOIN "values" USING("txout_id")
LEFT JOIN "spends" USING("txout_id")
LEFT JOIN "txins" USING("txin_id")
LEFT JOIN "transactions" AS "tx" ON "txins"."transaction_id" = "tx"."transaction_id"
LEFT JOIN "public_key_scripts" USING("txout_id")
LEFT JOIN "public_keys" AS "p" USING("public_key_id")
LEFT JOIN "p2pkh_scripts" USING("txout_id")
LEFT JOIN "public_keys" AS "q" ON "p2pkh_scripts"."public_key_id" = "q"."public_key_id"
WHERE "blocks"."hash" = :hash   
ORDER BY "transaction_index" ASC, "txout_index" ASC
