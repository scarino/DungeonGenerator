package com.scarino.dungeongenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Santo on 2014-04-15.
 */
public class CellGenerator implements DungeonGenerator {

    private int cellWidth, cellHeight, cellsX, cellsY;
    private double prob;
    private Tile[][] dungeon;

    final private int ROOM_MIN = 5;

    public CellGenerator(int cellWidth, int cellHeight, int cellsX, int cellsY, double prob){
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.cellsX = cellsX;
        this.cellsY = cellsY;
        this.prob = prob;

        dungeon = new Tile[cellWidth*cellsX][cellHeight*cellsY];
        initializeDungeon();
    }

    @Override
    public Tile[][] generate() {
        initializeDungeon();

        List<Room> rooms = generateRooms();

        copyListToArray(rooms);
        connectRooms(rooms);

        return dungeon;
    }

    private void connectRooms(List<Room> rooms){
        Room first = rooms.remove(0);
        Room next;
        while(rooms.size() > 0){
            next = getClosestRoom(first, rooms);
            addConnections(first, next);
            first = next;
        }
    }

    private void addConnections (Room first, Room second){
        int firstX = first.centerX();
        int secondX = second.centerX();
        int firstY = first.centerY();
        int secondY = second.centerY();

        while(firstX != secondX){
            if(firstX < secondX){
                firstX++;
            }
            else{
                firstX--;
            }
            dungeon[firstX][firstY] = Tile.FLOOR;
        }

        while(firstY != secondY){
            if(firstY < secondY){
                firstY++;
            }
            else{
                firstY--;
            }
            dungeon[firstX][firstY] = Tile.FLOOR;
        }

    }

    private Room getClosestRoom(Room room, List<Room> rooms){
        int curPos = -1;
        int curDis = Integer.MAX_VALUE;

        Room current;
        int nextDis;
        for(int i = 0; i < rooms.size(); i++){
            current = rooms.get(i);
            nextDis = distance(room, current);
            if(nextDis < curDis){
                curPos = i;
                curDis = nextDis;
            }
        }

        return rooms.remove(curPos);
    }

    private int distance(Room roomOne, Room roomTwo){
        int xDis = roomOne.centerX() - roomTwo.centerX();
        xDis *= xDis;

        int yDis = roomOne.centerY() - roomTwo.centerY();
        yDis *= yDis;

        return (int)Math.sqrt(xDis + yDis);
    }

    private List<Room> generateRooms(){
        List<Room> rooms = new ArrayList<Room>(cellsX*cellsY);

        int roomWidth, roomHeight;
        Random rand = new Random();
        for(int i = 0; i < cellsY; i++){
            for(int j = 0; j < cellsX; j++){
                if(rand.nextDouble() < prob) {
                    roomWidth = ROOM_MIN + rand.nextInt(cellWidth - 6);
                    roomHeight = ROOM_MIN + rand.nextInt(cellHeight - 6);
                    rooms.add(new Room(j * cellWidth, i * cellHeight, roomWidth, roomHeight));
                }
            }
        }

        return rooms;
    }

    private void copyListToArray(List<Room> rooms){
        for(Room room : rooms) {
            for (int i = room.getY()+1; i < room.getY() + room.getHeight()-1; i++) {
                for(int j = room.getX()+1; j < room.getX() + room.getWidth()-1; j++){
                    dungeon[j][i] = Tile.FLOOR;
                }
            }
        }
    }

    @Override
    public int getWidth() {
        return cellWidth * cellsX;
    }

    @Override
    public int getHeight() {
        return cellHeight * cellsY;
    }

    private void initializeDungeon(){
        for(int i = 0; i < getHeight(); i++){
            for(int j = 0; j < getWidth(); j++){
                dungeon[j][i] = Tile.WALL;
            }
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(getWidth()*getHeight());

        for(int i = 0; i < getHeight(); i++){
            for(int j = 0; j < getWidth(); j++){
                sb.append(dungeon[j][i].getValue());
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private static class Room {
        int x, y, width, height;

        public Room(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int centerX(){
            return x + (width / 2);
        }

        public int centerY(){
            return y + (height / 2);
        }
    }

    public static void main(String[] args){
        DungeonGenerator dg = new CellGenerator(10,8,10,5,0.65);
        dg.generate();
        System.out.println(dg);
    }
}
