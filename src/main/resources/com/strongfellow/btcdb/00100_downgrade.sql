
BEGIN TRANSACTION;

DROP TABLE IF EXISTS "transactions_in_blocks";
DROP TABLE IF EXISTS "spends";
DROP TABLE IF EXISTS "p2sh_scripts";
DROP TABLE IF EXISTS "p2pkh_scripts";
DROP TABLE IF EXISTS "public_key_scripts";
DROP TABLE IF EXISTS "txouts";
DROP TABLE IF EXISTS "txins";
DROP TABLE IF EXISTS "transactions";
DROP TABLE IF EXISTS "blockchain_links";
DROP TABLE IF EXISTS "blocks";

COMMIT;
