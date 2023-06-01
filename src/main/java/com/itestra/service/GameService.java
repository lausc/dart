package com.itestra.service;

import com.itestra.domain.Game;
import com.itestra.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class GameService extends AbstractService<Game> {
    private static final Logger log = LogManager.getLogger();

    @Inject
    private PlayerService playerService;
    public void storeGame(Game game) {
        log.info("Game wird gespeichert");
        if (game.id == null) {
            game.setDateTime(LocalDateTime.now());
        } else {
            throw new RuntimeException("Game schon vorhanden");
        }

        int gamePoints = 3;
        for (String id : game.getPlayerIds()) {
            Player player = playerService.getById(id);
            if (player == null) {
                throw new RuntimeException("Player <" + id + "> nicht gefunden");
            }
            int points = player.getPoints();
            points = points + gamePoints;
            player.setPoints(points);
            if (gamePoints > 0) {
                gamePoints = gamePoints - 1;
            }
            playerService.storePlayer(player);
        }
        game.persistOrUpdate();
    }


    public List<Game> getAllGames() {
        log.info("Alle Spiele werden geladen");
        return Game.findAll().list();
    }


    @Override
    public Game getById(String tid) {
        log.info("Lade Spiel mit Id");
        return Game.findById(new ObjectId((tid)));
    }
}
