package com.itestra.domain;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.UUID;

public abstract class AbstractDomainModel extends PanacheMongoEntity {
    private String uuid;

    public AbstractDomainModel() {
        this.uuid = UUID.randomUUID().toString();
    }
    public AbstractDomainModel(String uuid) {
        this.uuid = uuid;
    }
}
