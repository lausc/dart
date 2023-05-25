package com.itestra.service;

import com.itestra.domain.Player;

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
}
