package ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public final class CamelliaUtils {
    private CamelliaUtils() {};

    public static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    public static byte[] and(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte)(a[i] & b[i]);
        }
        return result;
    }

    public static byte[] or(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte)(a[i] | b[i]);
        }
        return result;
    }

    public static byte[] concatenation(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static byte[] rotateLeft(byte[] input, int bits) {
        int byteLen = input.length;
        int byteShift = bits / 8;
        int bitShift = bits % 8;

        byte[] rotated = new byte[byteLen];

        for (int i = 0; i < byteLen; i++) {
            int from = (i + byteShift) % byteLen;
            int fromNext = (from + 1) % byteLen;

            int left = (input[from] & 0xFF) << bitShift;
            int right = (input[fromNext] & 0xFF) >>> (8 - bitShift);

            int combined = (bitShift == 0) ? (input[from] & 0xFF) : (left | right) & 0xFF;

            rotated[i] = (byte) combined;
        }

        return rotated;
    }


    public static byte[] longToBytes(long high, long low) {
        ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(high).putLong(low);
        return buffer.array();
    }

    public static byte[] extractLeft64(byte[] block) {
        return Arrays.copyOfRange(block, 0, 8);
    }

    public static byte[] extractRight64(byte[] block) {
        return Arrays.copyOfRange(block, 8, 16);
    }
}