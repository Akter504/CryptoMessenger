package ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia;

import ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia.CamelliaRoundFunction;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia.CamelliaRoundKeyGenerator;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.SymmetricCryptographicAlgorithm;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia.Utils.CamelliaUtils.*;

public class Camellia implements SymmetricCryptographicAlgorithm {

    private final byte[][] rk;
    private final byte[][] kw;
    private final byte[][] kl;
    private final CamelliaRoundFunction roundFunction;

    /**
     * @param key секретный ключ.
     * @param roundKeyGenerator генератор раундовых ключей rk, ключей для "отбеливания" kw, ключей для функции FL
     */
    public Camellia(byte[] key, CamelliaRoundKeyGenerator roundKeyGenerator, CamelliaRoundFunction roundFunction) {
        byte[][][] result = roundKeyGenerator.createRoundKeys(key);
        this.rk = result[0];
        this.kw = result[1];
        this.kl = result[2];
        this.roundFunction= roundFunction;
    }

    @Override
    public byte[] encrypt(byte[] data) {
        byte[] L = Arrays.copyOfRange(data, 0, 8);
        byte[] R = Arrays.copyOfRange(data, 8, 16);

        L = xor(L, kw[0]);
        R = xor(R, kw[1]);
        byte[] temp;
        for (int round = 0; round < rk.length; round++) {
            if ((round % 6) == 0 && round != 0) {
                int klIndex = round / 6 - 1;
                L = FLFunc(L, kl[klIndex * 2]);
                R = FLFuncInverse(R, kl[klIndex * 2 + 1]);
            }


            temp = roundFunction.performEncryptionConversion(L, rk[round]);
            temp = xor(R, temp);

            R = L;
            L = temp;
        }

        temp = L;
        L = R;
        R = temp;

        L = xor(L, kw[2]);
        R = xor(R, kw[3]);
        return concatenation(L, R);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        byte[] R = Arrays.copyOfRange(data, 0, 8);
        byte[] L = Arrays.copyOfRange(data, 8, 16);

        R = xor(R, kw[2]);
        L = xor(L, kw[3]);
        byte[] temp;
        for (int round = rk.length - 1; round >= 0; round--) {
            if ((round + 1) % 6 == 0 && round != rk.length - 1) {
                int klIndex = (round + 1) / 6 - 1;
                L = FLFuncInverse(L, kl[klIndex * 2]);
                R = FLFunc(R, kl[klIndex * 2 + 1]);
            }

            temp = roundFunction.performEncryptionConversion(R, rk[round]);
            temp = xor(L, temp);

            L = R;
            R = temp;
        }

        L = xor(L, kw[0]);
        R = xor(R, kw[1]);

        return concatenation(L, R);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private byte[] FLFunc(byte[] data, byte[] kl) {
        byte[] left = Arrays.copyOfRange(data, 0, 4);
        byte[] right = Arrays.copyOfRange(data, 4, 8);

        byte[] klL = Arrays.copyOfRange(kl, 0, 4);
        byte[] klR = Arrays.copyOfRange(kl, 4, 8);

        byte[] andResult = and(left, klL);
        byte[] rotated = rotateLeft(andResult, 1);
        byte[] newRight = xor(rotated, right);

        byte[] orResult = or(newRight, klR);
        byte[] newLeft = xor(orResult, left);

        return concatenation(newLeft, newRight);
    }

    private byte[] FLFuncInverse(byte[] data, byte[] kl) {
        byte[] left = Arrays.copyOfRange(data, 0, 4);
        byte[] right = Arrays.copyOfRange(data, 4, 8);

        byte[] klL = Arrays.copyOfRange(kl, 0, 4);
        byte[] klR = Arrays.copyOfRange(kl, 4, 8);

        byte[] orResult = or(right, klR);
        byte[] newLeft = xor(orResult, left);

        byte[] andResult = and(newLeft, klL);
        byte[] rotated = rotateLeft(andResult, 1);
        byte[] newRight = xor(rotated, right);

        return concatenation(newLeft, newRight);
    }
}
