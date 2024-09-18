package com.homefit.backend.room.repository;

import com.homefit.backend.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
