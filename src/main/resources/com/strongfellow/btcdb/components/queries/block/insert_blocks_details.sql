
WITH "tmp"("hash", "size", "version", "merkle", "timestamp", "bits", "nonce")
AS (VALUES (:hash, :size, :version, :merkle, :timestamp, :bits, :nonce))
INSERT OR IGNORE
INTO "blocks_details"("block_id", "size", "version", "merkle", "timestamp", "bits", "nonce")
SELECT "blocks"."block_id", "tmp"."size", "tmp"."version", "tmp"."merkle", "tmp"."timestamp", "tmp"."bits", "tmp"."nonce"
FROM "blocks" JOIN "tmp" USING("hash")
