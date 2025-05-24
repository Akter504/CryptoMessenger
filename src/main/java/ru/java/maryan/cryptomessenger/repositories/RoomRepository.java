package ru.java.maryan.cryptomessenger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.java.maryan.cryptomessenger.models.Room;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query("""
    SELECT r FROM Room r
    JOIN FETCH r.userFirst
    JOIN FETCH r.userSecond
    WHERE r.userFirst.id = :userId OR r.userSecond.id = :userId
    """)
    List<Room> findAllRoomsByUserId(@Param("userId") UUID userId);

    @Query("""
    SELECT r FROM Room r
    JOIN FETCH r.userFirst
    JOIN FETCH r.userSecond
    WHERE (r.userFirst.id = :userId OR r.userSecond.id = :userId) AND r.id = :roomId
    """)
    Room findOtherUser(@Param("userId") UUID userId, @Param("roomId") UUID roomId);
}
