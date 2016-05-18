
SELECT "b"."size" AS "size",
       "b"."timestamp" AS "timestamp",
       "b"."bits" AS "bits",
       "b"."version" AS "version",
       "b"."nonce" AS "nonce",
       "b"."merkle" AS "merkle"
FROM "blocks" JOIN "blocks_details" AS "b" USING("block_id")
WHERE "blocks"."hash" = :hash
