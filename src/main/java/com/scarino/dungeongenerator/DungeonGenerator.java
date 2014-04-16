package com.scarino.dungeongenerator;

/**
 * Created by Santo on 2014-04-14.
 */
public interface DungeonGenerator {

    Tile[][] generate();
    int getWidth();
    int getHeight();

}
