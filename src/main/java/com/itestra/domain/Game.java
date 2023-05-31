package com.itestra.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@MongoEntity(collection="Game")
public class Game extends AbstractDomainModel {


    private GameTypeEnum gameType;
    private LocalDateTime dateTime;
    private List<String> playerIds;

    public Game() {
        super(); // Wird von JSON benötigt
    }

    public Game(GameTypeEnum gameType, List<String> playerIds) {
        super();
        this.gameType=gameType;
        this.playerIds=playerIds;
    }

    public GameTypeEnum getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeEnum gameType) {
        this.gameType = gameType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds;
    }
}
