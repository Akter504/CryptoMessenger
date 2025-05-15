package ru.java.maryan.CryptoLabs.DES.interfaces;

public interface SymmetricCryptographicAlgorithm {

    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);
}
