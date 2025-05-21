package ru.java.maryan.file_storage.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocalRoomConfig {
    private String roomId;
    private String senderLogin;
    private String receiverLogin;
    private String encryptionAlgorithm;
    private String padding;
    private String mode;
    private byte[] iv;
    private byte[] delta;
    private byte[] g;
    private byte[] p;
    private byte[] publicKey;
    private byte[] sharedKey;
}
