package com.itestra.domain;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.UUID;

public class AbstractDomainModel extends PanacheMongoEntity {

    public AbstractDomainModel(){
        // nothing to do
    }
}
