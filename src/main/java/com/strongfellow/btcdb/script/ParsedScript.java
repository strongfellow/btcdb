package com.strongfellow.btcdb.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.strongfellow.btcdb.logic.Hashes;

public class ParsedScript {

    // standard locking scripts:
    //   1 - P2PKH
    //           DUP HASH160 pkhash EQUAL CHECKSIG]
    //   2 - public-key
    //           pk CHECKSIG
    //   3 - multi-signature (limited to 15 keys)
    //           m pk1 pk2 ... pkN n CHECKMULTISIG
    //   4 - P2SH
    //           HASH160 script_hash EQUAL
    //   5 - OP_RETURN
    //           RETURN <data>

    private static int littleEndian(byte[] bytes, int offset, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result |= (0xff & bytes[offset + i]) << (8 * i);
        }
        return result;
    }

    @Override
    public String toString() {
        return StringUtils.join(this.scriptElements, ' ');
    }


    private ParsedScript(byte[] script) throws UnknownOpCodeException {

        List<ScriptElement> scriptElements = new ArrayList<>();

        for (int i = 0; i < script.length; ){
            final int index = i;
            Integer op = 0xff & script[index];
            OpCode opCode = opMap.get(op);
            ++i;
            if (opCode == null) {
                throw new UnknownOpCodeException(index, op);
            } else if (op < 0x4f) {
                Integer length = null;
                if (op < 0x4c) { // OP_DATA_XX
                    length = op;
                } else if (op == 0x4c) { // PUSHDATA1
                    length = littleEndian(script, index + 1, 1);
                    ++i;
                } else if (op == 0x4d) { // PUSHDATA2
                    length = littleEndian(script, index + 1, 2);
                    i += 2;
                } else if (op == 0x4e) { // PUSHDATA4
                    length = littleEndian(script, index + 1, 4);
                    i += 4;
                }
                ByteArrayWrapper data = new ByteArrayWrapper(i, length);
                i += length;
                ScriptElement e = new ScriptElement(index, opCode, data);
                scriptElements.add(e);
            } else {
                scriptElements.add(new ScriptElement(index, opCode));
            }
        }
        this.script = script;
        this.scriptElements = Collections.unmodifiableList(scriptElements);
    }

    public static ParsedScript from(byte[] script) throws UnknownOpCodeException {

        return new ParsedScript(script);
    }

    private final byte[] script;
    private final List<ScriptElement> scriptElements;

    static final Map<Integer, OpCode> opMap = new HashMap<>();
    static {
        for (OpCode op : OpCode.values()) {
            if (opMap.containsKey(op)) {
                throw new IllegalArgumentException();
            }
            opMap.put(op.n, op);
        }
    }

    private class ScriptElement {
        public ScriptElement(int index, OpCode opcode) {
            this(index, opcode, null);
        }
        public ScriptElement(int index, OpCode op, ByteArrayWrapper data) {
            this.index = index;
            this.opcode = op;
            this.data = data;
        }
        final int index;
        private final OpCode opcode;
        private final ByteArrayWrapper data;

        @Override
        public String toString() {
            if (this.data != null) {
                StringBuilder str = new StringBuilder().append('[');
                for (int i = 0; i < data.length; i++) {
                    int x = 0xff & script[data.offset + i];
                    str.append(String.format("%02x", x));
                }
                str.append(']');
                return str.toString();
            } else {
                return this.getOpCode().toString().substring(3);
            }
        }

        public boolean isData() {
            return this.data != null;
        }

        public OpCode getOpCode() {
            return this.opcode;
        }
    }

    private static class ByteArrayWrapper {
        public ByteArrayWrapper(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
        private final Integer offset;
        private final Integer length;
    }

    private static enum OpCode {

        // DATA
        OP_DATA_00(0x00),
        OP_DATA_01(0x01),
        OP_DATA_02(0x02),
        OP_DATA_03(0x03),
        OP_DATA_04(0x04),
        OP_DATA_05(0x05),
        OP_DATA_06(0x06),
        OP_DATA_07(0x07),
        OP_DATA_08(0x08),
        OP_DATA_09(0x09),
        OP_DATA_10(0x0a),
        OP_DATA_11(0x0b),
        OP_DATA_12(0x0c),
        OP_DATA_13(0x0d),
        OP_DATA_14(0x0e),
        OP_DATA_15(0x0f),
        OP_DATA_16(0x10),
        OP_DATA_17(0x11),
        OP_DATA_18(0x12),
        OP_DATA_19(0x13),
        OP_DATA_20(0x14),
        OP_DATA_21(0x15),
        OP_DATA_22(0x16),
        OP_DATA_23(0x17),
        OP_DATA_24(0x18),
        OP_DATA_25(0x19),
        OP_DATA_26(0x1a),
        OP_DATA_27(0x1b),
        OP_DATA_28(0x1c),
        OP_DATA_29(0x1d),
        OP_DATA_30(0x1e),
        OP_DATA_31(0x1f),
        OP_DATA_32(0x20),
        OP_DATA_33(0x21),
        OP_DATA_34(0x22),
        OP_DATA_35(0x23),
        OP_DATA_36(0x24),
        OP_DATA_37(0x25),
        OP_DATA_38(0x26),
        OP_DATA_39(0x27),
        OP_DATA_40(0x28),
        OP_DATA_41(0x29),
        OP_DATA_42(0x2a),
        OP_DATA_43(0x2b),
        OP_DATA_44(0x2c),
        OP_DATA_45(0x2d),
        OP_DATA_46(0x2e),
        OP_DATA_47(0x2f),
        OP_DATA_48(0x30),
        OP_DATA_49(0x31),
        OP_DATA_50(0x32),
        OP_DATA_51(0x33),
        OP_DATA_52(0x34),
        OP_DATA_53(0x35),
        OP_DATA_54(0x36),
        OP_DATA_55(0x37),
        OP_DATA_56(0x38),
        OP_DATA_57(0x39),
        OP_DATA_58(0x3a),
        OP_DATA_59(0x3b),
        OP_DATA_60(0x3c),
        OP_DATA_61(0x3d),
        OP_DATA_62(0x3e),
        OP_DATA_63(0x3f),
        OP_DATA_64(0x40),
        OP_DATA_65(0x41),
        OP_DATA_66(0x42),
        OP_DATA_67(0x43),
        OP_DATA_68(0x44),
        OP_DATA_69(0x45),
        OP_DATA_70(0x46),
        OP_DATA_71(0x47),
        OP_DATA_72(0x48),
        OP_DATA_73(0x49),
        OP_DATA_74(0x4a),
        OP_DATA_75(0x4b),
        OP_PUSHDATA(0x4c),
        OP_PUSHDATA1(0x4d),
        OP_PUSHDATA2(0x4e),
        OP_PUSHDATA4(0x4f),

        // CONSTANTS
        OP_1NEGATE(0x4f),


        OP_1(0x51),
        OP_2(0x52),
        OP_3(0x53),
        OP_4(0x54),
        OP_5(0x55),
        OP_6(0x56),
        OP_7(0x57),
        OP_8(0x58),
        OP_9(0x59),
        OP_10(0x5a),
        OP_11(0x5b),
        OP_12(0x5c),
        OP_13(0x5d),
        OP_14(0x5e),
        OP_15(0x5f),
        OP_16(0x60),

        // FLOW CONTROL
        OP_NOP(0x61),
        OP_IF(0x63),
        OP_NOTIF(0x64),
        OP_ELSE(0x67),
        OP_ENDIF(0x68),
        OP_VERIFY(0x69),
        OP_RETURN(0x6a),

        // STACK
        OP_TOALTSTACK(0x6b),
        OP_FROMALTSTACK(0x6c),
        OP_IFDUP(0x73),
        OP_DEPTH(0x74),
        OP_DROP(0x75),
        OP_DUP(0x76),
        OP_NIP(0x77),
        OP_OVER(0x78),
        OP_PICK(0x79),
        OP_ROLL(0x7a),
        OP_ROT(0x7b),
        OP_SWAP(0x7c),
        OP_TUCK(0x7d),
        OP_2DROP(0x6d),
        OP_2DUP(0x6e),
        OP_3DUP(0x6f),
        OP_2OVER(0x70),
        OP_2ROT(0x71),
        OP_2SWAP(0x72),

        // SPLICE
        OP_CAT(0x7e),
        OP_SUBSTR(0x7f),
        OP_LEFT(0x80),
        OP_RIGHT(0x81),
        OP_SIZE(0x82),

        // BITWISE
        OP_INVERT(0x83),
        OP_AND(0x84),
        OP_OR(0x85),
        OP_XOR(0x86),
        OP_EQUAL(0x87),
        OP_EQUALVERIFY(0x88),

        // ARITHMETIC
        OP_1ADD(0x8b),
        OP_1SUB(0x8c),
        OP_2MUL(0x8d),
        OP_2DIV(0x8e),
        OP_NEGATE(0x8f),
        OP_ABS(0x90),
        OP_NOT(0x91),
        OP_0NOTEQUAL(0x92),
        OP_ADD(0x93),
        OP_SUB(0x94),
        OP_MUL(0x95),
        OP_DIV(0x96),
        OP_MOD(0x97),
        OP_LSHIFT(0x98),
        OP_RSHIFT(0x99),
        OP_BOOLAND(0x9a),
        OP_BOOLOR(0x9b),
        OP_NUMEQUAL(0x9c),
        OP_NUMEQUALVERIFY(0x9d),
        OP_NUMNOTEQUAL(0x9e),
        OP_LESSTHAN(0x9f),
        OP_GREATERTHAN(0xa0),
        OP_LESSTHANOREQUAL(0xa1),
        OP_GREATERTHANOREQUAL(0xa2),
        OP_MIN(0xa3),
        OP_MAX(0xa4),
        OP_WITHIN(0xa5),

        // CRYPTO
        OP_RIPEMD160(0xa6),
        OP_SHA1(0xa7),
        OP_SHA256(0xa8),
        OP_HASH160(0xa9),
        OP_HASH256(0xaa),
        OP_CODESEPARATOR(0xab),
        OP_CHECKSIG(0xac),
        OP_CHECKSIGVERIFY(0xad),
        OP_CHECKMULTISIG(0xae),
        OP_CHECKMULTISIGVERIFY(0xaf),

        // LOCKTIME
        OP_CHECKLOCKTIMEVERIFY(0xb1),
        OP_CHECKSEQUENCEVERIFY(0xb2),

        // PSEUDO-WORDS
        OP_PUBKEYHASH(0xfd),
        OP_PUBKEY(0xfe),
        OP_INVALIDOPCODE(0xff),

        // RESERVED WORDS
        OP_RESERVED(0x50),
        OP_VER(0x62),
        OP_VERIF(0x65),
        OP_VERNOTIF(0x66),
        OP_RESERVED1(0x89),
        OP_RESERVED2(0x8a),
        OP_NOP1(0xb0),
        OP_NOP4(0xb3),
        OP_NOP5(0xb4),
        OP_NOP6(0xb5),
        OP_NOP7(0xb6),
        OP_NOP8(0xb7),
        OP_NOP9(0xb8),
        OP_NOP10(0xb9);

        OpCode(int n) {
            this.n = n;
        }

        private int n;
    }

    public boolean isPayToPubKeyHash() {
        return this.scriptElements.size() == 5
                && OpCode.OP_DUP.equals(this.scriptElements.get(0).getOpCode())
                && OpCode.OP_HASH160.equals(this.scriptElements.get(1).getOpCode())
                && this.scriptElements.get(2).isData()
                && OpCode.OP_EQUALVERIFY.equals(this.scriptElements.get(3).getOpCode())
                && OpCode.OP_CHECKSIG.equals(this.scriptElements.get(4).getOpCode());
    }

    public boolean isPayToScriptHash() {
        //           HASH160 script_hash EQUAL
        return this.scriptElements.size() == 3
                && OpCode.OP_HASH160.equals(scriptElements.get(0).getOpCode())
                && scriptElements.get(1).isData()
                && OpCode.OP_EQUAL.equals(scriptElements.get(2));

    }

    public boolean isPayToPublicKey() {
        return scriptElements.size() == 2
                && scriptElements.get(0).isData()
                && OpCode.OP_CHECKSIG.equals(scriptElements.get(1).getOpCode());
    }


    public byte[] getPublicKey() {
        if (isPayToPublicKey()) {
            ByteArrayWrapper baw = scriptElements.get(0).data;
            return Hashes.hash160(script, baw.offset, baw.length);
        } else if (isPayToPubKeyHash()) {
            ByteArrayWrapper baw = scriptElements.get(2).data;
            byte[] pk = new byte[baw.length];
            for (int i = 0; i < pk.length; i++) {
                pk[i] = script[baw.offset + i];
            }
            return pk;
        } else {
            throw new RuntimeException("can't get public key from this type of script");
        }
    }

    public boolean isOpReturn() {
        return scriptElements.size() == 2
                && OpCode.OP_RETURN.equals(scriptElements.get(1).getOpCode())
                && scriptElements.get(2).isData();
    }

}
