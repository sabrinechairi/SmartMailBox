package com.example.smartmailbox;

public class Machine {
    private String name;
    private String location;
    private String createdAt;
    private int id;

    public Machine(int id, String name, String location, String createdAt) {
        this.name = name;
        this.location = location;
        this.createdAt = createdAt;
        this.id=id;
    }

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
