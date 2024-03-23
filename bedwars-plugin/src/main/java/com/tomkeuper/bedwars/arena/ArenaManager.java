/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.tomkeuper.bedwars.arena;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.ArenaTemplate;
import com.tomkeuper.bedwars.api.arena.ArenaType;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.server.RestoreAdapter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

import static com.tomkeuper.bedwars.BedWars.autoscale;
import static com.tomkeuper.bedwars.BedWars.config;

public class ArenaManager {

    private List<ArenaTemplate> templates = new ArrayList<>();

    public static HashMap<UUID, Integer> afkCheck = new HashMap<>();
    public static HashMap<UUID, Integer> magicMilk = new HashMap<>();

    private static final LinkedList<IArena> enableQueue = new LinkedList<>();

    private static final HashMap<String, IArena> arenaByName = new HashMap<>();
    private static final HashMap<Player, IArena> arenaByPlayer = new HashMap<>();
    private static final HashMap<String, IArena> arenaByIdentifier = new HashMap<>();
    private static final LinkedList<IArena> arenas = new LinkedList<>();

    private static int gamesBeforeRestart = config.getInt(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_GAMES_BEFORE_RESTART);

    public String generateGameID() {
        String id;
        try(Jedis jedis = BedWars.getRedisConnection().getDataPool().getResource())
        {
            long counter = jedis.incr("bedwars:gameid");
            return String.valueOf(counter);
        }
        catch (JedisException e)
        {
            BedWars.debug("Could not generate game id: " + e.getMessage());
            return UUID.randomUUID().toString();
        }
    }

    public void spawnArena(ArenaTemplate template)
    {
        if (!canClone(template))
        {
            return;
        }
        String gameID = generateGameID();
        generateWorld(template, gameID);

        gamesBeforeRestart--;
    }

    public void loadArena(ArenaTemplate template)
    {

    }

    public void initializeArena(IArena arena, World world)
    {
        arena.init(world);
        arenas.add(arena);
        arenaByName.put(arena.getArenaName(), arena);
        arenaByIdentifier.put(arena.getWorldName(), arena);
    }

    public void enableArena(IArena arena)
    {

    }

    public void restartArena(IArena arena)
    {

    }

    public void destroyArena(IArena arena)
    {

    }

    public void disableArena(IArena arena)
    {

    }

    public IArena getArenaByName(String name)
    {
        return arenaByName.get(name);
    }

    public IArena getArenaByIdentifier(String identifier)
    {
        return arenaByIdentifier.get(identifier);
    }

    public IArena getArenaByPlayer(Player player)
    {
        return arenaByPlayer.get(player);
    }

    public LinkedList<IArena> getArenas()
    {
        return arenas;
    }

    public List<Player> getBedWarsPlayers()
    {
        List<Player> players = new ArrayList<>();
        for (IArena arena : arenas)
        {
            players.addAll(arena.getPlayers());
        }
        return players;
    }

    public List<Player> getBedWarsSpectators()
    {
        List<Player> players = new ArrayList<>();
        for (IArena arena : arenas)
        {
            players.addAll(arena.getSpectators());
        }
        return players;
    }

    public LinkedList<IArena> getEnableQueue() {
        return enableQueue;
    }

    @Deprecated
    public boolean canClone(String arenaName)
    {
        if (!autoscale) return false;

        //check if the server is restarting
        int totalActiveGames = arenas.size();
        int totalQueuedGames = enableQueue.size();

        if (totalActiveGames + totalQueuedGames >= gamesBeforeRestart)
        {
            return false;
        }

        if (gamesBeforeRestart < 0)
        {
            return false;
        }

        int maximumClone = config.getInt(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_AUTO_SCALE_LIMIT);
        int currentActiveClones = getActiveArenaClones(arenaName).size();
        int currentQueuedClones = getQueuedArenaClones(arenaName).size();

        if (currentActiveClones + currentQueuedClones >= maximumClone)
        {
            return false;
        }

        return true;
    }
    public boolean canClone(ArenaTemplate template)
    {
        if (!autoscale) return false;

        //check if the server is restarting
        int totalActiveGames = arenas.size();
        int totalQueuedGames = enableQueue.size();

        if (totalActiveGames + totalQueuedGames >= gamesBeforeRestart)
        {
            return false;
        }

        if (gamesBeforeRestart < 0)
        {
            return false;
        }

        int maximumClone = config.getInt(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_AUTO_SCALE_LIMIT);
        int currentActiveClones = getActiveArenaClones(template).size();
        int currentQueuedClones = getQueuedArenaClones(template).size();

        if (currentActiveClones + currentQueuedClones >= maximumClone)
        {
            return false;
        }

        return true;

    }

    private List<IArena> getActiveArenaClones(String arenaName)
    {
        List<IArena> clones = new ArrayList<>();
        for (IArena arena : arenas)
        {
            if (arena.getArenaName().equals(arenaName))
            {
                clones.add(arena);
            }
        }
        return clones;
    }

    private List<IArena> getQueuedArenaClones(String arenaName)
    {
        List<IArena> clones = new ArrayList<>();
        for (IArena arena : enableQueue)
        {
            if (arena.getArenaName().equals(arenaName))
            {
                clones.add(arena);
            }
        }
        return clones;
    }

    private List<IArena> getActiveArenaClones(ArenaTemplate template)
    {
        List<IArena> clones = new ArrayList<>();
        for (IArena arena : arenas)
        {
            if (arena.getTemplate().equals(template))
            {
                clones.add(arena);
            }
        }
        return clones;
    }

    private List<IArena> getQueuedArenaClones(ArenaTemplate template)
    {
        List<IArena> clones = new ArrayList<>();
        for (IArena arena : enableQueue)
        {
            if (arena.getTemplate().equals(template))
            {
                clones.add(arena);
            }
        }
        return clones;
    }

    private void generateWorld(ArenaTemplate template, String gameID) throws IllegalArgumentException
    {
        RestoreAdapter adapter = BedWars.getAPI().getRestoreAdapter();
        if (adapter == null)
        {
            throw new IllegalArgumentException("No restore adapter found");
        }

        if (!adapter.isWorld(template.getTemplateName()))
        {
            throw new IllegalArgumentException("There is no template world for " + template.getTemplateName());
        }

        adapter.cloneArena(template.getTemplateName(), gameID);
    }
}
