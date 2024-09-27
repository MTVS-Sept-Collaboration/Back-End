package com.homefit.backend.character.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterUpdateRequestDto {

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
}
