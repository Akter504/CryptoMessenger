package ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia;

import ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia.CamelliaRoundFunction;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia.CamelliaRoundKeyGenerator;

import java.util.Arrays;

import static ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia.Utils.CamelliaUtils.*;

public class CamelliaRoundKeyGeneratorImpl implements CamelliaRoundKeyGenerator {

    private final static long[] CONSTANTS = {
            0xA09E667F3BCC908BL, 0xB67AE8584CAA73B2L, 0xC6EF372FE94F82BEL,
            0x54FF53A5F1D36F1CL, 0x10E527FADE682D1DL, 0xB05688C2B3E6C1FDL};

    private static final int KEY_LENGTH_128 = 16;
    private static final int KEY_LENGTH_192 = 24;
    private static final int KEY_LENGTH_256 = 32;

    @Override
    public byte[][][] createRoundKeys(byte[] key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        byte[] KL = new byte[16];
        byte[] KR = new byte[16];

        if (key.length == KEY_LENGTH_128) {
            System.arraycopy(key, 0, KL, 0, 16);
        } else if (key.length == KEY_LENGTH_192) {
            System.arraycopy(key, 0, KL, 0, 16);
            System.arraycopy(key, 16, KR, 0, 8);
            for (int i = 0; i < 8; i++) KR[8 + i] = (byte) ~KR[i];
        } else if (key.length == KEY_LENGTH_256) {
            System.arraycopy(key, 0, KL, 0, 16);
            System.arraycopy(key, 16, KR, 0, 16);
        } else {
            throw new IllegalArgumentException("Invalid key length");
        }

        byte[][] auxiliaryKeys = generateAuxiliaryKeys(KL, KR);
        byte[] KA = auxiliaryKeys[0];
        byte[] KB = auxiliaryKeys[1];

        return generateSubkeys(KL, KR, KA, KB, key.length);
    }

    /**
     * @return K_A, K_B
     */
    public byte[][] generateAuxiliaryKeys(byte[] KL, byte[] KR) {
        CamelliaRoundFunction fFunction = new CamelliaRoundFunctionImpl();

        byte[] KL_L = extractLeft64(KL);
        byte[] KL_R = extractRight64(KL);
        byte[] KR_L = extractLeft64(KR);
        byte[] KR_R = extractRight64(KR);

        byte[] D1 = xor(KL_L, KR_L);
        byte[] D2 = xor(KL_R, KR_R);

        D2 = xor(D2, fFunction.performEncryptionConversion(D1, longToBytes(CONSTANTS[0], 0)));
        D1 = xor(D1, fFunction.performEncryptionConversion(D2, longToBytes(CONSTANTS[1], 0)));

        D1 = xor(D1, KL_L);
        D2 = xor(D2, KL_R);

        D2 = xor(D2, fFunction.performEncryptionConversion(D1, longToBytes(CONSTANTS[2], 0)));
        D1 = xor(D1, fFunction.performEncryptionConversion(D2, longToBytes(CONSTANTS[3], 0)));

        byte[] KA = new byte[16];
        System.arraycopy(D1, 0, KA, 0, 8);
        System.arraycopy(D2, 0, KA, 8, 8);

        byte[] KB = new byte[16];
        if (!Arrays.equals(KR, new byte[16])) {
            D1 = xor(D1, KR_L);
            D2 = xor(D2, KR_R);

            D2 = xor(D2, fFunction.performEncryptionConversion(D1, longToBytes(CONSTANTS[4], 0)));
            D1 = xor(D1, fFunction.performEncryptionConversion(D2, longToBytes(CONSTANTS[5], 0)));

            System.arraycopy(D1, 0, KB, 0, 8);
            System.arraycopy(D2, 0, KB, 8, 8);
        }

        return new byte[][] { KA, KB };
    }


    public byte[][][] generateSubkeys(byte[] KL, byte[] KR, byte[] KA, byte[] KB, int keyLength) {
        if (keyLength == KEY_LENGTH_128) {
            return generate128bitSubkeys(KL, KA);
        } else {
            return generate192_256bitSubkeys(KL, KR, KA, KB);
        }
    }

    private byte[][][] generate128bitSubkeys(byte[] KL, byte[] KA) {
        byte[][][] result = new byte[3][][];
        byte[][] k = new byte[18][];
        byte[][] kw = new byte[4][];
        byte[][] kl = new byte[4][];

        kw[0] = extractLeft64(rotateLeft(KL, 0));
        kw[1] = extractRight64(rotateLeft(KL, 0));

        k[0] = extractLeft64(rotateLeft(KA, 0));
        k[1] = extractRight64(rotateLeft(KA, 0));
        k[2] = extractLeft64(rotateLeft(KL, 15));
        k[3] = extractRight64(rotateLeft(KL, 15));
        k[4] = extractLeft64(rotateLeft(KA, 15));
        k[5] = extractRight64(rotateLeft(KA, 15));

        kl[0] = extractLeft64(rotateLeft(KA, 30));
        kl[1] = extractRight64(rotateLeft(KA, 30));

        k[6] = extractLeft64(rotateLeft(KL, 45));
        k[7] = extractRight64(rotateLeft(KL, 45));
        k[8] = extractLeft64(rotateLeft(KA, 45));
        k[9] = extractRight64(rotateLeft(KL, 60));
        k[10] = extractLeft64(rotateLeft(KA, 60));
        k[11] = extractRight64(rotateLeft(KA, 60));

        kl[2] = extractLeft64(rotateLeft(KL, 77));
        kl[3] = extractRight64(rotateLeft(KL, 77));

        k[12] = extractLeft64(rotateLeft(KL, 94));
        k[13] = extractRight64(rotateLeft(KL, 94));
        k[14] = extractLeft64(rotateLeft(KA, 94));
        k[15] = extractRight64(rotateLeft(KA, 94));
        k[16] = extractLeft64(rotateLeft(KL, 111));
        k[17] = extractRight64(rotateLeft(KL, 111));

        kw[2] = extractLeft64(rotateLeft(KA, 111));
        kw[3] = extractRight64(rotateLeft(KA, 111));

        result[0] = k;
        result[1] = kw;
        result[2] = kl;
        return result;
    }

    private byte[][][] generate192_256bitSubkeys(byte[] KL, byte[] KR, byte[] KA, byte[] KB) {
        byte[][][] result = new byte[3][][];
        byte[][] k = new byte[24][];
        byte[][] kw = new byte[4][];
        byte[][] kl = new byte[6][];

        kw[0] = extractLeft64(rotateLeft(KL, 0));
        kw[1] = extractRight64(rotateLeft(KL, 0));

        k[0] = extractLeft64(rotateLeft(KB, 0));
        k[1] = extractRight64(rotateLeft(KB, 0));
        k[2] = extractLeft64(rotateLeft(KR, 15));
        k[3] = extractRight64(rotateLeft(KR, 15));
        k[4] = extractLeft64(rotateLeft(KA, 15));
        k[5] = extractRight64(rotateLeft(KA, 15));

        kl[0] = extractLeft64(rotateLeft(KR, 30));
        kl[1] = extractRight64(rotateLeft(KR, 30));

        k[6] = extractLeft64(rotateLeft(KB, 30));
        k[7] = extractRight64(rotateLeft(KB, 30));
        k[8] = extractLeft64(rotateLeft(KL, 45));
        k[9] = extractRight64(rotateLeft(KL, 45));
        k[10] = extractLeft64(rotateLeft(KA, 45));
        k[11] = extractRight64(rotateLeft(KA, 45));

        kl[2] = extractLeft64(rotateLeft(KL, 60));
        kl[3] = extractRight64(rotateLeft(KL, 60));

        k[12] = extractLeft64(rotateLeft(KR, 60));
        k[13] = extractRight64(rotateLeft(KR, 60));
        k[14] = extractLeft64(rotateLeft(KB, 60));
        k[15] = extractRight64(rotateLeft(KB, 60));
        k[16] = extractLeft64(rotateLeft(KL, 77));
        k[17] = extractRight64(rotateLeft(KL, 77));

        kl[4] = extractLeft64(rotateLeft(KA, 77));
        kl[5] = extractRight64(rotateLeft(KA, 77));

        k[18] = extractLeft64(rotateLeft(KR, 94));
        k[19] = extractRight64(rotateLeft(KR, 94));
        k[20] = extractLeft64(rotateLeft(KA, 94));
        k[21] = extractRight64(rotateLeft(KA, 94));
        k[22] = extractLeft64(rotateLeft(KL, 111));
        k[23] = extractRight64(rotateLeft(KL, 111));

        kw[2] = extractLeft64(rotateLeft(KB, 111));
        kw[3] = extractRight64(rotateLeft(KB, 111));

        result[0] = k;
        result[1] = kw;
        result[2] = kl;
        return result;
    }
}
