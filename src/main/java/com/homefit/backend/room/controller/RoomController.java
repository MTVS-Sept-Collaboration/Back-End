package com.homefit.backend.room.controller;

import com.homefit.backend.room.dto.RoomCreateRequestDto;
import com.homefit.backend.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "운동 룸 생성", description = "새로운 운동 룸을 생성합니다. 포톤에서 룸을 생성하면 호출합니다.")
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateRequestDto requestDto) {
        return ResponseEntity.ok(roomService.createRoom(requestDto));
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "운동 룸 삭제", description = "운동 룸을 삭제합니다. 포톤에서 방이 사라지면 호출합니다.")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
