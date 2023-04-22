package com.itestra.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Game extends AbstractDomainModel {

    private LocalDateTime date;
    private List<String> playerId;

    public Game(String uuid) {
        super();
    }
}
