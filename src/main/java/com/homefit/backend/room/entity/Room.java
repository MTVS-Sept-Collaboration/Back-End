package com.homefit.backend.room.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;
    private LocalDateTime createdAt;

    public Room() {
        this.createdAt = LocalDateTime.now();
    }
}
