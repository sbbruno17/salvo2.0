package com.codeoftheweb.Salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores;

    private String userName;

    private String password;

    public Player() {

    }

    public Player(String user, String password) {
        this.userName = user;
        this.password = password;

    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }
    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("userName", this.userName);
        return dto;
    }

    public Score getScore(Game game){
        return this.scores.stream().filter(p -> p.getGame().equals(game)).findFirst().orElse(null);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
