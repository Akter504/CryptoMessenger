package ru.java.maryan.cryptomessenger.services.Impl;

import com.securechat.proto.RoomConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.java.maryan.cryptomessenger.models.Room;
import ru.java.maryan.cryptomessenger.models.User;
import ru.java.maryan.cryptomessenger.repositories.RoomRepository;
import ru.java.maryan.cryptomessenger.services.RoomService;
import ru.java.maryan.cryptomessenger.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final UserService userService;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, UserService userService) {
        this.roomRepository = roomRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createRoom(RoomConfig config) {
        Room room = new Room();
        if (roomRepository.existsById(UUID.fromString(config.getRoomId()))) {
            return;
        }
        User first = userService.findUserByLogin(config.getSenderLogin())
                .orElseThrow(() -> new RuntimeException("An error occurred when creating the room, the sender was not found."));
        User second = userService.findUserByLogin(config.getReceiverLogin())
                .orElseThrow(() -> new RuntimeException("An error occurred when creating the room, the receiver was not found."));

        room.setId(UUID.fromString(config.getRoomId()));
        room.setUserFirst(first);
        room.setUserSecond(second);
        room.setCreatedAt(LocalDateTime.now());

        roomRepository.save(room);
    }

    @Override
    public UUID findOtherUser(UUID roomId, UUID senderId) {
        Room room = roomRepository.findOtherUser(senderId, roomId);
        UUID otherUserId;
        if (room.getUserFirst().getId().equals(senderId)) {
            otherUserId = room.getUserSecond().getId();
        } else {
            otherUserId = room.getUserFirst().getId();
        }
        return otherUserId;
    }

    @Override
    public List<Room> findRoomsByUserId(UUID id) {
        return roomRepository.findAllRoomsByUserId(id);
    }

    @Transactional
    public void deleteRoom(UUID roomId) {
        roomRepository.findById(roomId).ifPresent(room -> {
            try {
                roomRepository.delete(room);
            } catch (ObjectOptimisticLockingFailureException e) {
                System.out.println("Room already deleted concurrently, skipping: " + roomId);
            }
        });
    }
}
