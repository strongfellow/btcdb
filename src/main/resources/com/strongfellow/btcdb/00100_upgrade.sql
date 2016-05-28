
PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS "blocks"(
  "block_id" INTEGER PRIMARY KEY,
  "hash" BLOB UNIQUE NOT NULL
);
CREATE INDEX IF NOT EXISTS "blocks_block_id_idx"
ON "blocks"("block_id");
CREATE INDEX IF NOT EXISTS "blocks_hash_idx"
ON "blocks"("hash");

CREATE TABLE IF NOT EXISTS "chain"(
  "block_id" INTEGER NOT NULL PRIMARY KEY,
  "parent" INTEGER,
  "height" INTEGER,
  "tip" INTEGER,
  FOREIGN KEY("block_id") REFERENCES "blocks"("block_id")
);
CREATE INDEX IF NOT EXISTS "chain_block_id_idx" ON "chain"("block_id");
CREATE INDEX IF NOT EXISTS "chain_parent_idx" ON "chain"("parent");
CREATE INDEX IF NOT EXISTS "chain_height_idx" ON "chain"("height");
CREATE INDEX IF NOT EXISTS "chain_tip_idx" ON "chain"("tip");

INSERT OR IGNORE INTO "blocks"("hash")
VALUES (x'0000000000000000000000000000000000000000000000000000000000000000');

INSERT OR IGNORE INTO "chain"("block_id", "parent", "height")
SELECT "block_id", NULL, -1
FROM "blocks"
WHERE "blocks"."hash" = x'0000000000000000000000000000000000000000000000000000000000000000';

CREATE TABLE IF NOT EXISTS "blocks_details"(
  "block_id" INTEGER NOT NULL PRIMARY KEY,
  "size" INTEGER NOT NULL,
  "version" INTEGER NOT NULL,
  "merkle" BLOB NOT NULL,
  "timestamp" INTEGER NOT NULL,
  "bits" INTEGER NOT NULL,
  "nonce" INTEGER NOT NULL,
  FOREIGN KEY("block_id") REFERENCES "blocks"("block_id")
);
CREATE INDEX IF NOT EXISTS "blocks_details_block_id_idx"
ON "blocks_details"("block_id");
CREATE INDEX IF NOT EXISTS "blocks_details_merkle_idx"
ON "blocks_details"("merkle");

CREATE TABLE IF NOT EXISTS "transactions"(
  "transaction_id" INTEGER PRIMARY KEY,
  "hash" BLOB UNIQUE NOT NULL
);
CREATE INDEX IF NOT EXISTS "transactions_transaction_id_idx"
ON "transactions"("transaction_id");

CREATE TABLE IF NOT EXISTS "transactions_details"(
  "transaction_id" INTEGER NOT NULL PRIMARY KEY,
  "size" INTEGER,
  "version" INTEGER,
  "lock_time" INTEGER,
  FOREIGN KEY("transaction_id") REFERENCES "transactions"("transaction_id")
);
CREATE INDEX IF NOT EXISTS "transactions_details_transaction_id_idx"
ON "transactions_details"("transaction_id");

CREATE TABLE IF NOT EXISTS "transactions_in_blocks"(
  "transaction_id" INTEGER NOT NULL,
  "block_id" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  FOREIGN KEY("transaction_id") REFERENCES "transactions"("transaction_id"),
  FOREIGN KEY("block_id") REFERENCES "blocks"("block_id"),
  UNIQUE("transaction_id", "block_id")
);
CREATE INDEX IF NOT EXISTS "transactions_in_blocks_transaction_id_idx"
ON "transactions_in_blocks"("transaction_id");
CREATE INDEX IF NOT EXISTS "transactions_in_blocks_block_id_idx"
ON "transactions_in_blocks"("block_id");

CREATE TABLE IF NOT EXISTS "txins" (
  "txin_id" INTEGER PRIMARY KEY,
  "transaction_id" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  "sequence" INTEGER,
  FOREIGN KEY("transaction_id") REFERENCES "transactions"("transaction_id"),
  UNIQUE("transaction_id", "index")
);
CREATE INDEX IF NOT EXISTS "txins_txin_id_idx"
ON "txins"("txin_id");
CREATE INDEX IF NOT EXISTS "txins_transaction_id_idx"
ON "txins"("transaction_id");

CREATE TABLE IF NOT EXISTS "txouts"(
  "txout_id" INTEGER PRIMARY KEY,
  "transaction_id" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  FOREIGN KEY("transaction_id") REFERENCES "transactions"("transaction_id"),
  UNIQUE("transaction_id", "index")
);
CREATE INDEX IF NOT EXISTS "txouts_txout_id_idx"
ON "txouts"("txout_id");
CREATE INDEX IF NOT EXISTS "txouts_transaction_id_idx"
ON "txouts"("transaction_id");

CREATE TABLE IF NOT EXISTS "values"(
  "txout_id" INTEGER NOT NULL PRIMARY KEY,
  "value" INTEGER NOT NULL,
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id")
);
CREATE INDEX IF NOT EXISTS "values_txout_id_idx"
ON "values"("txout_id");

CREATE TABLE IF NOT EXISTS "spends"(
  "txin_id" INTEGER NOT NULL PRIMARY KEY,
  "txout_id" INTEGER NOT NULL,
  FOREIGN KEY("txin_id") REFERENCES "txins"("txin_id"),
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id")
);
CREATE INDEX IF NOT EXISTS "spends_txin_id_idx"
ON "spends"("txin_id");
CREATE INDEX IF NOT EXISTS "spends_txout_id_idx"
ON "spends"("txout_id");

CREATE TABLE IF NOT EXISTS "script_hashes"(
  "script_hash_id" INTEGER NOT NULL PRIMARY KEY,
  "script_hash" BLOB UNIQUE NOT NULL
);
CREATE INDEX IF NOT EXISTS "script_hashes_script_hash_id_idx"
ON "script_hashes"("script_hash_id");

CREATE TABLE IF NOT EXISTS "public_keys"(
  "public_key_id" INTEGER NOT NULL PRIMARY KEY,
  "hash160" BLOB UNIQUE NOT NULL
);
CREATE INDEX IF NOT EXISTS "public_keys_public_key_id_idx"
ON "public_keys"("public_key_id");
CREATE INDEX IF NOT EXISTS "public_keys_hash160_idx"
ON "public_keys"("hash160");

CREATE TABLE IF NOT EXISTS "public_key_scripts"(
  "txout_id" INTEGER NOT NULL PRIMARY KEY,
  "public_key_id" INTEGER NOT NULL,
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id"),
  FOREIGN KEY("public_key_id") REFERENCES "public_keys"("public_key_id")
);
CREATE INDEX IF NOT EXISTS "public_key_scripts_txout_id_idx"
ON "public_key_scripts"("txout_id");

CREATE TABLE IF NOT EXISTS "p2pkh_scripts"(
  "txout_id" INTEGER NOT NULL PRIMARY KEY,
  "public_key_id" INTEGER NOT NULL,
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id"),
  FOREIGN KEY("public_key_id") REFERENCES "public_keys"("public_key_id")
);
CREATE INDEX IF NOT EXISTS "p2pkh_scripts_txout_id_idx"
ON "p2pkh_scripts"("txout_id");

CREATE TABLE IF NOT EXISTS "p2sh_scripts"(
  "txout_id" INTEGER NOT NULL PRIMARY KEY,
  "script_hash_id" INTEGER NOT NULL,
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id"),
  FOREIGN KEY("script_hash_id") REFERENCES "script_hashes"("script_hash_id")
);
CREATE INDEX IF NOT EXISTS "p2sh_scripts_txout_id_idx"
ON "p2sh_scripts"("txout_id");

CREATE TABLE IF NOT EXISTS "coinbase"(
  "txin_id" INTEGER NOT NULL PRIMARY KEY,
  "coinbase" BLOB NOT NULL,
  FOREIGN KEY("txin_id") REFERENCES "txins"("txin_id")
);
CREATE INDEX IF NOT EXISTS "coinbase_txin_idx"
ON "coinbase"("txin_id");

COMMIT;
