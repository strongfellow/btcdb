
PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS "blocks"(
  "id" INTEGER PRIMARY KEY,
  "hash" BLOB UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "blockchain_links"(
  "child" INTEGER NOT NULL PRIMARY KEY,
  "parent" INTEGER NOT NULL,
  FOREIGN KEY("child") REFERENCES "blocks"("id"),
  FOREIGN KEY("parent") references "blocks"("id")
);

CREATE TABLE IF NOT EXISTS "blocks_details"(
  "block" INTEGER NOT NULL PRIMARY KEY,
  "size" INTEGER NOT NULL,
  "version" INTEGER NOT NULL,
  "merkle" BLOB NOT NULL,
  "timestamp" INTEGER NOT NULL,
  "bits" INTEGER NOT NULL,
  "nonce" INTEGER NOT NULL,
  FOREIGN KEY("block") REFERENCES "blocks"("id")
);

CREATE TABLE IF NOT EXISTS "transactions"(
  "id" INTEGER PRIMARY KEY,
  "hash" BLOB UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "transactions_details"(
  "transaction" INTEGER NOT NULL PRIMARY KEY,
  "size" INTEGER,
  "version" INTEGER,
  "lock_time" INTEGER,
  FOREIGN KEY("transaction") REFERENCES "transactions"("id")
);

CREATE TABLE IF NOT EXISTS "txins" (
  "id" INTEGER PRIMARY KEY,
  "transaction" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  "sequence" INTEGER,
  FOREIGN KEY("transaction") REFERENCES "transactions"("id")
  UNIQUE("transaction", "index")
);

CREATE TABLE IF NOT EXISTS "txouts"(
  "id" INTEGER PRIMARY KEY,
  "transaction" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  FOREIGN KEY("transaction") REFERENCES "transactions"("id"),
  UNIQUE("transaction", "index")
);

CREATE TABLE IF NOT EXISTS "values"(
  "txout" INTEGER NOT NULL PRIMARY KEY,
  "value" INTEGER NOT NULL,
  FOREIGN KEY("txout") REFERENCES "txouts"("id")
);

CREATE TABLE IF NOT EXISTS "spends"(
  "txin" INTEGER NOT NULL PRIMARY KEY,
  "txout" INTEGER NOT NULL,
  FOREIGN KEY("txin") REFERENCES "txins"("id"),
  FOREIGN KEY("txout") REFERENCES "txouts"("id")
);

CREATE TABLE IF NOT EXISTS "public_key_scripts"(
  "txout" INTEGER NOT NULL PRIMARY KEY,
  "public_key" BLOB NOT NULL,
  FOREIGN KEY("txout") REFERENCES "txouts"("id")
);

CREATE TABLE IF NOT EXISTS "p2pkh_scripts"(
  "txout" INTEGER NOT NULL PRIMARY KEY,
  "public_key_hash" BLOB NOT NULL,
  FOREIGN KEY("txout") REFERENCES "txouts"("id")
);

CREATE TABLE IF NOT EXISTS "p2sh_scripts"(
  "txout" INTEGER NOT NULL PRIMARY KEY,
  "script_hash" BLOB NOT NULL,
  FOREIGN KEY("txout") REFERENCES "txouts"("id")
);

CREATE TABLE IF NOT EXISTS "transactions_in_blocks"(
  "id" INTEGER PRIMARY KEY,
  "transaction" INTEGER NOT NULL,
  "block" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  FOREIGN KEY("transaction") REFERENCES "transactions"("id"),
  FOREIGN KEY("block") REFERENCES "blocks"("id"),
  UNIQUE("transaction", "block")
);

COMMIT;
