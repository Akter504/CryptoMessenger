package ru.java.maryan.CryptoLabs.DES.interfaces;

public interface EncryptionConverter {
    public byte[] performEncryptionConversion(byte[] bytes, byte[] roundKey);
}
