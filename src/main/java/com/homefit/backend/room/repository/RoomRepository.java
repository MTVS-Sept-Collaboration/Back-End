package com.homefit.backend.room.repository;

import com.homefit.backend.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomId(Long roomId);
}
