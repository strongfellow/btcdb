
PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

DROP TABLE IF EXISTS "coinbase";
DROP TABLE IF EXISTS "p2sh_scripts";
DROP TABLE IF EXISTS "p2pkh_scripts";
DROP TABLE IF EXISTS "public_key_scripts";
DROP TABLE IF EXISTS "public_keys";
DROP TABLE IF EXISTS "script_hashes";
DROP TABLE IF EXISTS "spends";
DROP TABLE IF EXISTS "values";
DROP TABLE IF EXISTS "txouts";
DROP TABLE IF EXISTS "txins";
DROP TABLE IF EXISTS "transactions_in_blocks";
DROP TABLE IF EXISTS "transactions_details";
DROP TABLE IF EXISTS "transactions";
DROP TABLE IF EXISTS "blocks_details";
DROP TABLE IF EXISTS "chain";
DROP TABLE IF EXISTS "blocks";

COMMIT;
