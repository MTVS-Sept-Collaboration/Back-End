package com.homefit.backend.character.service;

import com.homefit.backend.character.dto.CharacterUpdateRequestDto;
import com.homefit.backend.character.entity.Character;
import com.homefit.backend.character.repository.CharacterRepository;
import com.homefit.backend.login.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;

    public Long updateCharacter(Long userId, CharacterUpdateRequestDto requestDto) {
        log.info("사용자 ID {}의 캐릭터 업데이트를 시도합니다.", userId);
        Character character = characterRepository.findByUser_Id(userId)
                .orElseThrow(() -> {
                    log.error("사용자 ID {}에 해당하는 캐릭터를 찾을 수 없습니다.", userId);
                    return new EntityNotFoundException("사용자 ID " + userId + "의 캐릭터를 찾을 수 없습니다.");
                });

        character.update(requestDto);
        Long updatedUserId = characterRepository.save(character).getUser().getId();
        log.info("사용자 ID {}의 캐릭터 업데이트가 완료되었습니다.", updatedUserId);
        return updatedUserId;
    }

    public Character createCharacterForUser(User user) {
        log.info("사용자 ID {}의 새 캐릭터를 생성합니다.", user.getId());
        // 자동으로 생성되는 캐릭터의 기본 의상을 0으로 설정
        Character newCharacter = Character.builder()
                .user(user)
                .backpack(0L)
                .body(0L)
                .eyebrow(0L)
                .glasses(0L)
                .glove(0L)
                .hair(0L)
                .hat(0L)
                .mustache(0L)
                .outerwear(0L)
                .pants(0L)
                .shoe(0L)
                .build();
        Character savedCharacter = characterRepository.save(newCharacter);
        log.info("사용자 ID {}의 새 캐릭터가 성공적으로 생성되었습니다.", user.getId());
        return savedCharacter;
    }

    @Transactional(readOnly = true)
    public CharacterUpdateRequestDto getCharacter(Long userId) {
        log.info("사용자 ID {}의 캐릭터 정보를 조회합니다.", userId);
        Character character = characterRepository.findByUser_Id(userId)
                .orElseThrow(() -> {
                    log.error("사용자 ID {}에 해당하는 캐릭터를 찾을 수 없습니다.", userId);
                    return new EntityNotFoundException("사용자 ID " + userId + "의 캐릭터를 찾을 수 없습니다.");
                });

        log.info("사용자 ID {}의 캐릭터 정보 조회가 완료되었습니다.", userId);
        return new CharacterUpdateRequestDto(
                character.getBackpack(),
                character.getBody(),
                character.getEyebrow(),
                character.getGlasses(),
                character.getGlove(),
                character.getHair(),
                character.getHat(),
                character.getMustache(),
                character.getOuterwear(),
                character.getPants(),
                character.getShoe()
        );
    }
}
