package com.homefit.backend.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomCreateRequestDto {

    @Schema(description = "운동 룸 ID", example = "1")
    @NotNull(message = "룸 ID는 필수 입력 사항입니다.")
    private Long roomId;

    @Schema(description = "방장 ID", example = "1")
    @NotNull(message = "방장 ID는 필수 입력 사항입니다.")
    private Long ownerId;
}
