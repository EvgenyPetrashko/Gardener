package com.example.gardener;

import java.util.ArrayList;

public class OwnPlant{
    // class which represents a plant created by user
    public int id;
    public String name;
    public String desc;
    public String date;

    public OwnPlant(int id, String name, String desc, String date) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.date = date;
    }

    public OwnPlant() {
    }
}
