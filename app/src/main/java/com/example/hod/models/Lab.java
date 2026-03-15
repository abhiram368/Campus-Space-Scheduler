package com.example.hod.models;

public class Lab {
    public String name;
    public String id;
    public String capacity;
    public String systems;
    public String location;
    public String admin;

    public Lab() {
        // Default constructor for Firebase
    }

    public Lab(String name, String id, String capacity, String systems, String location, String admin) {
        this.name = name;
        this.id = id;
        this.capacity = capacity;
        this.systems = systems;
        this.location = location;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getSystems() {
        return systems;
    }

    public void setSystems(String systems) {
        this.systems = systems;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
