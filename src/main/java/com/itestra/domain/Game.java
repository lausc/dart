package com.itestra.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@MongoEntity(collection="Game")
public class Game extends AbstractDomainModel {

    private LocalDateTime date;
    private List<String> playerId;

    public Game(String uuid) {
        super();
    }
}
