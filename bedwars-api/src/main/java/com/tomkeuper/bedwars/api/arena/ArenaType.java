package com.tomkeuper.bedwars.api.arena;

public enum ArenaType {
    PUBLIC, // Arena is public and can be joined by anyone at any time
    PRIVATE, // Arena is private and can only be joined by party members
    EVENT, // Arena is an event arena, only accessible by event participants set by an event manager
    RANKED, // Arena is a ranked arena, MMR is tracked and players are matched based on their MMR
}
