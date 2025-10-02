package com.Lino.playerWarps.models;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public class Warp {

    private final String name;
    private final UUID owner;
    private Location location;
    private String description;
    private Material icon;

    public Warp(String name, UUID owner, Location location) {
        this.name = name;
        this.owner = owner;
        this.location = location;
        this.description = "";
        this.icon = Material.ENDER_PEARL;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }
}