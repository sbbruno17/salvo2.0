package com.codeoftheweb.Salvo;

import java.time.LocalDateTime;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;
import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores;

    private LocalDateTime date;

    public Game() { }

    public Game(LocalDateTime date) {
        this.date = date;
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("created", this.date);
        dto.put("gamePlayers", this.gamePlayers.stream().map(GamePlayer::toDTO).collect(toList()));
        return dto;
    }

    public Set<GamePlayer> getGamePlayers(){
        return gamePlayers;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Set<Score> getScores() {
        return scores;
    }
}

