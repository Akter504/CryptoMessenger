package ru.java.maryan.SymmeticCryptoAlgs.interfaces;

public interface SymmetricCryptographicAlgorithm {

    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);
}
