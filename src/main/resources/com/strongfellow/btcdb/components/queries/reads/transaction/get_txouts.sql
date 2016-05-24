
SELECT "txouts"."index" AS "row",
       "public_keys"."hash160" AS "address",
       "values"."value" AS "value",
       "tx"."hash" AS "tx",
       "txins"."index"  AS "index"
FROM "transactions"
JOIN "txouts" USING("transaction_id")
JOIN "values" USING("txout_id")
LEFT JOIN 
  (SELECT "txout_id", "public_key_id" FROM "public_key_scripts"
   UNION
   SELECT  "txout_id", "public_key_id" FROM "p2pkh_scripts")
USING("txout_id")
LEFT JOIN "public_keys" USING("public_key_id")
LEFT JOIN "spends" USING("txout_id")
LEFT JOIN "txins" USING("txin_id")
LEFT JOIN "transactions" AS "tx" ON "txins"."transaction_id" = "tx"."transaction_id"
WHERE "transactions"."hash" = :hash
