
WITH "tmp"("hash", "size", "version", "lock_time") AS (VALUES :details)
INSERT OR IGNORE
INTO "transactions_details"("transaction_id",  "size", "version", "lock_time")
SELECT "transactions"."transaction_id", "tmp"."size", "tmp"."version", "tmp"."lock_time"
FROM "tmp"
JOIN "transactions" USING("hash")
