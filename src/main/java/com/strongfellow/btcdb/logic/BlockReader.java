package com.strongfellow.btcdb.logic;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.BlockHeader;
import com.strongfellow.btcdb.protocol.Metadata;
import com.strongfellow.btcdb.protocol.Input;
import com.strongfellow.btcdb.protocol.Output;
import com.strongfellow.btcdb.protocol.Transaction;

public class BlockReader {

    private byte[] buffer = new byte[1024];
    
    private int blockSize;
    private int transactionSize;
    private final MessageDigest doubleDigest = DigestUtils.getSha256Digest();
    private final MessageDigest blockDigest = DigestUtils.getSha256Digest();
    private final MessageDigest transactionDigest = DigestUtils.getSha256Digest();
    
    private final InputStream in;
    
    private final byte[] hash = new byte[32];
    private String readHash() throws IOException {
        readExactly(32);
        for (int i = 0; i < 32; i++) {
            hash[31 - i] = buffer[i];
        }
        return new String(Hex.encodeHex(hash, true));
    }
    
    private void readExactly(int n) throws IOException {
        int offset = 0;
        if (n > buffer.length) {
            buffer = new byte[n];
        }
        while (offset < n) {
            int r = in.read(buffer, offset, n - offset);
            if (r == -1) {
                throw new IOException("no bytes left in stream");
            }
            offset += r;
        }
        blockDigest.update(buffer, 0, n);
        transactionDigest.update(buffer, 0, n);
        blockSize += n;
        transactionSize += n;
    }
    
    private long readInt(int len) throws IOException {
        readExactly(len);
        long result = 0;
        for (int i = 0; i < len; i++) {
            result += (0xff & buffer[i]) << (8 * i);
        }
        return result;
    }
    
    private long readVarint() throws IOException {
        readExactly(1);
        int b = 0xff & buffer[0];
        
        int len = 0;
        if (b < 0xfd) {
            return b;
        } else if (b == 0xfd) {
            len = 2;
        } else if (b == 0xfe) {
            len = 4;
        } else {
            len = 8;
        }
        return readInt(len);
    }
    
    public BlockReader(InputStream input) {
        this.in = input;
    }

    private String getDigest(MessageDigest digest) {
        doubleDigest.reset();
        doubleDigest.update(digest.digest());
        byte[] bs = doubleDigest.digest();
        ArrayUtils.reverse(bs);
        return Hex.encodeHexString(bs);
    }
    
    public Block readBlock() throws IOException {
        BlockHeader header = this.readBlockHeader();
        String headerHash = getDigest(blockDigest);

        List<Transaction> transactions = this.readTransactions();
        Metadata blockMetadata = new Metadata(this.blockSize, headerHash);
        return new Block(blockMetadata, header, transactions);
    }

    private List<Transaction> readTransactions() throws IOException {
        int n = (int)this.readVarint();
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            transactions.add(this.readTransaction());
        }
        return transactions;
    }

    private BlockHeader readBlockHeader() throws IOException {
        return new BlockHeader((int) readInt(4), readHash(), readHash(), readInt(4), readInt(4), readInt(4));
    }

    public Transaction readTransaction() throws IOException {
        this.transactionDigest.reset();
        this.transactionSize = 0;
        int version = (int) readInt(4);
        List<Input> inputs = readInputs();
        List<Output> outputs = readOutputs();
        long lockTime = readInt(4);
        return new Transaction(new Metadata(transactionSize, getDigest(transactionDigest)), version, inputs, outputs, lockTime);
    }

    private List<Output> readOutputs() throws IOException {
        int n = (int) readVarint();
        List<Output> outputs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Output output = new Output(readInt(8), readScript());
            outputs.add(output );
        }
        return outputs;
    }

    private byte[] readScript() throws IOException {
        int n = (int) readVarint();
        readExactly(n);
        byte[] bs = new byte[n];
        for (int i = 0; i < n; i++) {
            bs[i] = buffer[i];
        }
        return bs;
    }

    private List<Input> readInputs() throws IOException {
        int n = (int) readVarint();
        List<Input> inputs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Input input = new Input(readHash(), (int) readInt(4), readScript(), readInt(4));
            inputs.add(input);
        }
        return inputs;
    }

}
