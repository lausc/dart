package com.itestra.domain;

import java.time.LocalDate;
import java.util.List;

public class Game extends AbstractDomainModel {

    private LocalDate date;
    private List<String> playerId;

    public Game(String uuid) {
        super();
    }
}
