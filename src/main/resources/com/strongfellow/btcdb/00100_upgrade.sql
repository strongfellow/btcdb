BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS "blocks"(
  "id" INTEGER PRIMARY KEY,
  "hash" BLOB UNIQUE NOT NULL,
  "size" INTEGER,
  "version" INTEGER,
  "merkle" BLOB,
  "timestamp" INTEGER,
  "bits" INTEGER,
  "nonce" INTEGER
);

CREATE TABLE IF NOT EXISTS "blockchain_links"(
  "child" INTEGER NOT NULL PRIMARY KEY,
  "parent" INTEGER NOT NULL,
  FOREIGN KEY("child") REFERENCES "blocks"("id"),
  FOREIGN KEY("parent") references "blocks"("id")
);

CREATE TABLE IF NOT EXISTS "transactions"(
  "id" INTEGER PRIMARY KEY,
  "hash" BLOB UNIQUE NOT NULL,
  "version" INTEGER NOT NULL,
  "lock_time" INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS "txins"(
  "id" INTEGER PRIMARY KEY,
  "transaction" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  "sequence" INTEGER,
  FOREIGN KEY("transaction") REFERENCES "transactions"("id")
);

CREATE TABLE IF NOT EXISTS "txouts"(
  "id" INTEGER PRIMARY KEY,
  "transaction" INTEGER NOT NULL,
  "index" INTEGER NOT NULL,
  "value" INTEGER NOT NULL,
  FOREIGN KEY("transaction") REFERENCES "transactions"("id")
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

CREATE TABLE IF NOT EXISTS "spends"(
  "txin" INTEGER NOT NULL PRIMARY KEY,
  "txout" INTEGER NOT NULL,
  FOREIGN KEY("txin") REFERENCES "txins"("id"),
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
