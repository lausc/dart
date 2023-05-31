package com.itestra.controller;

import com.itestra.domain.Game;
import com.itestra.domain.GameTypeEnum;
import com.itestra.service.GameService;
import com.itestra.service.PlayerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/dart")
public class GameController {
    private static final Logger log = LogManager.getLogger();

    @Inject
    private GameService gameService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/games")
    public List<Game> getPlayers() {
        log.info("Alle Spiele werden geladen");
        List<Game> allGames = gameService.getAllGames();
        return allGames;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/games")
    public void storeGame(Game game) {
        log.info("Game wird gespeichert");
        gameService.storeGame(game);

    }

    @GET
    @Path("/players/tid/{tid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Game getPlayer(@PathParam(value = "tid") String tid) {
        log.info("Lade Spiel mit Id");
       Game game = gameService.getById(tid);
        return game;
    }
}
