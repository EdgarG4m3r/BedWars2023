package com.tomkeuper.bedwars.objects;

import lombok.Getter;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.UUID;

@Getter
public class ExpectedPlayer {
    private final UUID uuid;
    private final ExpectedType expectedType;
    private final long createTime;
    private final long expireTime;
    private JSONObject data;

    public ExpectedPlayer(UUID uuid, ExpectedType expectedType, long expireTime, JSONObject data) {
        this.uuid = uuid;
        this.expectedType = expectedType;
        this.createTime = System.currentTimeMillis();
        this.expireTime = expireTime;
        this.data = data;
    }
}
