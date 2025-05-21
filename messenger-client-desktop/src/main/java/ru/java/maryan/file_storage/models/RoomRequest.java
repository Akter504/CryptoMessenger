package ru.java.maryan.file_storage.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {
    String token;
    String receiverLogin;
    String encryptionAlg;
    String paddingMode;
    String cipherMode;
}
