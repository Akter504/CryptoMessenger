package ru.java.maryan.file_storage.services;

import com.securechat.proto.EncryptedMessage;

import java.io.IOException;
import java.util.List;

public interface MessageFileService {

    void appendMessage(String roomId, EncryptedMessage message) throws IOException;

    List<EncryptedMessage> loadMessages(String roomId) throws IOException;
}
