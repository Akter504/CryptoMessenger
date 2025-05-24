package ru.java.maryan.file_storage.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securechat.proto.EncryptedMessage;
import ru.java.maryan.file_storage.services.MessageFileService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageFileServiceImpl implements MessageFileService {
    private final File messageDir = new File("chat-storage/messages");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageFileServiceImpl() {
        messageDir.mkdirs();
    }

    @Override
    public void appendMessage(String roomId, EncryptedMessage message) throws IOException {
        File file = new File(messageDir, roomId + ".json");

        List<EncryptedMessage> messages;
        if (file.exists()) {
            messages = objectMapper.readValue(file, new TypeReference<List<EncryptedMessage>>() {});
        } else {
            messages = new ArrayList<>();
        }

        messages.add(message);
        objectMapper.writeValue(file, messages);
    }

    @Override
    public List<EncryptedMessage> loadMessages(String roomId) throws IOException {
        File file = new File(messageDir, roomId + ".json");
        if (!file.exists()) return new ArrayList<>();
        return objectMapper.readValue(file, new TypeReference<List<EncryptedMessage>>() {});
    }
}
