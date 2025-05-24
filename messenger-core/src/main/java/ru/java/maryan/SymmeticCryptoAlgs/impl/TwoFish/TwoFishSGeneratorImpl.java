package ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish;

import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishSGenerator;

import java.util.Arrays;

import static ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish.TwoFishAdditionalFunctions.multiplyInGF;

public class TwoFishSGeneratorImpl implements TwoFishSGenerator {

    private final int maskRS = 0x14D;

    private static final byte[][] RS = {
            {(byte)0x01, (byte)0xA4, (byte)0x55, (byte)0x87, (byte)0x5A, (byte)0x58, (byte)0xDB, (byte)0x9E},
            {(byte)0xA4, (byte)0x56, (byte)0x82, (byte)0xF3, (byte)0x1E, (byte)0xC6, (byte)0x68, (byte)0xE5},
            {(byte)0x02, (byte)0xA1, (byte)0xFC, (byte)0xC1, (byte)0x47, (byte)0xAE, (byte)0x3D, (byte)0x19},
            {(byte)0xA4, (byte)0x55, (byte)0x87, (byte)0x5A, (byte)0x58, (byte)0xDB, (byte)0x9E, (byte)0x03}
    };

    @Override
    public int[] createS(int[] M) {
        int k = M.length;
        byte[] keyBytes = new byte[k * 4];

        for (int i = 0; i < k; i++) {
            keyBytes[4 * i] = (byte) (M[i] & 0xFF);
            keyBytes[4 * i + 1] = (byte) ((M[i] >> 8) & 0xFF);
            keyBytes[4 * i + 2] = (byte) ((M[i] >> 16) & 0xFF);
            keyBytes[4 * i + 3] = (byte) ((M[i] >> 24) & 0xFF);
        }

        int[] S = new int[k / 2];
        for (int i = 0; i < (k / 2); i++) {
            byte[] block = Arrays.copyOfRange(keyBytes, 8*i, Math.min(8*i + 8, keyBytes.length));

            int[] s_i = new int[4];
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < block.length; col++) {
                    s_i[row] ^= multiplyInGF(RS[row][col], block[col] & 0xFF, maskRS);
                }
            }

            S[(k / 2) - 1 - i] = (s_i[0] & 0xFF) | ((s_i[1] & 0xFF) << 8) |
                    ((s_i[2] & 0xFF) << 16) | (((s_i[3] & 0xFF) << 24));
        }
        return S;
    }
}
