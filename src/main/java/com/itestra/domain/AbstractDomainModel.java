package com.itestra.domain;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.UUID;

public class AbstractDomainModel extends PanacheMongoEntity {
    private String uuid;;

    public AbstractDomainModel() {
        this.uuid = UUID.randomUUID().toString();
    }
}
