package com.itestra.resource;

import com.itestra.domain.Player;
import com.itestra.service.PlayerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/dart")
public class PlayerResource {

    @Inject
    private PlayerService playerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/players")
    public Response getPlayers() {
        List<Player> allPlayer = playerService.getAllPlayer();
        return Response.ok(allPlayer).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/players")
    public Response storePlayer(Player player) {
        playerService.storePlayer(player);
        return Response.ok().build();
    }


}
