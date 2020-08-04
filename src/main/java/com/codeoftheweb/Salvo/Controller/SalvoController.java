package com.codeoftheweb.Salvo.Controller;


import com.codeoftheweb.Salvo.Models.AppMessages;
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
    private com.codeoftheweb.Salvo.GameRepository gameRepository;

    @Autowired
    private com.codeoftheweb.Salvo.GameplayerRepository gameplayerRepository;

    @Autowired
    private com.codeoftheweb.Salvo.PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.codeoftheweb.Salvo.ScoreRepository scoreRepository;



    @RequestMapping("/games")
    public Map<String, Object> Games(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("playerLog", null);
        } else {
            com.codeoftheweb.Salvo.Player playerLog = playerRepository.findByUserName(authentication.getName());
            dto.put("playerLog", playerLog.toDTO());
        }
        dto.put("games",
                gameRepository
                        .findAll()
                        .stream()
                        .map(com.codeoftheweb.Salvo.Game::toDTO)
                        .collect(Collectors.toList()));
        return dto;
    }

    //GAME CREATION
    @PostMapping("games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_LOGGED), HttpStatus.FORBIDDEN);
        }
        com.codeoftheweb.Salvo.Game newGame = gameRepository.save(new com.codeoftheweb.Salvo.Game(LocalDateTime.now()));
        com.codeoftheweb.Salvo.Player player = playerRepository.findByUserName(authentication.getName());
        com.codeoftheweb.Salvo.GamePlayer newGamePlayer = gameplayerRepository.save(new com.codeoftheweb.Salvo.GamePlayer(newGame, player));

        return new ResponseEntity<>(makeMap(AppMessages.GPID, newGamePlayer.getId()), HttpStatus.CREATED);
    }

    //GAME_VIEW CREATION (VISTA DEL JUEGO)
    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> gameView(@PathVariable Long gamePlayerId, Authentication authentication) {
        com.codeoftheweb.Salvo.Player playerLog = playerRepository.findByUserName(authentication.getName());
        Optional<com.codeoftheweb.Salvo.GamePlayer> gameplayer = gameplayerRepository.findById(gamePlayerId);
        if (!gameplayer.isPresent()) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.NOT_FOUND), HttpStatus.UNAUTHORIZED);
        }
        if (gameplayer.get().getPlayer().getId() != playerLog.getId()) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.FAIL_USER), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(gameplayer.get().toDTOGameView(), HttpStatus.CREATED);
    }

// USER CREATION
    @PostMapping("players")
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String username, @RequestParam String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.NO_NAME), HttpStatus.FORBIDDEN);
        }
        com.codeoftheweb.Salvo.Player player = playerRepository.findByUserName(username);
        if (player != null) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.USER_FOUND), HttpStatus.CONFLICT);
        }
        com.codeoftheweb.Salvo.Player newPlayer = playerRepository.save(new com.codeoftheweb.Salvo.Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap(AppMessages.GPID, newPlayer.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //GAME CREATING + JOINING
    @PostMapping("/games/{gameId}/player")
    private ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        com.codeoftheweb.Salvo.Player player = playerRepository.findByUserName(authentication.getName());
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.NOT_FOUND), HttpStatus.UNAUTHORIZED);
        }

        Optional<com.codeoftheweb.Salvo.Game> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.GAME_NOT_FOUND), HttpStatus.FORBIDDEN);
        }

        com.codeoftheweb.Salvo.Game game = optionalGame.get();
        if (game.getGamePlayers().size() > 1) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.GAME_FULL), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().stream().anyMatch(gamePlayer -> gamePlayer.getPlayer().getId() == player.getId())) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.DUPLICATED_ID), HttpStatus.FORBIDDEN);
        }

        com.codeoftheweb.Salvo.GamePlayer gamePlayer = gameplayerRepository.save(new com.codeoftheweb.Salvo.GamePlayer(game, player));
        return new ResponseEntity<>(makeMap(AppMessages.GPID, gamePlayer.getId()), HttpStatus.OK);
    }

    //SHIP CREATION
    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShip(@PathVariable Long gamePlayerId, @RequestBody List<com.codeoftheweb.Salvo.Ship> ships, Authentication authentication) {
        com.codeoftheweb.Salvo.GamePlayer gameplayer = gameplayerRepository.findById(gamePlayerId).orElse(null);
        com.codeoftheweb.Salvo.Player player = playerRepository.findByUserName(authentication.getName());
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_LOGGED), HttpStatus.UNAUTHORIZED);
        } else if (gameplayer == null) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.NOT_FOUND), HttpStatus.NOT_FOUND);
        } else if (gameplayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.FAIL_USER), HttpStatus.UNAUTHORIZED);
        } else if (gameplayer.getShips().size() > 0) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.FOUND_SHIPS), HttpStatus.UNAUTHORIZED);
        } else {
            ships.forEach(ship -> gameplayer.addShip(ship));
            gameplayerRepository.save(gameplayer);
            return new ResponseEntity<>(makeMap(AppMessages.success,AppMessages.SHIPS_HAVE_BEEN_SAVED), HttpStatus.CREATED);
        }
    }

    // SALVOS CREATION
    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> addSalvo(@PathVariable Long gamePlayerId, @RequestBody com.codeoftheweb.Salvo.Salvo salvo, Authentication authentication) {
        com.codeoftheweb.Salvo.GamePlayer gameplayer = gameplayerRepository.findById(gamePlayerId).orElse(null);
        String stateGame = gameplayer.getState();
        com.codeoftheweb.Salvo.Player player = playerRepository.findByUserName(authentication.getName());
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.NOT_LOGGED), HttpStatus.UNAUTHORIZED);
        } else if (gameplayer == null) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.FAIL_USER), HttpStatus.UNAUTHORIZED);
        } else if (gameplayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.FAIL_USER), HttpStatus.UNAUTHORIZED);
        } else if (gameplayer.getOpponent() == null) {
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.WAIT_OPPONENT), HttpStatus.FORBIDDEN);
        } else {
            int diff = gameplayer.getSalvoes().size() - gameplayer.getOpponent().getSalvoes().size();
            if (diff >=1) {
                return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.WAIT_TURN), HttpStatus.UNAUTHORIZED);
            }
            else if (stateGame.equals("YOU_WIN") || stateGame.equals("YOU_LOST") || stateGame.equals("TIE")){
            return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.GAME_OVER), HttpStatus.FORBIDDEN);
            }else if(!stateGame.equals(("ATTACK"))){
                return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.NOT_SALVOES), HttpStatus.FORBIDDEN);
            }else if (gameplayer.getSalvoes().stream().anyMatch(x -> x.getTurn() == salvo.getTurn())) {
                return new ResponseEntity<>(makeMap(AppMessages.error,AppMessages.TURN_FOUND), HttpStatus.FORBIDDEN);
            }else {
                gameplayer.addSalvo(salvo);
                gameplayerRepository.save(gameplayer);
                stateGame = gameplayer.getState();
                switch (stateGame) {
                    case "YOU_WON":
                        scoreRepository.save(new com.codeoftheweb.Salvo.Score(gameplayer.getPlayer(), gameplayer.getGame(),1.0));
                        scoreRepository.save(new com.codeoftheweb.Salvo.Score(gameplayer.getOpponent().getPlayer(),gameplayer.getGame(),0.0));
                        break;
                    case "TIE":
                        scoreRepository.save(new com.codeoftheweb.Salvo.Score(gameplayer.getPlayer(),gameplayer.getGame(),0.5));
                        scoreRepository.save(new com.codeoftheweb.Salvo.Score(gameplayer.getOpponent().getPlayer(),gameplayer.getGame(),0.5));
                        break;
                    case "YOU_LOST":
                        scoreRepository.save (new com.codeoftheweb.Salvo.Score(gameplayer.getPlayer(),gameplayer.getGame(),0.0));
                        scoreRepository.save (new com.codeoftheweb.Salvo.Score(gameplayer.getOpponent().getPlayer(),gameplayer.getGame(),1.0));
                }

                return new ResponseEntity<>(makeMap(AppMessages.success,AppMessages.SALVOES_SAVED), HttpStatus.CREATED);
            }
        }
    }
}


