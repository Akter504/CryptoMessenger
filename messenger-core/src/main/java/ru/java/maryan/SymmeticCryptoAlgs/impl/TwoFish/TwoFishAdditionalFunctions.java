package ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish;

public final class TwoFishAdditionalFunctions {

    private static final byte[] t0_q0 = {
            (byte) 0x8, (byte) 0x1, (byte) 0x7, (byte) 0xD,
            (byte) 0x6, (byte) 0xF, (byte) 0x3, (byte) 0x2,
            (byte) 0x0, (byte) 0xB, (byte) 0x5, (byte) 0x9,
            (byte) 0xE, (byte) 0xC, (byte) 0xA, (byte) 0x4
    };

    private static final byte[] t1_q0 = {
            (byte) 0xE, (byte) 0xC, (byte) 0xB, (byte) 0x8,
            (byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x5,
            (byte) 0xF, (byte) 0x4, (byte) 0xA, (byte) 0x6,
            (byte) 0x7, (byte) 0x0, (byte) 0x9, (byte) 0xD
    };

    private static final byte[] t2_q0 = {
            (byte) 0xB, (byte) 0xA, (byte) 0x5, (byte) 0xE,
            (byte) 0x6, (byte) 0xD, (byte) 0x9, (byte) 0x0,
            (byte) 0xC, (byte) 0x8, (byte) 0xF, (byte) 0x3,
            (byte) 0x2, (byte) 0x4, (byte) 0x7, (byte) 0x1
    };

    private static final byte[] t3_q0 = {
            (byte) 0xD, (byte) 0x7, (byte) 0xF, (byte) 0x4,
            (byte) 0x1, (byte) 0x2, (byte) 0x6, (byte) 0xE,
            (byte) 0x9, (byte) 0xB, (byte) 0x3, (byte) 0x0,
            (byte) 0x8, (byte) 0x5, (byte) 0xC, (byte) 0xA
    };

    private static final byte[] t0_q1 = {
            (byte) 0x2, (byte) 0x8, (byte) 0xB, (byte) 0xD,
            (byte) 0xF, (byte) 0x7, (byte) 0x6, (byte) 0xE,
            (byte) 0x3, (byte) 0x1, (byte) 0x9, (byte) 0x4,
            (byte) 0x0, (byte) 0xA, (byte) 0xC, (byte) 0x5
    };

    private static final byte[] t1_q1 = {
            (byte) 0x1, (byte) 0xE, (byte) 0x2, (byte) 0xB,
            (byte) 0x4, (byte) 0xC, (byte) 0x3, (byte) 0x7,
            (byte) 0x6, (byte) 0xD, (byte) 0xA, (byte) 0x5,
            (byte) 0xF, (byte) 0x9, (byte) 0x0, (byte) 0x8
    };

    private static final byte[] t2_q1 = {
            (byte) 0x4, (byte) 0xC, (byte) 0x7, (byte) 0x5,
            (byte) 0x1, (byte) 0x6, (byte) 0x9, (byte) 0xA,
            (byte) 0x0, (byte) 0xE, (byte) 0xD, (byte) 0x8,
            (byte) 0x2, (byte) 0xB, (byte) 0x3, (byte) 0xF
    };

    private static final byte[] t3_q1 = {
            (byte) 0xB, (byte) 0x9, (byte) 0x5, (byte) 0x1,
            (byte) 0xC, (byte) 0x3, (byte) 0xD, (byte) 0xE,
            (byte) 0x6, (byte) 0x4, (byte) 0x7, (byte) 0xF,
            (byte) 0x2, (byte) 0x0, (byte) 0x8, (byte) 0xA
    };

    private TwoFishAdditionalFunctions() {}

    /**
     * Функция h - ключезависимое преобразование, используемое в TwoFish
     * @param x входное 32-битное слово
     * @param L массив(для ключа M_O или M_E, для g используются S-Box)
     * @param mask маска для умножения в GF(2^8)
     * @return результат преобразования
     */
    public static int h(int x, int[] L, int mask) {
        byte[] y = new byte[4];

        y[0] = (byte) (x & 0xFF);
        y[1] = (byte) ((x >> 8) & 0xFF);
        y[2] = (byte) ((x >> 16) & 0xFF);
        y[3] = (byte) ((x >> 24) & 0xFF);

        int k = L.length;
        if (k >= 4) {
            y[0] = (byte)(q1(y[0]) ^ ((L[3] >> 24) & 0xFF));
            y[1] = (byte)(q0(y[1]) ^ ((L[3] >> 16) & 0xFF));
            y[2] = (byte)(q0(y[2]) ^ ((L[3] >> 8) & 0xFF));
            y[3] = (byte)(q1(y[3]) ^ (L[3] & 0xFF));
        }

        if (k >= 3) {
            y[0] = (byte)(q1(y[0]) ^ ((L[2] >> 24) & 0xFF));
            y[1] = (byte)(q1(y[1]) ^ ((L[2] >> 16) & 0xFF));
            y[2] = (byte)(q0(y[2]) ^ ((L[2] >> 8) & 0xFF));
            y[3] = (byte)(q0(y[3]) ^ (L[2] & 0xFF));
        }

        y[0] = q1((byte) (q0((byte) (q0(y[0]) ^ ((L[1] >> 24) & 0xFF))) ^ ((L[0] >> 24) & 0xFF)));
        y[1] = q0((byte) (q0((byte) (q1(y[1]) ^ ((L[1] >> 16) & 0xFF))) ^ ((L[0] >> 16) & 0xFF)));
        y[2] = q1((byte) (q1((byte) (q0(y[2]) ^ ((L[1] >> 8) & 0xFF))) ^ ((L[0] >> 8) & 0xFF)));
        y[3] = q0((byte) (q1((byte) (q1(y[3]) ^ (L[1] & 0xFF))) ^ (L[0] & 0xFF)));

        return mdsMultiply(y, mask);
    }

    public static byte q0(byte x) {
        return processQ(x, t0_q0, t1_q0, t2_q0, t3_q0);
    }

    public static byte q1(byte x) {
        return processQ(x, t0_q1, t1_q1, t2_q1, t3_q1);
    }

    /**
     * Перестановки q0 и q1
     * @param x входной байт
     * @param t0-t3 таблицы замен
     * @return преобразованный байт
     */
    private static byte processQ(byte x, byte[] t0, byte[] t1, byte[] t2, byte[] t3) {
        int a0 = (x >>> 4) & 0xF;
        int b0 = x & 0xF;

        int a1 = a0 ^ b0;
        int b1 = a0 ^ ror4(b0, 1) ^ ((8 * a0) & 0xF);

        int a2 = t0[a1 & 0xF] & 0xF;
        int b2 = t1[b1 & 0xF] & 0xF;

        int a3 = a2 ^ b2;
        int b3 = a2 ^ ror4(b2, 1) ^ ((8 * a2) & 0xF);

        int a4 = t2[a3 & 0xF] & 0xF;
        int b4 = t3[b3 & 0xF] & 0xF;

        return (byte)((b4 << 4) | a4);
    }

    private static int ror4(int val, int n) {
        return ((val >>> n) | (val << (4 - n))) & 0xF;
    }

    /**
     * Умножение с MDS матрицей
     * @param y входной массив байтов
     * @param mask маска для GF(2^8)
     * @return результат умножения
     */
    public static int mdsMultiply(byte[] y, int mask) {
        int[] result = new int[4];
        int[][] MDS = {
                {0x01, 0xEF, 0x5B, 0x5B},
                {0x5B, 0xEF, 0xEF, 0x01},
                {0xEF, 0x5B, 0x01, 0xEF},
                {0xEF, 0x01, 0xEF, 0x5B}
        };

        for (int i = 0; i < 4; i++) {
            int acc = 0;
            for (int j = 0; j < 4; j++) {
                acc ^= multiplyInGF(MDS[i][j], y[j] & 0xFF, mask);
            }
            result[i] = acc;
        }

        return (result[0] & 0xFF) | ((result[1] & 0xFF) << 8) |
                ((result[2] & 0xFF) << 16) | ((result[3] & 0xFF) << 24);
    }

    /**
     * Умножение в поле Галуа GF(2^8)
     * @param a первый операнд
     * @param b второй операнд
     * @param mask примитивный полином (0x169 для g-функции, и 0x14D для нахождения S-box)
     * @return результат умножения
     */
    public static int multiplyInGF(int a, int b, int mask) {
        int res = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) res ^= a;
            boolean carry = (a & 0x80) != 0;
            a <<= 1;
            if (carry) a ^= mask;
            b >>= 1;
        }
        return res & 0xFF;
    }

    /**
     * @param data сообщение или ключ.
     * разбиение
     */
    public static int[] convertDataToParts(byte[] data) {
        int[] parts = new int[4];
        for (int i = 0; i < parts.length; i++) {
            int result = 0;
            for (int j = 0; j < parts.length; j++) {
                result |= ((data[4*i + j] & 0xFF) << (8*j));
            }
            parts[i] = result;
        }
        return parts;
    }

    public static byte[] convertPartsToData(int[] parts) {
        byte[] data = new byte[16];
        for (int i = 0; i < 4; i++) {
            data[4 * i] = (byte) (parts[i]);
            data[4 * i + 1] = (byte) (parts[i] >>> 8);
            data[4 * i + 2] = (byte) (parts[i] >>> 16);
            data[4 * i + 3] = (byte) (parts[i] >>> 24);
        }
        return data;
    }

    public static int ROL(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    public static int ROR(int x, int n) {
        return (x >>> n) | (x << (32 - n));
    }
}
