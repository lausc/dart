package com.itestra.controller;

import com.itestra.domain.Player;
import com.itestra.service.PlayerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;


@Path("/dart")
public class PlayerController {
    private static final Logger log = LogManager.getLogger();
    @Inject
    private PlayerService playerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/players")
    public List<Player> getPlayers() {
        log.info("Spielerliste wird ausgegeben");
        List<Player> allPlayers =  playerService.getAllPlayer();
        return allPlayers;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/players")
    public void storePlayer(Player player) {
        log.info("Spieler wird gespeichert");
        playerService.storePlayer(player);

    }

    @GET
    @Path("/players/tid/{tid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Player getPlayer(@PathParam(value = "tid") String tid) {
        log.info("Lade Spieler mit Id");
        Player player = playerService.getById(tid);
        return player;
    }
}
