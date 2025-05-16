package ru.java.maryan.SymmeticCryptoAlgs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
//        byte[] originalMessage =  {
//                (byte) 0x49, (byte) 0x6C, (byte) 0x79, (byte) 0x61, (byte) 0x47, (byte) 0x61, (byte) 0x79, (byte) 0x21,
//                (byte) 0x23, (byte) 0x23
//        };
//        //byte[] key = {1, 2, 3, 4, 5, 6, 7, 8};
        byte[] key = { // 64-bit version: 39C3BB89D5E167D3
                (byte) 0x39, (byte) 0xC3, (byte) 0xBB, (byte) 0x89, (byte) 0xD5, (byte) 0xE1, (byte) 0x67, (byte) 0xD3
                //(byte) 0x39, (byte) 0x86, (byte) 0xEC, (byte) 0x4D, (byte) 0x5C, (byte) 0x19, (byte) 0xE9
//                (byte) 0x39, (byte) 0xC3, (byte) 0xBB, (byte) 0x89, (byte) 0xD5, (byte) 0xE1, (byte) 0x67, (byte) 0xD3,
//                (byte) 0x39, (byte) 0xC3, (byte) 0xBB, (byte) 0x89, (byte) 0xD5, (byte) 0xE1, (byte) 0x67, (byte) 0xD3
        };
//        RoundKeyGenerator roundKeyGenerator = new DESRoundKeyGenerator();
//        EncryptionConverter encryptionConverter = new EncryptionConverterImpl();
//        SymmetricCryptographicAlgorithm des = new DES(key);
//        Map<String, Object> additionalParams = new HashMap<>();
//        additionalParams.put("delta", 1234L);
//        SymmetricCryptographicContext SCContext = new SymmetricCryptographicContext(
//                SymmetricCryptographicContext.CipherMode.CBC,
//                SymmetricCryptographicContext.PaddingMode.PKCS7,
//                new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08},
//                additionalParams,
//                des
//        );
//        //DEAL deal = new DEAL(key, new DEALRoundKeyGenerator(), new EncryptionConverterImpl());
//        CompletableFuture<byte[]> encryptedData = SCContext
//                .symmetricalEncrypt(originalMessage, SymmetricCryptographicContext.BlockCipher.DES.getBlockSize());
//        try {
//            byte[] encrypted = encryptedData.get();
//
//            logger.trace("Raw encrypted data: {}", Arrays.toString(encrypted));
//            logger.debug("Hex encrypted: {}", bytesToHex(encrypted));
//
//            String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
//            logger.info("Encrypted (Base64): {}", encryptedBase64);
//            logger.debug("Original message: {}", new String(originalMessage));
//
//            CompletableFuture<byte[]> decryptedData = SCContext
//                    .symmetricalDecrypt(encrypted, SymmetricCryptographicContext.BlockCipher.DES.getBlockSize());
//            byte[] decrypted = decryptedData.get();
//
//            logger.debug("Decrypted message: {}", new String(decrypted));
//
//            if (Arrays.equals(originalMessage, decrypted)) {
//                logger.info("Decryption successful");
//            } else {
//                logger.error("Decryption failed! Original and decrypted data mismatch");
//                logger.debug("Original: {} | Decrypted: {}",
//                        Arrays.toString(originalMessage),
//                        Arrays.toString(decrypted));
//            }
//
//        } catch (InterruptedException e) {
//            logger.error("Encryption/decryption interrupted", e);
//            Thread.currentThread().interrupt();
//        } catch (ExecutionException e) {
//            logger.error("Error during crypto operations", e);
//        }
//        SymmetricCryptographicAlgorithm des = new DES(key);
//        SymmetricCryptographicContext symmetricCC = new SymmetricCryptographicContext(
//                SymmetricCryptographicContext.CipherMode.CBC,
//                SymmetricCryptographicContext.PaddingMode.ZEROS,
//                new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08},
//                null,
//                des,
//                SymmetricCryptographicContext.BlockCipher.DES.getBlockSize()
//        );
//        try {
//            FileHandler fileHandler = new FileHandler("input.bin", symmetricCC, FileHandler.Mode.ENCRYPT);
//            fileHandler.processing();
//
//            fileHandler.changeMode(FileHandler.Mode.DECRYPT);
//            fileHandler.changeFileName("input.bin.ciphered");
//            fileHandler.processing();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
}