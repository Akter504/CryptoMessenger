package ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia;

public interface CamelliaRoundKeyGenerator {
    byte[][][] createRoundKeys(byte[] key);
}
