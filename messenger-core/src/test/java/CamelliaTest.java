import org.junit.Test;
import ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia.Camellia;
import ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia.CamelliaRoundFunctionImpl;
import ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia.CamelliaRoundKeyGeneratorImpl;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia.CamelliaRoundFunction;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia.CamelliaRoundKeyGenerator;
import java.util.HexFormat;

import static org.junit.Assert.assertArrayEquals;

public class CamelliaTest {
    private static final HexFormat hex = HexFormat.of();

    // Тестовые векторы из документации.
    private static final byte[] KEY128Bit = hex.parseHex("0123456789ABCDEFFEDCBA9876543210");
    private static final byte[] PLAINTEXT128Bit = hex.parseHex("0123456789ABCDEFFEDCBA9876543210");
    private static final byte[] EXPECTED_CIPHERTEXT128Bit = hex.parseHex("67673138549669730857065648EABE43");

    private static final byte[] KEY192Bit = hex.parseHex("0123456789ABCDEFFEDCBA98765432100011223344556677");
    private static final byte[] PLAINTEXT192Bit = hex.parseHex("0123456789ABCDEFFEDCBA9876543210");
    private static final byte[] EXPECTED_CIPHERTEXT192Bit = hex.parseHex("B4993401B3E996F84EE5CEE7D79B09B9");

    private static final byte[] KEY256Bit = hex.parseHex("0123456789ABCDEFFEDCBA987654321000112233445566778899AABBCCDDEEFF");
    private static final byte[] PLAINTEXT256Bit = hex.parseHex("0123456789ABCDEFFEDCBA9876543210");
    private static final byte[] EXPECTED_CIPHERTEXT256Bit = hex.parseHex("9ACC237DFF16D76C20EF7C919E3A7509");


    @Test
    public void testEncryptKey128bit() {
        CamelliaRoundKeyGenerator keyGen = new CamelliaRoundKeyGeneratorImpl();
        CamelliaRoundFunction converter = new CamelliaRoundFunctionImpl();
        Camellia camellia = new Camellia(KEY128Bit, keyGen, converter);

        byte[] actualCiphertext = camellia.encrypt(PLAINTEXT128Bit);

        System.out.println("Actual ciphertext: " + bytesToHex(actualCiphertext));
        System.out.println("Expected ciphertext: " + bytesToHex(EXPECTED_CIPHERTEXT128Bit));


        assertArrayEquals("Ciphertext should match official test vector",
                EXPECTED_CIPHERTEXT128Bit, actualCiphertext);
    }

    @Test
    public void testDecryptKey128bit() {
        CamelliaRoundKeyGenerator keyGen = new CamelliaRoundKeyGeneratorImpl();
        CamelliaRoundFunction converter = new CamelliaRoundFunctionImpl();
        Camellia camellia = new Camellia(KEY128Bit, keyGen, converter);

        byte[] actualText = camellia.decrypt(EXPECTED_CIPHERTEXT128Bit);

        System.out.println("Actual text: " + bytesToHex(PLAINTEXT128Bit));
        System.out.println("Expected ciphertext: " + bytesToHex(actualText));


        assertArrayEquals("The decrypted text must match the official test vector.",
                PLAINTEXT128Bit, actualText);
    }

    @Test
    public void testEncryptKey192bit() {
        CamelliaRoundKeyGenerator keyGen = new CamelliaRoundKeyGeneratorImpl();
        CamelliaRoundFunction converter = new CamelliaRoundFunctionImpl();
        Camellia camellia = new Camellia(KEY192Bit, keyGen, converter);

        byte[] actualCiphertext = camellia.encrypt(PLAINTEXT192Bit);

        System.out.println("Actual ciphertext: " + bytesToHex(actualCiphertext));
        System.out.println("Expected ciphertext: " + bytesToHex(EXPECTED_CIPHERTEXT192Bit));


        assertArrayEquals("Ciphertext should match official test vector",
                EXPECTED_CIPHERTEXT192Bit, actualCiphertext);
    }

    @Test
    public void testDecryptKey192bit() {
        CamelliaRoundKeyGenerator keyGen = new CamelliaRoundKeyGeneratorImpl();
        CamelliaRoundFunction converter = new CamelliaRoundFunctionImpl();
        Camellia camellia = new Camellia(KEY192Bit, keyGen, converter);

        byte[] actualText = camellia.decrypt(EXPECTED_CIPHERTEXT192Bit);

        System.out.println("Actual text: " + bytesToHex(PLAINTEXT192Bit));
        System.out.println("Expected ciphertext: " + bytesToHex(actualText));


        assertArrayEquals("The decrypted text must match the official test vector.",
                PLAINTEXT192Bit, actualText);
    }

    @Test
    public void testEncryptKey256bit() {
        CamelliaRoundKeyGenerator keyGen = new CamelliaRoundKeyGeneratorImpl();
        CamelliaRoundFunction converter = new CamelliaRoundFunctionImpl();
        Camellia camellia = new Camellia(KEY256Bit, keyGen, converter);

        byte[] actualCiphertext = camellia.encrypt(PLAINTEXT256Bit);

        System.out.println("Actual ciphertext: " + bytesToHex(actualCiphertext));
        System.out.println("Expected ciphertext: " + bytesToHex(EXPECTED_CIPHERTEXT256Bit));


        assertArrayEquals("Ciphertext should match official test vector",
                EXPECTED_CIPHERTEXT256Bit, actualCiphertext);
    }

    @Test
    public void testDecryptKey256bit() {
        CamelliaRoundKeyGenerator keyGen = new CamelliaRoundKeyGeneratorImpl();
        CamelliaRoundFunction converter = new CamelliaRoundFunctionImpl();
        Camellia camellia = new Camellia(KEY256Bit, keyGen, converter);

        byte[] actualText = camellia.decrypt(EXPECTED_CIPHERTEXT256Bit);

        System.out.println("Actual text: " + bytesToHex(PLAINTEXT256Bit));
        System.out.println("Expected ciphertext: " + bytesToHex(actualText));


        assertArrayEquals("The decrypted text must match the official test vector.",
                PLAINTEXT256Bit, actualText);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
