package ru.java.maryan.CryptoLabs.DES.impl;


import ru.java.maryan.CryptoLabs.DES.interfaces.SymmetricCryptographicAlgorithm;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class SymmetricCryptographicContext {
    public enum CipherMode {
        ECB,
        CBC,
        PCBC,
        CFB,
        OFB,
        CTR,
        RANDOM_DELTA
    }

    public enum PaddingMode {
        ZEROS,
        X923,
        PKCS7,
        ISO10126
    }

    public enum BlockCipher {
        DES(8),
        DEAL128(16),
        DEAL192(24),
        DEAL256(32);

        private final int blockSize;

        BlockCipher(int blockSize) {
            this.blockSize = blockSize;
        }

        public int getBlockSize() {
            return blockSize;
        }
    }

    private CipherMode cipherMode;
    private PaddingMode paddingMode;
    private byte[] iv = new byte[8];
    private Map<String, Object> additionalParams;
    private SymmetricCryptographicAlgorithm SCAlgorithm;
    private int blockSize;

    private byte[] lastEncryptBlock = null;
    private byte[] lastDecryptBlock = null;

    public SymmetricCryptographicContext(CipherMode cipherMode,
                                         PaddingMode paddingMode, byte[] iv,
                                         Map<String, Object> additionalParams,
                                         SymmetricCryptographicAlgorithm SCAlgorithm,
                                         int blockSize) {
        this.cipherMode = cipherMode;
        this.paddingMode = paddingMode;
        this.additionalParams = additionalParams;
        this.SCAlgorithm = SCAlgorithm;
        this.blockSize = blockSize;
        if (iv != null && iv.length != 0) {
            this.iv = iv;
        }
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public CompletableFuture<byte[]> symmetricalEncrypt(byte[] data) {
        lastDecryptBlock = null;
        return CompletableFuture.supplyAsync(() -> switch (cipherMode) {
            case ECB -> ecbEncrypt(data);
            case CBC -> cbcEncrypt(data);
            case PCBC -> pcbcEncrypt(data);
            case CFB -> cfbEncrypt(data);
            case OFB -> ofbEncrypt(data);
            case CTR -> ctrEncrypt(data);
            case RANDOM_DELTA -> randomDeltaEncrypt(data);
        });
    }

    public CompletableFuture<byte[]> symmetricalDecrypt(byte[] data) {
        lastEncryptBlock = null;
        return CompletableFuture.supplyAsync(() -> switch (cipherMode) {
            case ECB -> ecbDecrypt(data);
            case CBC -> cbcDecrypt(data);
            case PCBC -> pcbcDecrypt(data);
            case CFB -> cfbDecrypt(data);
            case OFB -> ofbDecrypt(data);
            case CTR -> ctrDecrypt(data);
            case RANDOM_DELTA -> randomDeltaDecrypt(data);
        });
    }

    private byte[] fillBlock(byte[] block) {
        return switch (paddingMode) {
            case ZEROS -> zerosPadding(block);
            case PKCS7 -> PKCS7Padding(block);
            case X923 -> X923Padding(block);
            case ISO10126 -> ISO10126Padding(block);
        };
    }

    private byte[] unFillBlock(byte[] block) {
        return switch (paddingMode) {
            case ZEROS -> zerosUnPadding(block);
            case PKCS7 -> PKCS7UnPadding(block);
            case X923 -> X923UnPadding(block);
            case ISO10126 -> ISO10126UnPadding(block);
        };
    }

    private byte[] ecbEncrypt(byte[] data) {
        byte[] encryptedData = new byte[data.length + (blockSize - data.length % blockSize)];
        int countBlocks = data.length / blockSize;
        IntStream.range(0, countBlocks)
                .parallel()
                .forEach(i ->  {
                    byte[] block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

                    byte[] encryptedBlock = SCAlgorithm.encrypt(block);

                    System.arraycopy(encryptedBlock, 0, encryptedData, i * blockSize, blockSize);
                });
        byte[] block = data.length % blockSize != 0
                ? Arrays.copyOfRange(data, countBlocks * blockSize, countBlocks * blockSize + data.length % blockSize)
                : new byte[0];
        block = fillBlock(block);
        byte[] encryptedBlock = SCAlgorithm.encrypt(block);
        System.arraycopy(encryptedBlock, 0, encryptedData, countBlocks * blockSize, blockSize);
        return encryptedData;
    }

    private byte[] ecbDecrypt(byte[] data) {
        byte[] decryptedData = new byte[data.length];
        int countBlocks = data.length / blockSize;
        IntStream.range(0, countBlocks)
                .parallel()
                .forEach(i -> {
                    byte[] block = Arrays.copyOfRange(data, i * blockSize, (i+1) * blockSize);
                    byte[] decryptedBlock = SCAlgorithm.decrypt(block);
                    System.arraycopy(decryptedBlock, 0, decryptedData, i * blockSize, blockSize);
                });
        byte[] block = Arrays.copyOfRange(data, (countBlocks - 1) * blockSize, countBlocks * blockSize);
        byte[] decryptedBlock = SCAlgorithm.decrypt(block);
        decryptedBlock = unFillBlock(decryptedBlock);

        if (decryptedBlock.length > 0) {
            System.arraycopy(decryptedBlock, 0, decryptedData, (countBlocks - 1) * blockSize, decryptedBlock.length);
        }

        return Arrays.copyOf(decryptedData, data.length - blockSize + decryptedBlock.length);
    }

    private byte[] cbcEncrypt(byte[] data) {
        byte[] encryptedData = new byte[data.length + (blockSize - data.length % blockSize)];
        byte[] block;
        byte[] encryptedBlock = lastEncryptBlock == null
                ? Arrays.copyOfRange(iv, 0, iv.length)
                : Arrays.copyOfRange(lastEncryptBlock, 0, lastEncryptBlock.length);
        int countBlocks = data.length / blockSize;
        for (int i = 0; i < countBlocks; i++) {
            block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

            encryptedBlock = SCAlgorithm.encrypt(xor(block, encryptedBlock));

            System.arraycopy(encryptedBlock, 0, encryptedData, i * blockSize, blockSize);
        }
        block = data.length % blockSize != 0
                ? Arrays.copyOfRange(data, countBlocks * blockSize, countBlocks * blockSize + data.length % blockSize)
                : new byte[0];
        block = fillBlock(block);
        block = xor(block, encryptedBlock);
        encryptedBlock = SCAlgorithm.encrypt(block);

        lastEncryptBlock = Arrays.copyOfRange(encryptedBlock, 0, blockSize);
        System.arraycopy(encryptedBlock, 0, encryptedData, countBlocks * blockSize, blockSize);
        return encryptedData;
    }

    private byte[] cbcDecrypt(byte[] data) {
        byte[] decryptedData = new byte[data.length];
        byte[] block;
        byte[] decryptedBlock;
        byte[] prevBlock = lastDecryptBlock == null
                ? Arrays.copyOfRange(iv, 0, iv.length)
                : Arrays.copyOfRange(lastDecryptBlock, 0, lastDecryptBlock.length);
        int countBlocks = data.length / blockSize;
        for (int i = 0; i < (countBlocks - 1); i++) {
            block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

            decryptedBlock = SCAlgorithm.decrypt(block);
            decryptedBlock = xor(prevBlock, decryptedBlock);
            prevBlock = block;

            System.arraycopy(decryptedBlock, 0, decryptedData, i * blockSize, blockSize);
        }
        block = Arrays.copyOfRange(data, (countBlocks - 1) * blockSize, countBlocks * blockSize);

        lastDecryptBlock = Arrays.copyOfRange(block, 0, blockSize);

        decryptedBlock = SCAlgorithm.decrypt(block);
        decryptedBlock = xor(prevBlock, decryptedBlock);

        decryptedBlock = unFillBlock(decryptedBlock);

        if (decryptedBlock.length > 0) {
            System.arraycopy(decryptedBlock, 0, decryptedData, (countBlocks - 1) * blockSize, decryptedBlock.length);
        }

        return Arrays.copyOf(decryptedData, data.length - blockSize + decryptedBlock.length);
    }

    private byte[] pcbcEncrypt(byte[] data) {
        byte[] encryptedData = new byte[data.length + (blockSize - data.length % blockSize)];
        byte[] block;
        byte[] encryptedBlock;
        byte[] xorRes = lastEncryptBlock == null
                ? Arrays.copyOfRange(iv, 0, iv.length)
                : Arrays.copyOfRange(lastEncryptBlock, 0, lastEncryptBlock.length);
        int countBlocks = data.length / blockSize;
        for (int i = 0; i < countBlocks; i++) {
            block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

            encryptedBlock = SCAlgorithm.encrypt(xor(block, xorRes));
            xorRes = xor(encryptedBlock, block);

            System.arraycopy(encryptedBlock, 0, encryptedData, i * blockSize, blockSize);
        }
        block = data.length % blockSize != 0
                ? Arrays.copyOfRange(data, countBlocks * blockSize, countBlocks * blockSize + data.length % blockSize)
                : new byte[0];

        block = fillBlock(block);
        lastEncryptBlock = Arrays.copyOfRange(block, 0, blockSize);
        block = xor(block, xorRes);
        encryptedBlock = SCAlgorithm.encrypt(block);

        lastEncryptBlock = xor(encryptedBlock, lastEncryptBlock);
        System.arraycopy(encryptedBlock, 0, encryptedData, countBlocks * blockSize, blockSize);
        return encryptedData;
    }

    private byte[] pcbcDecrypt(byte[] data) {
        byte[] decryptedData = new byte[data.length];
        byte[] block;
        byte[] decryptedBlock;
        byte[] xorRes = lastDecryptBlock == null
                ? Arrays.copyOfRange(iv, 0, iv.length)
                : Arrays.copyOfRange(lastDecryptBlock, 0, lastDecryptBlock.length);
        int countBlocks = data.length / blockSize;
        for (int i = 0; i < (countBlocks - 1); i++) {
            block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

            decryptedBlock = SCAlgorithm.decrypt(block);
            decryptedBlock = xor(xorRes, decryptedBlock);
            xorRes = xor(block, decryptedBlock);

            System.arraycopy(decryptedBlock, 0, decryptedData, i * blockSize, blockSize);
        }
        block = Arrays.copyOfRange(data, (countBlocks - 1) * blockSize, countBlocks * blockSize);
        lastDecryptBlock = Arrays.copyOfRange(block, 0, blockSize);

        decryptedBlock = SCAlgorithm.decrypt(block);
        decryptedBlock = xor(xorRes, decryptedBlock);

        lastDecryptBlock = xor(block, decryptedBlock);

        decryptedBlock = unFillBlock(decryptedBlock);

        if (decryptedBlock.length > 0) {
            System.arraycopy(decryptedBlock, 0, decryptedData, (countBlocks - 1) * blockSize, decryptedBlock.length);
        }

        return Arrays.copyOf(decryptedData, data.length - blockSize + decryptedBlock.length);
    }

    private byte[] cfbEncrypt(byte[] data) {
        byte[] encryptedData = new byte[data.length + (blockSize - data.length % blockSize)];
        byte[] block;
        byte[] encryptedBlock;
        byte[] shiftRegister = lastEncryptBlock == null
                ? Arrays.copyOfRange(iv, 0, iv.length)
                : Arrays.copyOfRange(lastEncryptBlock, 0, lastEncryptBlock.length);
        int countBlocks = data.length / blockSize;
        for (int i = 0; i < countBlocks; i++) {
            block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

            encryptedBlock = SCAlgorithm.encrypt(shiftRegister);
            shiftRegister = xor(block, encryptedBlock);

            System.arraycopy(shiftRegister, 0, encryptedData, i * blockSize, blockSize);
        }
        block = data.length % blockSize != 0
                ? Arrays.copyOfRange(data, countBlocks * blockSize, countBlocks * blockSize + data.length % blockSize)
                : new byte[0];
        block = fillBlock(block);
        encryptedBlock = SCAlgorithm.encrypt(shiftRegister);
        lastEncryptBlock = Arrays.copyOfRange(encryptedBlock, 0, blockSize);

        shiftRegister = xor(block, encryptedBlock);
        System.arraycopy(shiftRegister, 0, encryptedData, countBlocks * blockSize, blockSize);
        return encryptedData;
    }

    private byte[] cfbDecrypt(byte[] data) {
        byte[] decryptedData = new byte[data.length];
        byte[] block;
        byte[] decryptedBlock;
        byte[] shiftRegister = lastDecryptBlock == null
                ? Arrays.copyOfRange(iv, 0, iv.length)
                : Arrays.copyOfRange(lastDecryptBlock, 0, lastDecryptBlock.length);
        int countBlocks = data.length / blockSize;
        for (int i = 0; i < (countBlocks - 1); i++) {
            block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

            decryptedBlock = SCAlgorithm.encrypt(shiftRegister);
            shiftRegister = xor(block, decryptedBlock);

            System.arraycopy(shiftRegister, 0, decryptedData, i * blockSize, blockSize);
            shiftRegister = block;
        }
        block = Arrays.copyOfRange(data, (countBlocks - 1) * blockSize, countBlocks * blockSize);
        lastDecryptBlock = Arrays.copyOfRange(block, 0, blockSize);

        decryptedBlock = SCAlgorithm.encrypt(shiftRegister);
        decryptedBlock = xor(block, decryptedBlock);

        decryptedBlock = unFillBlock(decryptedBlock);
        if (decryptedBlock.length > 0) {
            System.arraycopy(decryptedBlock, 0, decryptedData, (countBlocks - 1) * blockSize, decryptedBlock.length);
        }

        return Arrays.copyOf(decryptedData, data.length - blockSize + decryptedBlock.length);
    }

    private byte[] ofbEncrypt(byte[] data) {
        byte[] encryptedData = new byte[data.length + (blockSize - data.length % blockSize)];
        byte[] block;
        byte[] encryptedBlock;
        byte[] shiftRegister = lastEncryptBlock == null
                ? Arrays.copyOfRange(iv, 0, iv.length)
                : Arrays.copyOfRange(lastEncryptBlock, 0, lastEncryptBlock.length);
        int countBlocks = data.length / blockSize;
        for (int i = 0; i < countBlocks; i++) {
            block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

            encryptedBlock = SCAlgorithm.encrypt(shiftRegister);
            shiftRegister = xor(block, encryptedBlock);

            System.arraycopy(shiftRegister, 0, encryptedData, i * blockSize, blockSize);

            shiftRegister = encryptedBlock;
        }
        block = data.length % blockSize != 0
                ? Arrays.copyOfRange(data, countBlocks * blockSize, countBlocks * blockSize + data.length % blockSize)
                : new byte[0];
        block = fillBlock(block);
        encryptedBlock = SCAlgorithm.encrypt(shiftRegister);
        lastEncryptBlock = Arrays.copyOfRange(encryptedBlock, 0, blockSize);

        shiftRegister = xor(block, encryptedBlock);
        System.arraycopy(shiftRegister, 0, encryptedData, countBlocks * blockSize, blockSize);
        return encryptedData;
    }

    private byte[] ofbDecrypt(byte[] data) {
        byte[] decryptedData = new byte[data.length];
        byte[] block;
        byte[] decryptedBlock;
        byte[] shiftRegister = lastDecryptBlock == null
                ? Arrays.copyOfRange(iv, 0, iv.length)
                : Arrays.copyOfRange(lastDecryptBlock, 0, lastDecryptBlock.length);
        int countBlocks = data.length / blockSize;
        for (int i = 0; i < (countBlocks - 1); i++) {
            block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);

            decryptedBlock = SCAlgorithm.encrypt(shiftRegister);
            shiftRegister = xor(block, decryptedBlock);

            System.arraycopy(shiftRegister, 0, decryptedData, i * blockSize, blockSize);
            shiftRegister = decryptedBlock;
        }
        block = Arrays.copyOfRange(data, (countBlocks - 1) * blockSize, countBlocks * blockSize);

        decryptedBlock = SCAlgorithm.encrypt(shiftRegister);
        lastDecryptBlock = Arrays.copyOfRange(decryptedBlock, 0, blockSize);

        decryptedBlock = xor(block, decryptedBlock);

        decryptedBlock = unFillBlock(decryptedBlock);
        if (decryptedBlock.length > 0) {
            System.arraycopy(decryptedBlock, 0, decryptedData, (countBlocks - 1) * blockSize, decryptedBlock.length);
        }

        return Arrays.copyOf(decryptedData, data.length - blockSize + decryptedBlock.length);
    }

    private byte[] ctrEncrypt(byte[] data) {
        byte[] encryptedData = new byte[data.length + (blockSize - data.length % blockSize)];
        int countBlocks = data.length / blockSize;
        boolean hasPartitial = data.length % blockSize != 0;
        if (lastEncryptBlock == null) {
            lastEncryptBlock = new byte[blockSize];
        }

        List<byte[]> counterList = generateCounters(countBlocks + 1, blockSize);
        IntStream.range(0, countBlocks)
                .parallel()
                .forEach(i -> {
                    byte[] block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);
                    byte[] counter = counterList.get(i);
                    byte[] inputBlock = xor(iv, counter);
                    byte[] encryptedBlock = SCAlgorithm.encrypt(inputBlock);
                    encryptedBlock = xor(block, encryptedBlock);
                    System.arraycopy(encryptedBlock, 0, encryptedData, i * blockSize, blockSize);
                });
        byte[] block = hasPartitial
                ? Arrays.copyOfRange(data, countBlocks * blockSize, countBlocks * blockSize + data.length % blockSize)
                : new byte[0];
        block = fillBlock(block);

        byte[] counter = counterList.get(countBlocks);

        byte[] inputBlock = xor(iv, counter);

        byte[] encryptedBlock = SCAlgorithm.encrypt(inputBlock);
        encryptedBlock = xor(block, encryptedBlock);
        System.arraycopy(encryptedBlock, 0, encryptedData, countBlocks * blockSize, blockSize);
        return encryptedData;
    }

    private byte[] ctrDecrypt(byte[] data) {
        byte[] decryptedData = new byte[data.length];
        int countBlocks = data.length / blockSize;
        if (lastDecryptBlock == null) {
            lastDecryptBlock = new byte[blockSize];
        }

        List<byte[]> counterList = generateCounters(countBlocks, blockSize);
        IntStream.range(0, countBlocks - 1)
                .parallel()
                .forEach(i -> {
                    byte[] block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);
                    byte[] counter = counterList.get(i);
                    byte[] inputBlock = xor(iv, counter);
                    byte[] decryptedBlock = SCAlgorithm.encrypt(inputBlock);
                    decryptedBlock = xor(block, decryptedBlock);
                    System.arraycopy(decryptedBlock, 0, decryptedData, i * blockSize, blockSize);
                });
        byte[] block = Arrays.copyOfRange(data, (countBlocks - 1) * blockSize, countBlocks * blockSize);

        byte[] counter = counterList.get(countBlocks - 1);
        byte[] inputBlock = xor(iv, counter);

        byte[] decryptedBlock = SCAlgorithm.encrypt(inputBlock);
        decryptedBlock = xor(block, decryptedBlock);

        decryptedBlock = unFillBlock(decryptedBlock);
        if (decryptedBlock.length > 0) {
            System.arraycopy(decryptedBlock, 0, decryptedData, (countBlocks - 1) * blockSize, decryptedBlock.length);
        }

        return Arrays.copyOf(decryptedData, data.length - blockSize + decryptedBlock.length);
    }

    private byte[] randomDeltaEncrypt(byte[] data) {
        byte[] encryptedData = new byte[data.length + (blockSize - data.length % blockSize)];
        int countBlocks = data.length / blockSize;
        if (lastEncryptBlock == null) {
            lastEncryptBlock = new byte[blockSize];
        }

        List<byte[]> listRD = generateRandomDeltas(countBlocks + 1, blockSize);
        IntStream.range(0, countBlocks)
                .parallel()
                .forEach(i -> {
                    byte[] block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);
                    byte[] inputBlock = listRD.get(i);
                    byte[] encryptedBlock = SCAlgorithm.encrypt(inputBlock);
                    encryptedBlock = xor(block, encryptedBlock);
                    System.arraycopy(encryptedBlock, 0, encryptedData, i * blockSize, blockSize);
                });
        byte[] block = data.length % blockSize != 0
                ? Arrays.copyOfRange(data, countBlocks * blockSize, countBlocks * blockSize + data.length % blockSize)
                : new byte[0];
        block = fillBlock(block);
        byte[] inputBlock = listRD.get(countBlocks);
        byte[] encryptedBlock = SCAlgorithm.encrypt(inputBlock);
        encryptedBlock = xor(block, encryptedBlock);
        System.arraycopy(encryptedBlock, 0, encryptedData, countBlocks * blockSize, blockSize);
        return encryptedData;
    }

    private byte[] randomDeltaDecrypt(byte[] data) {
        byte[] decryptedData = new byte[data.length];
        int countBlocks = data.length / blockSize;
        if (lastDecryptBlock == null) {
            lastDecryptBlock = new byte[blockSize];
        }

        List<byte[]> listRD = generateRandomDeltas(countBlocks, blockSize);
        IntStream.range(0, countBlocks - 1)
                .parallel()
                .forEach(i -> {
                    byte[] block = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);
                    byte[] inputBlock = listRD.get(i);
                    byte[] decryptedBlock = SCAlgorithm.encrypt(inputBlock);
                    decryptedBlock = xor(block, decryptedBlock);
                    System.arraycopy(decryptedBlock, 0, decryptedData, i * blockSize, blockSize);
                });
        byte[] block = Arrays.copyOfRange(data, (countBlocks - 1) * blockSize, countBlocks * blockSize);

        byte[] inputBlock = listRD.get(countBlocks - 1);
        byte[] decryptedBlock = SCAlgorithm.encrypt(inputBlock);
        decryptedBlock = xor(block, decryptedBlock);

        decryptedBlock = unFillBlock(decryptedBlock);
        if (decryptedBlock.length > 0) {
            System.arraycopy(decryptedBlock, 0, decryptedData, (countBlocks - 1) * blockSize, decryptedBlock.length);
        }

        return Arrays.copyOf(decryptedData, data.length - blockSize + decryptedBlock.length);
    }

    private byte[] zerosPadding(byte[] block) {
        byte[] paddedBlock = new byte[blockSize];
        System.arraycopy(block, 0, paddedBlock, 0, block.length);

        return paddedBlock;
    }

    private byte[] X923Padding(byte[] block) {
        int padLength = blockSize - block.length;
        byte[] paddedBlock = new byte[blockSize];
        System.arraycopy(block, 0, paddedBlock, 0, block.length);
        paddedBlock[paddedBlock.length - 1] = (byte) padLength;

        return paddedBlock;
    }

    private byte[] PKCS7Padding(byte[] block) {
        int padLength = blockSize - block.length;
        byte[] paddedBlock = new byte[blockSize];
        System.arraycopy(block, 0, paddedBlock, 0, block.length);
        Arrays.fill(paddedBlock, block.length, paddedBlock.length, (byte) padLength);

        return paddedBlock;
    }

    private byte[] ISO10126Padding(byte[] block) {
        int padLength = blockSize - block.length;
        byte[] paddedBlock = new byte[blockSize];
        System.arraycopy(block, 0, paddedBlock, 0, block.length);
        Random random = new Random();
        for (int i = block.length; i < paddedBlock.length - 1; i++) {
            paddedBlock[i] = (byte) random.nextInt(256);
        }
        paddedBlock[paddedBlock.length - 1] = (byte) padLength;

        return paddedBlock;
    }

    private byte[] zerosUnPadding(byte[] block) {
        if (block == null || block.length == 0) {
            return new byte[0];
        }

        int countForRemove = 0;
        for (int i = block.length - 1; i >= 0; i--) {
            if (block[i] == 0x00) {
                countForRemove++;
            }
        }
        byte[] unPaddedBlock = new byte[block.length - countForRemove];
        System.arraycopy(block, 0, unPaddedBlock, 0, block.length - countForRemove);
        return unPaddedBlock;
    }

    private byte[] X923UnPadding(byte[] block) {
        if (block == null || block.length == 0) {
            return new byte[0];
        }

        int countForRemove = block[block.length - 1];
        byte[] unPaddedBlock = new byte[block.length - countForRemove];
        System.arraycopy(block, 0, unPaddedBlock, 0, block.length - countForRemove);
        return unPaddedBlock;
    }

    private byte[] PKCS7UnPadding(byte[] block) {
        if (block == null || block.length == 0) {
            return new byte[0];
        }

        int countForRemove = block[block.length - 1];
        byte[] unPaddedBlock = new byte[block.length - countForRemove];
        System.arraycopy(block, 0, unPaddedBlock, 0, block.length - countForRemove);
        return unPaddedBlock;
    }

    private byte[] ISO10126UnPadding(byte[] block) {
        if (block == null || block.length == 0) {
            return new byte[0];
        }

        int countForRemove = block[block.length - 1];
        byte[] unPaddedBlock = new byte[block.length - countForRemove];
        System.arraycopy(block, 0, unPaddedBlock, 0, block.length - countForRemove);
        return unPaddedBlock;
    }

    private byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    private void incrementCounter(byte[] counter) {
        for (int i = counter.length - 1; i >= 0; i--) {
            counter[i]++;
            if (counter[i] != 0) break;
        }
    }

    private List<byte[]> generateCounters(int totalBlocks, int blockSize) {
        List<byte[]> counters = new ArrayList<>(totalBlocks);
        byte[] counter = lastDecryptBlock == null
                ? lastEncryptBlock
                : lastDecryptBlock;
        for (int i = 0; i < totalBlocks; i++) {
            counters.add(Arrays.copyOf(counter, blockSize));
            incrementCounter(counter);
        }

        return counters;
    }

    private List<byte[]> generateRandomDeltas(int totalBlocks, int blockSize) {
        List<byte[]> listRD = new ArrayList<>(totalBlocks);
        byte[] init = lastDecryptBlock == null
                ? lastEncryptBlock
                : lastDecryptBlock;
        for (int i = 0; i < totalBlocks; i++) {
            listRD.add(Arrays.copyOf(computeInputBlock(init, i), blockSize));
        }
        return listRD;
    }

    private byte[] computeInputBlock(byte[] iv, int blockIndex) {
        Object deltaObj = additionalParams.get("delta");
        if (!(deltaObj instanceof Number)) {
            throw new IllegalArgumentException("Delta must be a number.");
        }
        long deltaLong = ((Number) deltaObj).longValue();
        BigInteger delta = BigInteger.valueOf(deltaLong);
        BigInteger ivInt = new BigInteger(1, iv);

        BigInteger offset = delta.multiply(BigInteger.valueOf(blockIndex + 1));

        BigInteger inputInt = ivInt.add(offset);

        byte[] result = inputInt.toByteArray();

        int start = result.length > iv.length ? result.length - iv.length : 0;
        int length = Math.min(result.length, iv.length);

        System.arraycopy(result, start, iv, iv.length - length, length);

        return iv;
    }
}
