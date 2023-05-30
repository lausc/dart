package com.itestra.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "Person")
public class Player extends AbstractDomainModel {

    private String name;
    private int points;

    public Player(String name, int points) {
        super();
        this.name = name;
        this.points = points;
    }

    public Player() {
        // Mappings
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
