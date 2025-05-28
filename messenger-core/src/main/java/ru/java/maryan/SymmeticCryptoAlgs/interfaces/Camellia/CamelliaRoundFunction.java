package ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia;

public interface CamelliaRoundFunction {
    byte[] performEncryptionConversion(byte[] data, byte[] roundKey);
}
