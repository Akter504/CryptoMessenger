package ru.java.maryan.cryptomessenger.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.maryan.cryptomessenger.models.Room;
import ru.java.maryan.cryptomessenger.repositories.RoomRepository;
import ru.java.maryan.cryptomessenger.services.RoomService;

import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> findRoomsByUserId(UUID id) {
        return roomRepository.findAllRoomsByUserId(id);
    }
}
