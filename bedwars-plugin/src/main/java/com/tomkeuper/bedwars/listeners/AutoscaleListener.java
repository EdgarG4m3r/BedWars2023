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

package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.ArenaTemplate;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AutoscaleListener implements Listener {

    @EventHandler
    public void onPlaying(GameStateChangeEvent e) {
        if (e.getNewState() != GameState.playing) {
            return;
        }

        IArena arena = e.getArena();
        ArenaTemplate template = arena.getTemplate();

        if (BedWars.arenaManager.canClone(template)) {
            BedWars.arenaManager.spawnArena(template);
        }
    }
}
