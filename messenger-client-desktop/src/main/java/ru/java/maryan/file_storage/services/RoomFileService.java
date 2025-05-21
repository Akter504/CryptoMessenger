package ru.java.maryan.file_storage.services;

import com.securechat.proto.RoomConfig;
import ru.java.maryan.file_storage.models.LocalRoomConfig;
import ru.java.maryan.file_storage.models.RoomRequest;

import java.io.IOException;
import java.util.List;

public interface RoomFileService {
    RoomConfig createRoomConfig(RoomRequest request);

    void saveRoomConfig(RoomConfig config, byte[] sharedKey, String userLogin) throws IOException;

    LocalRoomConfig loadRoomConfig(String roomId, String userLogin) throws IOException;

    List<RoomConfig> loadAllRoomConfigs(String userLogin) throws IOException;

    String getSubject(String token);
}
