package com.codeoftheweb.Salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public Map<String, Object> hitsDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("hits", this.getHits());

        return dto;
    }

    public Map<String, Object> sunkenDto() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turns", this.turn);
        dto.put("sunken", this.getSunken());
        return dto;
    }

    public Map<String, Object> shipsLeftDto() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn",this.getTurn());
        dto.put("shipsRemain", this.shipsLeft());
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


    //METHODS
    public List<String> getHits() {

        List<String> allEnemyLocations = new ArrayList<>();

        List<String> hits = new ArrayList<>();

        GamePlayer opponent = this.getGamePlayer().getOpponent();

        if (opponent != null) {

            Set<Ship> enemyShips = opponent.getShips();

            enemyShips.forEach(ship -> allEnemyLocations.addAll(ship.getLocations()));

            hits = this.locations.stream().filter(shot -> allEnemyLocations.contains(shot)).collect(Collectors.toList());
        }
        return hits;
    }

    public List<String> getSunken() {
        List<String> salvos = new ArrayList<>();
        Set<Salvo> mySalvos = this.gamePlayer.getSalvoes().stream()
                .filter(salvo -> this.getTurn() <= this.getTurn()).collect(Collectors.toSet());
        mySalvos.stream().forEach(salvo -> salvos.addAll(salvo.getLocations()));

        GamePlayer opponent = this.getGamePlayer().getOpponent();
        if (opponent != null){
        return this.gamePlayer.getOpponent().getShips().stream().filter(s ->
                salvos.containsAll(s.getLocations())
        ).map(Ship::getType).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public List<String> shipsLeft() {
        List<String> shipsRemain = new ArrayList<>();
        Salvo lastSalvo = gamePlayer.getSalvoes().stream()
                .filter(x -> x.getTurn() == gamePlayer.getSalvoes().size()).findAny().orElse(null);

            if (this.gamePlayer.getOpponent() != null && lastSalvo != null) {
            shipsRemain = this.gamePlayer.getOpponent().getShips().stream().filter(y -> !lastSalvo.getSunken().contains(y.getType()))
                    .map(Ship::getType).collect(Collectors.toList());
        } else {
            shipsRemain = shipsRemain;
        }
        return shipsRemain;
    }

    public int getLeft(){
        List <String> allShips = new ArrayList<>();
        List <String> sunkenShips = this.getSunken();
        if (this.gamePlayer.getOpponent() != null ){
            allShips = this.gamePlayer.getShips().stream().map(Ship::getType).collect(Collectors.toList());
        }
        return allShips.size() - sunkenShips.size();
    }
}

