package ru.java.maryan.file_storage.services.impl;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.securechat.proto.RoomConfig;
import org.springframework.stereotype.Service;
import ru.java.maryan.file_storage.models.LocalRoomConfig;
import ru.java.maryan.file_storage.models.RoomRequest;
import ru.java.maryan.file_storage.models.converters.RoomConfigConverter;
import ru.java.maryan.file_storage.services.RoomFileService;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoomFileServiceImpl implements RoomFileService {
    private final File storageDir = new File("chat-storage");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoomConfigConverter converter;

    public RoomFileServiceImpl(RoomConfigConverter converter) {
        this.converter = converter;
        storageDir.mkdirs();
    }

    @Override
    public RoomConfig createRoomConfig(RoomRequest request) {
        String roomId = UUID.randomUUID().toString();
        String senderLogin = getSubject(request.getToken());

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        byte[] delta = new byte[16];
        new SecureRandom().nextBytes(delta);

        return RoomConfig.newBuilder()
                .setRoomId(roomId)
                .setSenderLogin(senderLogin)
                .setReceiverLogin(request.getReceiverLogin())
                .setEncryptionAlgorithm(request.getEncryptionAlg())
                .setMode(request.getCipherMode())
                .setPadding(request.getPaddingMode())
                .setIv(ByteString.copyFrom(iv))
                .setDelta(ByteString.copyFrom(delta))
                .build();

    }

    @Override
    public void saveRoomConfig(RoomConfig configRoom, byte[] sharedKey, String userLogin) throws IOException {
        LocalRoomConfig config = converter.convert(configRoom);
        config.setSharedKey(sharedKey);
        File roomsDir = getUserRoomsDir(userLogin);
        File file = new File(roomsDir, config.getRoomId() + ".json");
        objectMapper.writeValue(file, config);
    }

    @Override
    public LocalRoomConfig loadRoomConfig(String roomId, String userLogin) throws IOException {
        File roomsDir = getUserRoomsDir(userLogin);
        File file = new File(roomsDir, roomId + ".json");
        return objectMapper.readValue(file, LocalRoomConfig.class);
    }

    @Override
    public List<RoomConfig> loadAllRoomConfigs(String userLogin) throws IOException {
        List<RoomConfig> configs = new ArrayList<>();
        File roomsDir = getUserRoomsDir(userLogin);
        File[] files = roomsDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null) return configs;

        for (File file : files) {
            try {
                LocalRoomConfig localConfig = objectMapper.readValue(file, LocalRoomConfig.class);
                configs.add(converter.convert(localConfig));
            } catch (IOException e) {
                System.err.println("Failed to read config: " + file.getName() + " — " + e.getMessage());
            }
        }

        return configs;
    }

    private File getUserRoomsDir(String userId) throws IOException {
        File userDir = new File(storageDir, userId);
        File roomsDir = new File(userDir, "rooms");

        if (!roomsDir.exists() && !roomsDir.mkdirs()) {
            throw new IOException("Failed to create user rooms directory: " + roomsDir.getAbsolutePath());
        }

        return roomsDir;
    }

    @Override
    public String getSubject(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token must not be null or blank");
        }
        String actualToken = "";
        try {
            JsonNode jsonNode = objectMapper.readTree(token);
            JsonNode tokenNode = jsonNode.path("token");
            actualToken = tokenNode.path("token").asText();
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return JWT.decode(actualToken).getSubject();
    }
}
