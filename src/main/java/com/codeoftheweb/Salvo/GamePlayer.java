package com.codeoftheweb.Salvo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private  Set<Salvo> salvoes = new HashSet<>();

    private LocalDateTime time;

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.time = LocalDateTime.now();
    }

    public Map<String, Object> toDTOGameView() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("created", this.time);
        dto.put("gamePlayers", this.game.getGamePlayers().stream().map(GamePlayer::toDTO).collect(toList()));
        dto.put("ships", this.ships.stream().map(Ship::toDTOships).collect(toList()));
        dto.put("salvoes", this.game.getGamePlayers().stream().flatMap(gamePlayer -> gamePlayer.getSalvoes().stream().map(Salvo::toDTOsalvoes)).collect(toList()));
        dto.put("mainPlayerHits",this.salvoes.stream().map(Salvo::hitsDTO).collect(toList()));
       if (this.getOpponent() != null) {
            dto.put ("opponentSunken", this.getOpponent().getSalvoes().stream().map(Salvo::sunkenDto).collect(Collectors.toList()));
       }else{
            dto.put ("opponentSunken",null);
       }
        if (this.getOpponent() != null) {
            dto.put("remainingOppShips", this.getOpponent().getSalvoes().stream().map(Salvo::shipsLeftDto).collect(Collectors.toList()));
        }else{
            dto.put("remainingOppShips", null);
        }
        if (this.getOpponent() != null) {
            dto.put("opponentHits", this.getOpponent().getSalvoes().stream().map(Salvo::hitsDTO).collect(toList()));
        }else{
            dto.put("opponentHits",null);
        }
        dto.put("mainPlayerSunken", this.salvoes.stream().map(Salvo::sunkenDto).collect(Collectors.toList()));
        dto.put("remainingShips", this.salvoes.stream().map(Salvo::shipsLeftDto).collect(Collectors.toList()));
        dto.put("gameState",this.getState());
        return dto;

    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("player", this.player.toDTO());

        Score score = getScore();
        if (score != null) {
            dto.put("score", score.toDTOScore());
        } else {
            dto.put("score", "null");
        }
        return dto;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addShip(Ship ship) {
        ship.setGameplayer(this);
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public GamePlayer getOpponent(){
        return this.getGame().getGamePlayers()
                .stream().filter(gp->gp.getId()!=this.getId())
                .findFirst()
                .orElse(null);
    }
    public Score getScore() {
        Score score = this.player.getScore(game);
        return score;
    }

    public String getState() {
        String state = "";
        if (this.getOpponent() == null) {
            if (this.getShips().isEmpty()) {
                state = "PLACE_SHIPS";
            } else if (this.getOpponent() == null) {
                state = "WAITING_OPPONENT";
            }
        } else {
            int mainTurn = this.getSalvoes().size();
            int oppTurn = this.getOpponent().getSalvoes().size();

            if (this.getShips().isEmpty()) {
                state = "PLACE_SHIPS";
            } else if (this.getOpponent().getShips().isEmpty()) {
                state = "WAITING_OPPONENT_SHIPS";
            } else if (mainTurn > oppTurn) {
                state = "OPPONENT'S_ATTACKING";
            } else if (mainTurn < oppTurn) {
                state = "ATTACK";
            } else if (mainTurn == oppTurn) {
                boolean sinkMain = this.getSalvoes().stream().anyMatch(salvo -> salvo.getLeft() == 0);
                boolean sinkOpp = this.getOpponent().getSalvoes().stream().anyMatch(salvo -> salvo.getLeft() == 0);
                if (sinkMain && !sinkOpp) {
                    state = "YOU_WON";
                } else if (!sinkMain && sinkOpp) {
                    state = "YOU_LOST";
                } else if (sinkMain && sinkOpp) {
                    state = "TIE";
                } else if (this.getId() > this.getOpponent().getId()){
                    state = "ATTACK";
                } else if (this.getId() < this.getOpponent().getId()) {
                    state = "OPPONENT'S_ATTACKING";
                }
            }
        }
    return state;
    }
}
