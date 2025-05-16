package ru.java.maryan.cryptomessenger.services;

import ru.java.maryan.cryptomessenger.models.Room;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    List<Room> findRoomsByUserId(UUID id);
}
