
SELECT "d"."size" AS "size",
       "d"."version" AS "version",
       "d"."lock_time" AS "lock_time",
       SUM("values"."value") AS "output"
FROM "transactions"
JOIN "transactions_details" AS "d" USING("transaction_id")
JOIN "txouts" USING("transaction_id")
JOIN "values" USING("txout_id")
WHERE "transactions"."hash" = :hash
