
PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS "blocks"(
  "block_id" INTEGER PRIMARY KEY,
  "hash" BLOB UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "chain"(
  "block_id" INTEGER NOT NULL PRIMARY KEY,
  "parent" INTEGER,
  "height" INTEGER,
  FOREIGN KEY("block_id") REFERENCES "blocks"("block_id")
);

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

CREATE TABLE IF NOT EXISTS "transactions"(
  "transaction_id" INTEGER PRIMARY KEY,
  "hash" BLOB UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "transactions_details"(
  "transaction_id" INTEGER NOT NULL PRIMARY KEY,
  "size" INTEGER,
  "version" INTEGER,
  "lock_time" INTEGER,
  FOREIGN KEY("transaction_id") REFERENCES "transactions"("transaction_id")
);

CREATE TABLE IF NOT EXISTS "transactions_in_blocks"(
  "transaction_id" INTEGER NOT NULL,
  "block_id" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  FOREIGN KEY("transaction_id") REFERENCES "transactions"("transaction_id"),
  FOREIGN KEY("block_id") REFERENCES "blocks"("block_id"),
  UNIQUE("transaction_id", "block_id")
);

CREATE TABLE IF NOT EXISTS "txins" (
  "txin_id" INTEGER PRIMARY KEY,
  "transaction_id" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  "sequence" INTEGER,
  FOREIGN KEY("transaction_id") REFERENCES "transactions"("transaction_id")
  UNIQUE("transaction_id", "index")
);

CREATE TABLE IF NOT EXISTS "txouts"(
  "txout_id" INTEGER PRIMARY KEY,
  "transaction_id" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  FOREIGN KEY("transaction_id") REFERENCES "transactions"("transaction_id"),
  UNIQUE("transaction_id", "index")
);

CREATE TABLE IF NOT EXISTS "values"(
  "txout_id" INTEGER NOT NULL PRIMARY KEY,
  "value" INTEGER NOT NULL,
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id")
);

CREATE TABLE IF NOT EXISTS "spends"(
  "txin_id" INTEGER NOT NULL PRIMARY KEY,
  "txout_id" INTEGER NOT NULL,
  FOREIGN KEY("txin_id") REFERENCES "txins"("txin_id"),
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id")
);

CREATE TABLE IF NOT EXISTS "public_key_scripts"(
  "txout_id" INTEGER NOT NULL PRIMARY KEY,
  "public_key" BLOB NOT NULL,
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id")
);

CREATE TABLE IF NOT EXISTS "p2pkh_scripts"(
  "txout_id" INTEGER NOT NULL PRIMARY KEY,
  "public_key_hash" BLOB NOT NULL,
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id")
);

CREATE TABLE IF NOT EXISTS "p2sh_scripts"(
  "txout_id" INTEGER NOT NULL PRIMARY KEY,
  "script_hash" BLOB NOT NULL,
  FOREIGN KEY("txout_id") REFERENCES "txouts"("txout_id")
);

COMMIT;
