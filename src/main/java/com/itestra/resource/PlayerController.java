package com.itestra.resource;

import com.itestra.domain.Player;
import com.itestra.service.PlayerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;


@Path("/dart")
public class PlayerController {

    @Inject
    private PlayerService playerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/players")
    public List<Player> getPlayers() {
        List<Player> allPlayers = (ArrayList<Player>) playerService.getAllPlayer();
        return allPlayers;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/players")
    public void storePlayer(Player player) {
        playerService.storePlayer(player);
        return;
    }

    @GET
    @Path("/players/tid/{tid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Player getPlayer(@PathParam(value = "tid") String tid) {
        Player player = playerService.getById(tid);
        return player;
    }
}
