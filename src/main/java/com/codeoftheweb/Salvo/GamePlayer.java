package com.codeoftheweb.Salvo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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
    private Set<Salvo> salvoes = new HashSet<>();

    private LocalDateTime time;

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.time = LocalDateTime.now();
    }

    public Map<String,Object>toDTOGameView() {
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put ("id",this.id);
        dto.put("created", this.time);
        dto.put("gamePlayers", this.game.getGamePlayers().stream().map(GamePlayer::toDTO).collect(toList()));
        dto.put("ships", this.ships.stream().map(Ship::toDTOships).collect(toList()));
        dto.put("salvoes", this.game.getGamePlayers().stream().flatMap(gamePlayer -> gamePlayer.getSalvoes().stream().map(Salvo::toDTOsalvoes)).collect(toList()));
        return dto;
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("player", this.player.toDTO());

       Score score = getScore();
        if (score != null) {
            dto.put("score", score.toDTOScore());
        }
            else{
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

    public Game getGame() {return game;}

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public Set<Ship> getShips() {
        return ships;
    }


    public void addShip(Ship ship) {
        ship.setGameplayer(this);
        ships.add(ship);
    }
    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }
    public Score getScore () {
        Score score = this.player.getScore(game);
        return score;
    }
}
