package com.codeoftheweb.Salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "location")
    private List<String> locations = new ArrayList<>();

    private int turn;

    public Salvo() {
    }

    public Salvo(List<String> locations, int turn) {
        this.locations = locations;
        this.turn = turn;
    }

    public Map<String, Object> toDTOsalvoes() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("playerId", this.gamePlayer.getPlayer().getId());
        dto.put("locations", this.locations);
        return dto;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public int getTurn() {
        return turn;
    }

    public void setGamePlayer(GamePlayer gameplayer) {
        this.gamePlayer = gameplayer;
    }

}
