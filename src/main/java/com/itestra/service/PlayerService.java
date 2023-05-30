package com.itestra.service;

import com.itestra.domain.Player;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PlayerService extends AbstractService<Player> {
    public void storePlayer(Player player) {
        player.persistOrUpdate();
    }

    public List<Player> getAllPlayer() {
        return Player.findAll().list();
    }


    @Override
    public Player getById(String tid) {
        return Player.findById(new ObjectId((tid)));
    }
}
