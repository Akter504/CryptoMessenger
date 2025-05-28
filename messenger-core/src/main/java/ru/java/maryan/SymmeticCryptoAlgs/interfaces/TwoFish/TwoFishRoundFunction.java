package ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFish;

import de.scravy.pair.Pair;

public interface TwoFishRoundFunction {
    public byte[] performEncryptionConversion(byte[] bytes, Pair<Integer, Integer> keys, int[] S);
}
