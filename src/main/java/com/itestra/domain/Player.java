package com.itestra.domain;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.UUID;

public class Player extends AbstractDomainModel {


    private String name;
    private long points;

    public Player(String name, long points) {
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

    public void setPoints(long points) {
        this.points = points;
    }
}
