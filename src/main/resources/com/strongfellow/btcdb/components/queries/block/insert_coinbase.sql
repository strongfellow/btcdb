
INSERT OR IGNORE INTO "coinbase"("txin_id", "coinbase")
SELECT "txins"."txin_id", :coinbase
FROM "txins"
JOIN "transactions" USING("transaction_id")
WHERE "txins"."index" = 0
  AND "transactions"."hash" = :tx
