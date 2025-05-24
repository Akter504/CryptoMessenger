package ru.java.maryan.SymmeticCryptoAlgs.interfaces;

import de.scravy.pair.Pair;

public interface TwoFishEncryptionConverter {
    public byte[] performEncryptionConversion(byte[] bytes, Pair<Integer, Integer> keys, int[] S);
}
