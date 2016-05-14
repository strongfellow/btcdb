
WITH "tmp"("index", "hash") AS (VALUES :tx_hashes)
INSERT OR IGNORE INTO "transactions"("hash")
SELECT "hash" FROM "tmp"
