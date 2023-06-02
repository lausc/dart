package com.itestra.service;

import com.itestra.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PlayerService extends AbstractService<Player> {
    private static final Logger log = LogManager.getLogger();

    public void storePlayer(Player player) {
        log.info("Spieler wird gespeichert");
        player.persistOrUpdate();
    }

    public List<Player> getAllPlayer() {
        log.info("Spielerliste wird ausgegeben");
        return Player.findAll().list();
    }


    @Override
    public Player getById(String tid) {
        log.info("Lade Spieler mit Id");
        return Player.findById(new ObjectId((tid)));
    }

    public Player getByName(String name) {
        log.info("Lade Spieler mit name");
        return Player.find("name", name).firstResult();
    }
}
