
SELECT "b"."size" AS "size",
       "b"."timestamp" AS "timestamp",
       "b"."bits" AS "bits",
       "b"."version" AS "version",
       "b"."nonce" AS "nonce",
       "b"."merkle" AS "merkle"
FROM "blocks" JOIN "blocks_details" "b"
ON "blocks"."id" = "b"."block"
WHERE "blocks"."hash" = :hash
