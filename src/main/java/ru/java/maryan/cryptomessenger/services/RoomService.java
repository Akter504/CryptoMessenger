package ru.java.maryan.cryptomessenger.services;

import com.securechat.proto.RoomConfig;
import ru.java.maryan.cryptomessenger.models.Room;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    void createRoom(RoomConfig config);

    UUID findOtherUser(UUID roomId, UUID senderId);

    List<Room> findRoomsByUserId(UUID id);

    void deleteRoom(UUID roomId);
}
