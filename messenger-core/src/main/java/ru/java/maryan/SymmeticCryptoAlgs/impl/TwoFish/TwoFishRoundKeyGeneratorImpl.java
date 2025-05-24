package ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish;

import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishRoundKeyGenerator;

import static ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish.TwoFishAdditionalFunctions.*;

public class TwoFishRoundKeyGeneratorImpl implements TwoFishRoundKeyGenerator {

    private final int maskKey = 0x169;
    private final int rho = 0x01010101;

    @Override
    public int[] createRoundKeys(int[] M) {
        int[] roundKeys = new int[40];
        int[] M_E = divisionIntoEven(M);
        int[] M_O = divisionIntoOdd(M);
        for (int i = 0; i < 20; i++) {
            int A = h(2 * i * rho, M_E, maskKey);
            int B = h((2 * i + 1) * rho, M_O, maskKey);
            B = ROL(B, 8);
            roundKeys[2 * i] = A + B;
            roundKeys[2 * i + 1] = ROL(A + 2 * B, 9);
        }
        return roundKeys;
    }

    private int[] divisionIntoEven(int[] M) {
        int[] M_E = new int[M.length / 2 + M.length % 2];
        int index = 0;
        for (int j = 0; j < M.length; j +=2) {
            M_E[index++] = M[j];
        }
        return M_E;
    }

    private int[] divisionIntoOdd(int[] M) {
        int[] M_O = new int[M.length / 2];
        int index = 0;
        for (int j = 1; j < M.length; j +=2) {
            M_O[index++] = M[j];
        }
        return M_O;
    }
}
