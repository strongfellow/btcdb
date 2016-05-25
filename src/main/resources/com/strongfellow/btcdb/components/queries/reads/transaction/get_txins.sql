
SELECT "txins"."index" AS "row",
       "values"."value" AS "value",
       "tx"."hash" AS "tx",
       "txouts"."index" AS "index"
FROM "transactions"
JOIN "txins" USING("transaction_id")
JOIN "spends" USING("txin_id")
JOIN "txouts" USING("txout_id")
JOIN "values" USING("txout_id")
JOIN "transactions" AS "tx" ON "tx"."transaction_id" = "txouts"."transaction_id"
LEFT JOIN "transactions_in_blocks" USING("transaction_id")
WHERE "transactions"."hash" = :hash
AND ("transactions_in_blocks"."index" != 0 OR "txins"."index" != 0)
