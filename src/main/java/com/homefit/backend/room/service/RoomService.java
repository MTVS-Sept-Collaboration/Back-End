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
        return roomRepository.save(room).getId();
    }

    public void deleteRoom(Long roomId) {
        Room foundRoom = roomRepository.findById(roomId)
                .orElseThrow();

        LocalDateTime createdTime = foundRoom.getCreatedAt();

        roomRepository.delete(foundRoom);
        LocalDateTime deleteTime = LocalDateTime.now();

        Duration existsTime = Duration.between(createdTime, deleteTime);
        log.info("Delete Room Exists Time : {}", existsTime);
    }
}
