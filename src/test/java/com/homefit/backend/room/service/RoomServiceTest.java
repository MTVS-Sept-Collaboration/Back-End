package com.homefit.backend.room.service;

import com.homefit.backend.room.dto.RoomCreateRequestDto;
import com.homefit.backend.room.entity.Room;
import com.homefit.backend.room.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @DisplayName("Room 생성 테스트")
    void testCreateRoom() {
        RoomCreateRequestDto requestDto = new RoomCreateRequestDto();
        requestDto.setRoomId(1L);
        requestDto.setOwnerId(123L);

        Long roomId = roomService.createRoom(requestDto);

        assertNotNull(roomId, "룸 ID가 null이 아니어야 합니다.");
        assertTrue(roomRepository.findById(roomId).isPresent(), "생성된 방이 존재해야 합니다.");
    }

    @Test
    @DisplayName("Room 삭제 테스트")
    void testDeleteRoom() {
        // 테스트용 Room 생성
        Room room = new Room();
        roomRepository.save(room);
        Long roomId = room.getRoomId();

        // Room 삭제
        roomService.deleteRoom(roomId);

        // 삭제 확인
        assertTrue(roomRepository.findById(roomId).isEmpty(), "삭제된 방은 존재하지 않아야 합니다.");
    }
}
