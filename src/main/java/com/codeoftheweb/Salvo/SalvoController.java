package com.codeoftheweb.Salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameplayerRepository gameplayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/games")
    public Map<String, Object> Games(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("playerLog", null);
        } else {
            Player playerLog = playerRepository.findByUserName(authentication.getName());
            dto.put("playerLog", playerLog.toDTO());
        }
        dto.put("games",
                gameRepository
                        .findAll()
                        .stream()
                        .map(Game::toDTO)
                        .collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> gameView(@PathVariable Long gamePlayerId) {
        return gameplayerRepository.findById(gamePlayerId).get().toDTOGameView();
    }

    @RequestMapping("/players")

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}