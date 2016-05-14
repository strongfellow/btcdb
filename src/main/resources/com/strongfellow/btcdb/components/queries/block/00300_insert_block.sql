
INSERT OR IGNORE
INTO "blocks"("hash",
              "size",
	          "version",
	   		  "merkle",
	          "timestamp",
	          "bits",
	          "nonce")
VALUES(:hash,
       :size,
       :version,
       :merkle,
       :timestamp,
       :bits,
       :nonce)
