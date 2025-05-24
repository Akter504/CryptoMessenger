package ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish;

import de.scravy.pair.Pair;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishEncryptionConverter;
import static ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish.TwoFishAdditionalFunctions.*;

public class TwoFishRoundFunction implements TwoFishEncryptionConverter {

    private final int mask = 0x169;

    @Override
    public byte[] performEncryptionConversion(byte[] bytes, Pair<Integer, Integer> roundKeys, int[] S) {
        int R0 = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        int R1 = (bytes[4] & 0xFF) | ((bytes[5] & 0xFF) << 8) | ((bytes[6] & 0xFF) << 16) | ((bytes[7] & 0xFF) << 24);
        int T0 = h(R0, S, mask);
        int T1 = h(ROL(R1, 8), S, mask);

        int F0 = (T0 + T1 + roundKeys.getFirst());
        int F1 = (T0 + 2 * T1 + roundKeys.getSecond());

        byte[] out = new byte[8];
        for (int i = 0; i < 4; i++) {
            out[i] = (byte) (F0 >>> (8 * i));
            out[4 + i] = (byte) (F1 >>> (8 * i));
        }
        return out;
    }
}
