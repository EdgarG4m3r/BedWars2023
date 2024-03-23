package com.tomkeuper.bedwars.api.arena;

import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.team.TeamColor;
import com.tomkeuper.bedwars.api.configuration.ArenaConfig;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ArenaTemplate {

    private final YamlConfiguration yml;
    private final ArenaConfig cm;
    private String arenaName; // A unique identifier for the template
    private int minPlayers = 2, maxPlayers = 10, maxInTeam = 1, islandRadius = 10, worldBorder;
    private boolean allowSpectate = true, allowMapBreak = false;
    private ArenaGroup arenaGroup;
    private Location respawnLocation, spectatorLocation, waitingLocation;
    private int yKillHeight;
    private List<Location> emeraldGenerators = new ArrayList<>(), diamondGenerators = new ArrayList<>(), teamGenerators = new ArrayList<>();
    private ArenaType arenaType;

    public ArenaTemplate(ArenaConfig arenaConfig) throws IllegalArgumentException
    {
        yml = arenaConfig.getYml();
        cm = arenaConfig;

        if (yml.get("Team") == null) {
            throw new IllegalArgumentException("You didn't set any team for arena: " + arenaConfig.getName());
        }

        if (yml.getConfigurationSection("Team").getKeys(false).size() < 2) {
            throw new IllegalArgumentException("You must set at least 2 teams on: " + arenaConfig.getName());
        }
        maxInTeam = yml.getInt("maxInTeam");
        maxPlayers = yml.getConfigurationSection("Team").getKeys(false).size() * maxInTeam;
        minPlayers = yml.getInt("minPlayers");
        allowSpectate = yml.getBoolean("allowSpectate");
        allowMapBreak = yml.getBoolean("allow-map-break");
        islandRadius = yml.getInt(ConfigPath.ARENA_ISLAND_RADIUS);

        arenaGroup = ArenaGroup.valueOf(yml.getString("group").toUpperCase());
        if (arenaGroup == null) {
            throw new IllegalArgumentException("Invalid group. Must be one of: " + ArenaGroup.values());
        }

        boolean error = false;
        for (String team : yml.getConfigurationSection("Team").getKeys(false)) {
            String colorS = yml.getString("Team." + team + ".Color");
            if (colorS == null) continue;
            colorS = colorS.toUpperCase();
            try {
                TeamColor.valueOf(colorS);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid color at team: " + team + " in arena: " + arenaConfig.getName());
            }
            for (String stuff : List.of("Color", "Spawn", "Bed", "Shop", "Upgrade", "Iron", "Gold")) {
                if (yml.get("Team." + team + "." + stuff) == null) {
                    throw new IllegalArgumentException(stuff + " not set for " + team + " team on: " + arenaConfig.getName());
                }
            }
        }

        if (yml.get("generator.Diamond") == null) {
            throw new IllegalArgumentException("There isn't set any Diamond generator on: " + arenaConfig.getName());
        }

        if (yml.get("generator.Emerald") == null) {
            throw new IllegalArgumentException("There isn't set any Emerald generator on: " + arenaConfig.getName());
        }

        if (yml.get("waiting.Loc") == null) {
            throw new IllegalArgumentException("Waiting spawn not set on: " + arenaConfig.getName());
        }

        waitingLocation = yml.getLocation("waiting.Loc");
        if (waitingLocation == null) {
            throw new IllegalArgumentException("Invalid waiting spawn location on: " + arenaConfig.getName());
        }

        respawnLocation = cm.getArenaLoc(ConfigPath.ARENA_SPEC_LOC);
        if (respawnLocation == null) {
            respawnLocation = cm.getArenaLoc("waiting.Loc");
        }

        spectatorLocation = cm.getArenaLoc(ConfigPath.ARENA_SPEC_LOC);
        if (spectatorLocation == null) {
            spectatorLocation = cm.getArenaLoc("waiting.Loc");
        }

        yKillHeight = yml.getInt(ConfigPath.ARENA_Y_LEVEL_KILL);
        worldBorder = yml.getInt("worldBorder");

        //load diamond generators
        for (String s : yml.getStringList("generator.Diamond")) {
            Location location = cm.convertStringToArenaLocation(s);
            if (location == null) {
                throw new IllegalArgumentException("Invalid location for Diamond generator: " + s);
            }
            diamondGenerators.add(location);
        }

        //load emerald generators
        for (String s : yml.getStringList("generator.Emerald")) {
            Location location = cm.convertStringToArenaLocation(s);
            if (location == null) {
                throw new IllegalArgumentException("Invalid location for Emerald generator: " + s);
            }
            emeraldGenerators.add(location);
        }

        //load team generators
        for (String type : List.of("Iron", "Gold")) {
            GeneratorType gt = GeneratorType.valueOf(type.toUpperCase());
            List<Location> locs = cm.getArenaLocations("Team." + arenaName + "." + type);
            Object o = yml.get("Team." + arenaName + "." + type);
            if (o instanceof String) {
                locs.add(cm.getArenaLoc("Team." + arenaName + "." + type));
            } else {
                locs = cm.getArenaLocations("Team." + arenaName + "." + type);
            }
            for (Location loc : locs) {
                teamGenerators.add(loc);
            }
        }

    }

    public String getTemplateName() {
        return arenaGroup.name() + "_" + arenaType.name() + "_" + arenaName;
    }
}
