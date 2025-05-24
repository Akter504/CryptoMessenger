package ru.java.maryan.SymmeticCryptoAlgs.interfaces;

public interface TwoFishRoundKeyGenerator {
    int[] createRoundKeys(int[] M);
}
