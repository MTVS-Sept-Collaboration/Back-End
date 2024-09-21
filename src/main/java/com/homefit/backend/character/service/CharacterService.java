package com.homefit.backend.character.service;

import com.homefit.backend.character.dto.CharacterUpdateRequestDto;
import com.homefit.backend.character.entity.Character;
import com.homefit.backend.character.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;

    public Long updateCharacter(Long userId, CharacterUpdateRequestDto requestDto) {

        Character character = characterRepository.findByUserId(userId)
                .orElseThrow();

        character.update(requestDto);

        return characterRepository.save(character).getUserId().getId();
    }
}
