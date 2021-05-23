package com.example.gardener;

public class Plant {
    // class which represents particular plant inside the menu
    public String name;
    public int image_id;
    public int id;

    public Plant(int id, String name, int image_id) {
        this.id = id;
        this.name = name;
        this.image_id = image_id;
    }

    public Plant() {
    }
}
