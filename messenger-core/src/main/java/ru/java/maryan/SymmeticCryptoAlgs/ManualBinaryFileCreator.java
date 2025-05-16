package ru.java.maryan.CryptoLabs.DES;

import java.io.FileOutputStream;
import java.io.IOException;

public class ManualBinaryFileCreator {
    public static void main(String[] args) {
        String fileName = "input.bin";

        byte[] data = new byte[] {
                (byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF,
                (byte)0x00, (byte)0x11, (byte)0x22, (byte)0x33,
                (byte)0x44, (byte)0x55, (byte)0x66, (byte)0x77,
                (byte)0x88, (byte)0x99, (byte)0xAA, (byte)0xBB,
                (byte)0xCC, (byte)0xDD, (byte)0xEE, (byte)0xFF,
                (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                (byte)0x10, (byte)0x20, (byte)0x30, (byte)0x40
        };

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(data);
            System.out.println("Success create file: " + fileName);
        } catch (IOException e) {
            System.err.println("Something get wrong in ManualBinaryFileCreator: " + e.getMessage());
        }
    }
}

