UPDATE "blocks"
SET "hash"=:hash,
    "size"=:size,
    "version"=:version,
    "merkle"=:merkle,
    "timestamp"=:timestamp,
    "bits"=:bits,
    "nonce"=:nonce
WHERE "hash"=:hash
