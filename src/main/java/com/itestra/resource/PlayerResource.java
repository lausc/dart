package com.itestra.resource;

import com.itestra.domain.Player;
import com.itestra.service.PlayerService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/dart")
public class PlayerResource {

    private PlayerService playerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/players")
    public List<Player> getPlayers() {
        return List.of(new Player("ingo", 10),new Player("lauri", 20));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/players")
    public List<Player> newPlayer(String name) {

        return List.of(new Player("ingo", 10),new Player("lauri", 20));
    }


}
