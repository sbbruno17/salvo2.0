package com.codeoftheweb.Salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    private List<String> locations = new ArrayList<>();

    public Ship() {
    }

    public Ship(String type, List<String> locations) {
        this.type = type;
        this.locations = locations;
    }

    public void setGameplayer(GamePlayer gameplayer) {
        this.gamePlayer = gameplayer;
    }

    public Map<String, Object> toDTOships() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("type", this.type);
        dto.put("locations", this.locations);
        return dto;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<String> getLocations() {
        return locations;
    }
}

