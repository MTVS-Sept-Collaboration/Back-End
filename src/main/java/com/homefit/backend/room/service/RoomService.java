package com.homefit.backend.room.service;

import com.homefit.backend.room.dto.RoomCreateRequestDto;
import com.homefit.backend.room.entity.Room;
import com.homefit.backend.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    public Long createRoom(RoomCreateRequestDto roomCreateRequestDto) {
        Room room = new Room();
        // 방 아이디 넣을 거면 넣어야 함
        // 유저 엔티티에 방 생성 횟수 들어가야 할듯
        return roomRepository.save(room).getRoomId();
    }

    public void deleteRoom(Long roomId) {
        Room foundRoom = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found with id: " + roomId));

        LocalDateTime createdTime = foundRoom.getCreatedAt();
        roomRepository.delete(foundRoom);

        LocalDateTime deleteTime = LocalDateTime.now();
        Duration existsTime = Duration.between(createdTime, deleteTime);
        log.info("Delete Room Exists Time : {}", existsTime);
    }


}
