import org.junit.Test;
import org.testng.Assert;
import ru.java.maryan.CryptoLabs.DES.impl.EncryptionConverterImpl;
import ru.java.maryan.CryptoLabs.DES.impl.FeistelCipher;
import ru.java.maryan.CryptoLabs.DES.interfaces.EncryptionConverter;
import ru.java.maryan.CryptoLabs.DES.interfaces.SymmetricCryptographicAlgorithm;

import java.util.stream.IntStream;

public class FeistelNetworkTest {

  EncryptionConverter feistelFunction = new EncryptionConverterImpl() {
    @Override
    public byte[] performEncryptionConversion(byte[] data, byte[] key) {
      byte[] res = new byte[data.length];
      IntStream.range(0, data.length).forEach(i -> res[i] = (byte) (data[i] ^ key[i]));
      return res;
    }
  };

  @Test
  public void testFeistelNetworkEncryption() {
    // SETUP
    byte[][] roundKeys = {
        { (byte) 0x58, (byte) 0xE2 },
        { (byte) 0x42, (byte) 0xE6 },
    };
    byte[] message = {
        (byte) 0x54, (byte) 0x76, (byte) 0x65, (byte) 0x4B
    };
    byte[] expectedCipherText = {
        (byte) 0x4E, (byte) 0x72, (byte) 0x69, (byte) 0xDF
    };

    // EXECUTION
    SymmetricCryptographicAlgorithm feistelCipher = new FeistelCipher(feistelFunction, roundKeys);

    byte[] actualCipherText = feistelCipher.encrypt(message);

    // ASSERTION
    Assert.assertEquals(expectedCipherText, actualCipherText);
  }

  @Test
   public void testFeistelNetworkDecryption() {
    // SETUP
    byte[][] roundKeys = {
        { (byte) 0x58, (byte) 0xE2 },
        { (byte) 0x42, (byte) 0xE6 },
    };
    byte[] cipherText = {
        (byte) 0x4E, (byte) 0x72, (byte) 0x69, (byte) 0xDF
    };
    byte[] expectedMessage = {
        (byte) 0x54, (byte) 0x76, (byte) 0x65, (byte) 0x4B
    };

    // EXECUTION
    SymmetricCryptographicAlgorithm feistelCipher = new FeistelCipher(feistelFunction, roundKeys);

    byte[] decryptedMessage = feistelCipher.decrypt(cipherText);

    // ASSERTION
    Assert.assertEquals(expectedMessage, decryptedMessage);
  }

  @Test
  public void testFeistelNetworkCycle() {
    // SETUP
    byte[][] roundKeys = {
        { (byte) 0x58, (byte) 0xE2 },
        { (byte) 0x42, (byte) 0xE6 },
        { (byte) 0x99, (byte) 0x26 },
        { (byte) 0x44, (byte) 0xA4 },
        { (byte) 0xFD, (byte) 0x50 },
        { (byte) 0xDA, (byte) 0xAE },
        { (byte) 0x75, (byte) 0x00 },
        { (byte) 0x8D, (byte) 0xFB },
    };

    byte[] message = {
        (byte) 0x18, (byte) 0x7C, (byte) 0xAF, (byte) 0x99
    };

    // EXECUTION
  SymmetricCryptographicAlgorithm feistelCipher = new FeistelCipher(feistelFunction, roundKeys);

    byte[] decryptedMessage = feistelCipher.decrypt(feistelCipher.encrypt(message));

    // ASSERTION
    Assert.assertEquals(message, decryptedMessage);
  }
}
