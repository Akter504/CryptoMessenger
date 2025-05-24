package ru.java.maryan.SymmeticCryptoAlgs.interfaces;

public interface EncryptionConverter {
    byte[] performEncryptionConversion(byte[] bytes, byte[] roundKey);
}
