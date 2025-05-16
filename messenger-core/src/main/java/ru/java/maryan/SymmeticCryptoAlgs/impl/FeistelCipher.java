package ru.java.maryan.CryptoLabs.DES.impl;

import ru.java.maryan.CryptoLabs.DES.interfaces.EncryptionConverter;
import ru.java.maryan.CryptoLabs.DES.interfaces.SymmetricCryptographicAlgorithm;

import java.util.Arrays;

public class FeistelCipher implements SymmetricCryptographicAlgorithm {
    EncryptionConverter encryptionConverter;
    byte[][] roundKeys;

    public FeistelCipher(EncryptionConverter encryptionConverter, byte[][] roundKeys) {
        this.encryptionConverter = encryptionConverter;
        this.roundKeys = roundKeys;
    }

    public byte[] encrypt(byte[] block) {
        byte[] left = Arrays.copyOfRange(block, 0, block.length / 2);
        byte[] right = Arrays.copyOfRange(block, block.length / 2, block.length);
        for (int i = 0; i < roundKeys.length; ++i) {
            byte[] newRight = encryptionConverter.performEncryptionConversion(right, roundKeys[i]);
            byte[] temp = xor(left, newRight);
            left = right;
            right = temp;
        }
        return concatenate(right, left);
    }

    public byte[] decrypt(byte[] block) {
        byte[] right = Arrays.copyOfRange(block, 0, block.length / 2);
        byte[] left = Arrays.copyOfRange(block, block.length / 2, block.length);

        for (int i = roundKeys.length - 1; i >= 0; i--) {
            byte[] newLeft = encryptionConverter.performEncryptionConversion(left, roundKeys[i]);

            byte[] temp = xor(right, newLeft);
            right = left;
            left = temp;
        }
        return concatenate(left, right);
    }

    private byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    private byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length); // length ?
        return result;
    }
}
