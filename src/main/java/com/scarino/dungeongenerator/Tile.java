package com.scarino.dungeongenerator;

/**
 * Created by Santo on 2014-04-14.
 */
public enum Tile {

    WALL("#"),
    FLOOR("."),
    DOOR("+");

    private String value;

    Tile(String val){
        this.value = val;
    }

    public String getValue(){ return value; }
}
