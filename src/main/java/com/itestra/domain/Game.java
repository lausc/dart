package com.itestra.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.LocalDateTime;
import java.util.List;

@MongoEntity(collection = "Game")
public class Game extends AbstractDomainModel {


    private GameTypeEnum gameType;
    private LocalDateTime dateTime;
    private List<String> playerNames;

    public Game() {
        super(); // Wird von JSON benötigt
    }

    public Game(GameTypeEnum gameType, List<String> playerNames) {
        super();
        this.gameType = gameType;
        this.playerNames = playerNames;
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

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }
}
