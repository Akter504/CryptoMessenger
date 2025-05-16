package ru.java.maryan.CryptoLabs.DES.interfaces;

public interface RoundKeyGenerator {
    public byte[][] createRoundKeys(byte[] key);
}
