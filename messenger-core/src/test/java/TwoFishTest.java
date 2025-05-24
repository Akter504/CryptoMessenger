import org.junit.Test;
import org.testng.Assert;
import ru.java.maryan.AsymmetricCryptoAlgs.impl.DiffieHellman;
import ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish.TwoFish;
import ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish.TwoFishRoundFunction;
import ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish.TwoFishRoundKeyGeneratorImpl;
import ru.java.maryan.SymmeticCryptoAlgs.impl.TwoFish.TwoFishSGeneratorImpl;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishEncryptionConverter;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishRoundKeyGenerator;
import ru.java.maryan.SymmeticCryptoAlgs.interfaces.TwoFishSGenerator;

import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.Assert.assertArrayEquals;

public class TwoFishTest {
    private static final HexFormat hex = HexFormat.of();

    // Тестовые векторы из документации.
    private static final byte[] ZERO_KEY = hex.parseHex("00000000000000000000000000000000");
    private static final byte[] ZERO_PLAINTEXT = hex.parseHex("00000000000000000000000000000000");
    private static final byte[] EXPECTED_CIPHERTEXT = hex.parseHex("9F589F5CF6122C32B6BFEC2F2AE8C35A");
    @Test
    public void testEncrypt() {
        TwoFishSGenerator sGen = new TwoFishSGeneratorImpl();
        TwoFishRoundKeyGenerator keyGen = new TwoFishRoundKeyGeneratorImpl();
        TwoFishEncryptionConverter converter = new TwoFishRoundFunction();
        TwoFish twoFish = new TwoFish(ZERO_KEY, keyGen, sGen, converter);

        byte[] actualCiphertext = twoFish.encrypt(ZERO_PLAINTEXT);

        assertArrayEquals("Ciphertext should match official test vector",
                EXPECTED_CIPHERTEXT, actualCiphertext);
    }

    @Test
    public void testDecrypt() {
        TwoFishSGenerator sGen = new TwoFishSGeneratorImpl();
        TwoFishRoundKeyGenerator keyGen = new TwoFishRoundKeyGeneratorImpl();
        TwoFishEncryptionConverter converter = new TwoFishRoundFunction();
        TwoFish twoFish = new TwoFish(ZERO_KEY, keyGen, sGen, converter);

        byte[] actualText = twoFish.decrypt(EXPECTED_CIPHERTEXT);

        assertArrayEquals("The decrypted text must match the official test vector.",
                ZERO_PLAINTEXT, actualText);
    }
}