package ru.java.maryan.SymmeticCryptoAlgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class UtilityClassForRearrangingBits {
    public enum BitsRule {
        LSB,
        MSB
    }
    private UtilityClassForRearrangingBits() {}

    public static List<Byte> getBits(byte[] bytes, BitsRule rule) {
        boolean isLSB = (rule == BitsRule.LSB);
        List<Byte> bits = new ArrayList<>();
        for (byte b : bytes) {
            int unsignedByte = Byte.toUnsignedInt(b);
            for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                int currIndex = isLSB
                        ? bitIndex
                        : (7 - bitIndex);
                bits.add((byte) ((unsignedByte >> currIndex) & 1));
            }
        }
        return bits;
    }

    public static byte[] getBytes(int bytesLength, List<Byte> resultBits, BitsRule rule) {
        byte[] resultBytes = new byte[bytesLength];
        boolean isLSB = (rule == BitsRule.LSB);
        for (int i = 0; i < bytesLength; i++) {
            int b = 0;
            for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                int bitPos = i * 8 + bitIndex;
                if (bitPos >= resultBits.size()) break;

                if (resultBits.get(bitPos) == 1) {
                    int currIndex = isLSB ? bitIndex : (7 - bitIndex);
                    b |= (1 << currIndex);
                }
            }

            resultBytes[i] = (byte) b;
        }

        return resultBytes;
    }

    public static byte[] rerrangingBits(byte[] bytes, List<Integer> permutations, BitsRule rule, byte firstByte) {
        if (bytes == null || permutations == null || bytes.length == 0 || permutations.isEmpty()) {
            return new byte[0];
        }
        List<Byte> bits = getBits(bytes, rule);

        if (firstByte == 1) {
            permutations = permutations.stream().map(el -> (el - 1)).toList();
        }

//        List<Integer> resultBits = new ArrayList<>(Collections.nCopies(permutations.size(), 0));
        List<Byte> resultBits = Collections.nCopies(permutations.size(), 0)
                .stream()
                .map(Integer::byteValue)
                .collect(Collectors.toList());
        for (int i = 0; i < permutations.size(); i++) {
            int permIndex = permutations.get(i);
            byte temp = bits.get(permIndex);
            resultBits.set(i, temp);
        }
        return getBytes(permutations.size() / 8, resultBits, rule);

    }
}
