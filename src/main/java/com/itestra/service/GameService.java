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


        calculateScore(game);

        for (int i = 0; i < game.getPlayerNames().size(); i++) {
            String playerName = game.getPlayerNames().get(i);
            int count = 0;
            for (int j = 0; j < game.getPlayerNames().size(); j++) {
                String playerNameInListe = game.getPlayerNames().get(j);
                if (playerNameInListe.equals(playerName)) {
                    count++;
                }
            }
            if (count > 1) {
                throw new RuntimeException("Spieler mehr als einmal vorhanden");
            }

        }
        game.persistOrUpdate();
    }

    private void calculateScore(Game game) {
        int gamePoints = 0;
        if (game.getPlayerNames().size() > 3) {
            gamePoints = 3;
        } else if (game.getPlayerNames().size() > 2) {
            gamePoints = 2;
        } else if (game.getPlayerNames().size() > 1) {
            gamePoints = 1;
        }
        for (String name : game.getPlayerNames()) {
            Player player = playerService.getByName(name);
            if (player == null) {
                throw new RuntimeException("Player <" + name + "> nicht gefunden");
            }
            int points = player.getPoints();
            points = points + gamePoints;
            if (gamePoints > 0) {
                gamePoints = gamePoints - 1;
            }
            player.setPoints(points);

            playerService.storePlayer(player);
        }
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

    public void delete(String tid) {
        log.info("Lösche Spiel");
        Game game = getById(tid);
        if (game != null) {
            game.delete();
        } else {
            log.info("Game nicht gefunden");
        }

    }

    public void recalculateScore() {
        List<Player> players = playerService.getAllPlayer();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            player.setPoints(0);
            playerService.storePlayer(player);
        }
        List<Game> games = getAllGames();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            calculateScore(game);

        }

    }
}
