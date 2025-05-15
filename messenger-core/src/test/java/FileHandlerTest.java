//import org.testng.Assert;
//import org.testng.annotations.Test;
//import ru.java.maryan.CryptoLabs.DES.FileHandler;
//import ru.java.maryan.CryptoLabs.DES.impl.DES;
//import ru.java.maryan.CryptoLabs.DES.impl.SymmetricCryptographicContext;
//import ru.java.maryan.CryptoLabs.DES.interfaces.SymmetricCryptographicAlgorithm;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
public class FileHandlerTest {
//
//    @Test
//    public void testEncryptDecryptCycle() throws IOException, ExecutionException, InterruptedException {
//        // ARRANGE
//        Path inputPath = Path.of("input.bin");
//        Path cipheredPath = Path.of("input.bin.ciphered");
//        Path decryptedPath = Path.of("input.bin.ciphered.deciphered");
//        byte[] key = {
//                (byte) 0x39, (byte) 0x86, (byte) 0xEC, (byte) 0x4D,
//                (byte) 0x5C, (byte) 0x19, (byte) 0xE9
//        };
//        SymmetricCryptographicAlgorithm des = new DES(key);
//        Map<String, Object> additionalParams = new HashMap<>();
//        additionalParams.put("delta", 1234L);
//        SymmetricCryptographicContext context = new SymmetricCryptographicContext(
//                SymmetricCryptographicContext.CipherMode.RANDOM_DELTA,
//                SymmetricCryptographicContext.PaddingMode.ZEROS,
//                new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08},
//                additionalParams,
//                des,
//                SymmetricCryptographicContext.BlockCipher.DES.getBlockSize()
//        );
//
//        // ACT
//        try (FileHandler encryptor = new FileHandler(inputPath.toString(), context, FileHandler.Mode.ENCRYPT)) {
//            encryptor.processing();
//        }
//
//        try (FileHandler decryptor = new FileHandler(cipheredPath.toString(), context, FileHandler.Mode.DECRYPT)) {
//            decryptor.processing();
//        }
//
//        // ASSERT
//        byte[] original = Files.readAllBytes(inputPath);
//        byte[] decrypted = Files.readAllBytes(decryptedPath);
//
//        Assert.assertEquals(decrypted, original, "Decrypted file does not match original input");
//    }
}