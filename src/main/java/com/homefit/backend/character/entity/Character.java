package com.homefit.backend.character.entity;

import com.homefit.backend.character.dto.CharacterUpdateRequestDto;
import com.homefit.backend.login.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "`character`") // MySQL에서 character는 예약어이므로
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user; // userId를 user로 변경

    private Long backpack;
    private Long body;
    private Long eyebrow;
    private Long glasses;
    private Long glove;
    private Long hair;
    private Long hat;
    private Long mustache;
    private Long outerwear;
    private Long pants;
    private Long shoe;

    public void update(CharacterUpdateRequestDto requestDto) {
        this.backpack = requestDto.getBackpack();
        this.body = requestDto.getBody();
        this.eyebrow = requestDto.getEyebrow();
        this.glasses = requestDto.getGlasses();
        this.glove = requestDto.getGlove();
        this.hair = requestDto.getHair();
        this.hat = requestDto.getHat();
        this.mustache = requestDto.getMustache();
        this.outerwear = requestDto.getOuterwear();
        this.pants = requestDto.getPants();
        this.shoe = requestDto.getShoe();
    }
}
