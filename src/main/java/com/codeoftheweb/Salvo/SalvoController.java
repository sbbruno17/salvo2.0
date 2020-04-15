package com.codeoftheweb.Salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/games/{gameId}/player")
    private ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        Player player = playerRepository.findByUserName(authentication.getName());
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Not logged in!"), HttpStatus.UNAUTHORIZED);
        }

        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "this game does not exist"), HttpStatus.FORBIDDEN);
        }

        Game game = optionalGame.get();
        if (game.getGamePlayers().size() > 1) {
            return new ResponseEntity<>(makeMap("error", "Already 2 players"), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().stream().anyMatch(gamePlayer -> gamePlayer.getPlayer().getId() == player.getId())) {
            return new ResponseEntity<>(makeMap("error", "You can't play against yourself!"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = gameplayerRepository.save(new GamePlayer(game, player));
        return new ResponseEntity<>(makeMap("gpID", gamePlayer.getId()), HttpStatus.OK);
    }

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

    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> gameView(@PathVariable Long gamePlayerId, Authentication authentication) {
        Player playerLog = playerRepository.findByUserName(authentication.getName());
        Optional<GamePlayer> gameplayer = gameplayerRepository.findById(gamePlayerId);
        if (!gameplayer.isPresent()) {
            return new ResponseEntity<>(makeMap("error", "Must log in to acces"), HttpStatus.CONFLICT);
        }
        if (gameplayer.get().getPlayer().getId() != playerLog.getId()) {
            return new ResponseEntity<>(makeMap("error", "No room for cheaters :D"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(gameplayer.get().toDTOGameView(), HttpStatus.CREATED);
    }


    @PostMapping("players")
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String username, @RequestParam String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name or password"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(username);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        Player newPlayer = playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @PostMapping("games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No player logged. Please Log in before entering any game"), HttpStatus.FORBIDDEN);
        }
        Game newGame = gameRepository.save(new Game(LocalDateTime.now()));
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer newGamePlayer = gameplayerRepository.save(new GamePlayer(newGame, player));

        return new ResponseEntity<>(makeMap("gamePlayerId", newGamePlayer.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShip(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {
        GamePlayer gameplayer = gameplayerRepository.findById(gamePlayerId).orElse(null);
        Player player = playerRepository.findByUserName(authentication.getName());
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No player logged. Please Log in before entering any game"), HttpStatus.UNAUTHORIZED);
        } else if (gameplayer == null) {
            return new ResponseEntity<>(makeMap("error", "there is no game player with the given ID"), HttpStatus.NOT_FOUND);
        } else if (gameplayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "this is not your game"), HttpStatus.UNAUTHORIZED);
        } else if (gameplayer.getShips().size() > 0) {
            return new ResponseEntity<>(makeMap("error", "Ships allready located"), HttpStatus.FORBIDDEN);
        } else {
            ships.forEach(ship -> gameplayer.addShip(ship));
            gameplayerRepository.save(gameplayer);
            return new ResponseEntity<>(makeMap("succes", "ships created succesfully"), HttpStatus.FORBIDDEN);
        }
    }

}

