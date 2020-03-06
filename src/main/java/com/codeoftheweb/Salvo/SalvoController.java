package com.codeoftheweb.Salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameplayerRepository gameplayerRepository;

    @RequestMapping("/games")
    public List<Map<String, Object>> getAll() {
        return gameRepository.findAll().stream().map(Game::toDTO).collect(toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> gameView(@PathVariable Long gamePlayerId) {
        return gameplayerRepository.findById(gamePlayerId).get().toDTOGameView();
    }
}