package ru.java.maryan.CryptoLabs.DES;

import ru.java.maryan.CryptoLabs.DES.impl.SymmetricCryptographicContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FileHandler implements AutoCloseable {
    public enum Mode {
        ENCRYPT,
        DECRYPT
    }

    private static final int SIZE = 16;
    private FileChannel fileChannelIn;
    private FileChannel fileChannelOut;
    private Mode mode;
    private SymmetricCryptographicContext cipherContext;

    public FileHandler(String fileName, SymmetricCryptographicContext cipherContext, Mode mode) throws IOException {
        Path filePath = Paths.get(Objects.requireNonNull(fileName, "File name cannot be null"));
        this.mode = mode;
        openChannels(filePath);
        this.cipherContext = cipherContext;
    }

    public void changeFileName(String fileName) throws IOException{
        if (fileChannelIn != null && fileChannelIn.isOpen()) {
            fileChannelIn.close();
        }
        Path filePath = Paths.get(Objects.requireNonNull(fileName, "File name cannot be null"));
        openChannels(filePath);
    }

    public void changeMode(Mode mode) {
        this.mode = mode;
    }

    private void openChannels(Path filePath) throws IOException {
        this.fileChannelIn = FileChannel.open(filePath, StandardOpenOption.READ);
        String extension = mode == Mode.ENCRYPT ? ".ciphered" : ".deciphered";
        Path outFilePath = Paths.get(filePath.toString() + extension);
        this.fileChannelOut = FileChannel.open(outFilePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public boolean processing() throws IOException, ExecutionException, InterruptedException {
        int currentSize = mode == Mode.ENCRYPT
                ? SIZE
                : SIZE + cipherContext.getBlockSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(currentSize);
        for (long i = 0; i < fileChannelIn.size(); i += currentSize) {
            fileChannelIn.position(i);
            byteBuffer.clear();
            int bytesRead = fileChannelIn.read(byteBuffer);
            if (bytesRead == -1) break;

            byte[] data = new byte[bytesRead];
            System.arraycopy(byteBuffer.array(), 0, data, 0, bytesRead);
            byte[] block = mode == Mode.ENCRYPT
                    ? cipherContext.symmetricalEncrypt(data).get()
                    : cipherContext.symmetricalDecrypt(data).get();
            ByteBuffer outBuffer = ByteBuffer.wrap(block);
            while (outBuffer.hasRemaining()) {
                int bytesWritten = fileChannelOut.write(outBuffer);
                if (bytesWritten <= 0) {
                    throw new IOException("Failed to write data to output file");
                }
            }
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        if (fileChannelIn != null) {
            fileChannelIn.close();
        }
        if (fileChannelOut != null) {
            fileChannelOut.close();
        }
    }
}
