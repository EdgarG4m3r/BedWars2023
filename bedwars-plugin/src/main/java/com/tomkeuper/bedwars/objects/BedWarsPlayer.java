package com.tomkeuper.bedwars.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class BedWarsPlayer {

    private final UUID uuid;
    private Optional<Player> bukkitPlayer = Optional.empty();
    private Optional<BedWarsStats> stats = Optional.empty();
    private final PlayerType playerType;

    public BedWarsPlayer(UUID uuid, PlayerType playerType) {
        this.uuid = uuid;
        this.playerType = playerType;
    }
}
