package ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish;

import de.scravy.pair.Pair;
import de.scravy.pair.Pairs;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishEncryptionConverter;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.SymmetricCryptographicAlgorithm;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishRoundKeyGenerator;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishSGenerator;

import java.util.Arrays;

import static ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish.TwoFishAdditionalFunctions.*;

public class TwoFish implements SymmetricCryptographicAlgorithm {

    private final int[] M;
    private final int[] roundKeys;
    private final int[] S;
    private final TwoFishEncryptionConverter roundFunction;

    public TwoFish(byte[] key, TwoFishRoundKeyGenerator keyGenerator, TwoFishSGenerator sGenerator,
                   TwoFishEncryptionConverter roundFunction) {
        this.M = convertDataToParts(key);
        this.roundKeys = keyGenerator.createRoundKeys(M);
        this.S = sGenerator.createS(M);
        this.roundFunction = roundFunction;
    }

    @Override
    public byte[] encrypt(byte[] data) {
        int[] R = convertDataToParts(data);

        inputWhitening(R);

        for (int r = 0; r < 16; r++) {
            byte[] inputBlock = packBlock(R[0], R[1]);
            Pair<Integer, Integer> keys = Pairs.from(roundKeys[2*r + 8], roundKeys[2*r + 9]);
            byte[] F = roundFunction.performEncryptionConversion(inputBlock, keys, S);

            int F0 = unpackWord(F, 0);
            int F1 = unpackWord(F, 4);

            int tempR2 = R[2];
            int tempR3 = R[3];

            R[2] = ROR(tempR2 ^ F0, 1);
            R[3] = ROL(tempR3, 1) ^ F1;

            if (r != 15) {
                int temp0 = R[0];
                int temp1 = R[1];
                R[0] = R[2];
                R[1] = R[3];
                R[2] = temp0;
                R[3] = temp1;
            }
        }

        outputWhitening(R);

        return convertPartsToData(R);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        int[] R = convertDataToParts(data);

        outputWhitening(R);

        for (int r = 15; r >= 0; r--) {
            if (r != 15) {
                int temp0 = R[0];
                int temp1 = R[1];
                R[0] = R[2];
                R[1] = R[3];
                R[2] = temp0;
                R[3] = temp1;
            }

            byte[] inputBlock = packBlock(R[0], R[1]);
            Pair<Integer, Integer> keys = Pairs.from(roundKeys[2 * r + 8], roundKeys[2 * r + 9]);
            byte[] F = roundFunction.performEncryptionConversion(inputBlock, keys, S);

            int F0 = unpackWord(F, 0);
            int F1 = unpackWord(F, 4);

            int tempR2 = R[2];
            int tempR3 = R[3];

            R[2] = ROL(tempR2, 1) ^ F0;
            R[3] = ROR(tempR3 ^ F1, 1);
        }

        inputWhitening(R);

        return convertPartsToData(R);
    }

    private void inputWhitening(int[] R) {
        for (int i = 0; i < 4; i++) {
            R[i] ^= roundKeys[i];
        }
    }

    private void outputWhitening(int[] R) {
        for (int i = 0; i < 4; i++) {
            R[i] ^= roundKeys[i + 4];
        }
    }

    private byte[] packBlock(int R0, int R1) {
        byte[] out = new byte[8];
        for (int i = 0; i < 4; i++) {
            out[i] = (byte) (R0 >>> (8 * i));
            out[4 + i] = (byte) (R1 >>> (8 * i));
        }
        return out;
    }

    private int unpackWord(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
                | ((bytes[offset + 2] & 0xFF) << 16)
                | ((bytes[offset + 3] & 0xFF) << 24);
    }

}
