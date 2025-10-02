package com.Lino.playerWarps.models;

public class Sponsor {

    private final String warpName;
    private final long expirationTime;

    public Sponsor(String warpName, long expirationTime) {
        this.warpName = warpName;
        this.expirationTime = expirationTime;
    }

    public String getWarpName() {
        return warpName;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expirationTime;
    }

    public long getTimeRemaining() {
        return Math.max(0, expirationTime - System.currentTimeMillis());
    }
}