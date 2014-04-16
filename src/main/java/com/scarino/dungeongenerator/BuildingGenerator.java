package com.scarino.dungeongenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Santo on 2014-04-16.
 */
public class BuildingGenerator implements DungeonGenerator {

    private Tile[][] dungeon;
    private int width, height, iterations;

    final private int MIN = 3;

    public BuildingGenerator(int width, int height, int iterations){
        this.width = width;
        this.height = height;
        this.iterations = iterations;

        dungeon = new Tile[width][height];
        initializeDungeon();
    }

    @Override
    public Tile[][] generate() {
        initializeDungeon();

        Room root = new Room(0,0, width, height);
        split(root, 0);
        drawDividers(root);
        drawDoors(root);
        drawBorder();

        return dungeon;
    }

    private void drawBorder(){
        for(int i = 0; i < width; i++){
            dungeon[i][0] = Tile.WALL;
            dungeon[i][height-1] = Tile.WALL;
        }
        for(int i = 0; i < height; i++){
            dungeon[0][i] = Tile.WALL;
            dungeon[width-1][i] = Tile.WALL;
        }
    }

    private void drawDoors(Room room){
        if(room.getLeft() != null && room.getRight() != null){
            List<Point> points = new ArrayList<Point>();
            if(room.isSplitVert()){
                for(int i = room.getY(); i < room.getY() + room.getHeight(); i++){
                    if(dungeon[room.getSplitX()+1][i].equals(Tile.FLOOR) &&
                            dungeon[room.getSplitX()-1][i].equals(Tile.FLOOR) &&
                            i != 0 && i != height-1){
                        points.add(new Point(room.getSplitX(), i));
                    }
                }
            }
            else{
                for(int i = room.getX(); i < room.getX() + room.getWidth(); i++){
                    if(dungeon[i][room.getSplitY()+1].equals(Tile.FLOOR) &&
                            dungeon[i][room.getSplitY()-1].equals(Tile.FLOOR) &&
                            i != 0 && i != width-1){
                        points.add(new Point(i, room.getSplitY()));
                    }
                }
            }

            Random rand = new Random();
            Point selection = points.get(rand.nextInt(points.size()));
            dungeon[selection.getX()][selection.getY()] = Tile.DOOR;

            drawDoors(room.getLeft());
            drawDoors(room.getRight());
        }
    }

    private void drawDividers(Room room){
        if(room.getLeft() != null && room.getRight() != null){
            if(room.isSplitVert()){
                for(int i = room.getY(); i < room.getY() + room.getHeight(); i++){
                    dungeon[room.getSplitX()][i] = Tile.WALL;
                }
            }
            else{
                for(int i = room.getX(); i < room.getX() + room.getWidth(); i++){
                    dungeon[i][room.getSplitY()] = Tile.WALL;
                }
            }

            drawDividers(room.getLeft());
            drawDividers(room.getRight());
        }
    }

    private void split(Room room, int iteration){
        if(iteration < iterations &&
                room.getWidth() - (2*MIN + 1) > 0 &&
                room.getHeight() - (2*MIN + 1) > 0){
            Random rand = new Random();
            boolean splitVert = rand.nextBoolean();

            Room leftOrTop;
            Room rightOrBottom;
            int splitPoint;
            if(splitVert){
                splitPoint = MIN + room.getX() + rand.nextInt(room.getWidth() - (2*MIN+1));
                leftOrTop = new Room(room.getX(), room.getY(), splitPoint - room.getX(), room.getHeight());
                rightOrBottom = new Room(splitPoint, room.getY(), room.getX() + room.getWidth() - splitPoint, room.getHeight());
                room.setSplitX(splitPoint);
                room.setSplitY(room.getHeight());
                room.setSplitVert(true);
            }
            else{
                splitPoint = MIN + room.getY() + rand.nextInt(room.getHeight() - (2*MIN+1));
                leftOrTop = new Room(room.getX(), room.getY(), room.getWidth(), splitPoint - room.getY());
                rightOrBottom = new Room(room.getX(), splitPoint, room.getWidth(), room.getY() + room.getHeight() - splitPoint);
                room.setSplitX(room.getWidth());
                room.setSplitY(splitPoint);
                room.setSplitVert(false);
            }

            room.setLeft(leftOrTop);
            room.setRight(rightOrBottom);

            iteration++;
            split(leftOrTop, iteration);
            split(rightOrBottom, iteration);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private void initializeDungeon(){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                dungeon[j][i] = Tile.FLOOR;
            }
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(this.width*this.height);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                sb.append(dungeon[j][i].getValue());
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private static class Room {
        int x, y, width, height, splitX, splitY;
        boolean splitVert;
        Room left, right;

        public Room(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            splitVert = false;
            left = null;
            right = null;
            splitX = -1;
            splitY = -1;
        }

        public void setSplitVert(boolean splitVert){
            this.splitVert = splitVert;
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

        public int getSplitX() {
            return splitX;
        }

        public int getSplitY() {
            return splitY;
        }

        public boolean isSplitVert() {
            return splitVert;
        }

        public Room getLeft() {
            return left;
        }

        public Room getRight() {
            return right;
        }

        public void setRight(Room right) {
            this.right = right;
        }

        public void setLeft(Room left) {
            this.left = left;
        }

        public void setSplitY(int splitY) {
            this.splitY = splitY;
        }

        public void setSplitX(int splitX) {
            this.splitX = splitX;
        }
    }

    private static class Point {
        int x, y;

        public Point(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static void main(String[] args){
        DungeonGenerator dg = new BuildingGenerator(70, 20, 10);
        dg.generate();
        System.out.println(dg);
    }
}
