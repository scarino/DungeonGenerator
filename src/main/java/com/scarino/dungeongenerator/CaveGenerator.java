package com.scarino.dungeongenerator;

import com.scarino.dungeongenerator.exception.InvalidSizeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Santo on 2014-04-14.
 */
public class CaveGenerator implements DungeonGenerator {

    private int width, height;
    private int seedCount;
    private int iterations;
    private Tile[][] dungeon;

    public CaveGenerator(int width, int height, int seeds, int iterations) throws InvalidSizeException {
        if((width * height) < (iterations + seeds) || width == 0 || height == 0){
            throw new InvalidSizeException("Dungeon is too small to generate.");
        }

        this.width = width;
        this.height = height;
        this.seedCount = seeds;
        this.iterations = iterations;

        dungeon = new Tile[this.width][this.height];
        initializeDungeon();
    }

    @Override
    public Tile[][] generate() {
        initializeDungeon();

        List<Position> seeds = generateSeeds();
        List<Position> dungeonList = new ArrayList<Position>(iterations+seedCount);
        List<Position> potentialTiles = new ArrayList<Position>(iterations+seedCount);

        for(Position pos : seeds){
            dungeonList.add(pos);
            potentialTiles.addAll(getNeighbours(pos));
        }

        int count = 0;
        Random rand = new Random();
        while(count < iterations){
            int next = rand.nextInt(potentialTiles.size());
            Position pos = potentialTiles.remove(next);
            if(!posExists(pos.getX(), pos.getY(), dungeonList) && !edgePos(pos)) {
                dungeonList.add(pos);
                potentialTiles.addAll(getNeighbours(pos));
                count++;
            }
        }

        connectSeeds(seeds, dungeonList);
        copyListToArray(dungeonList);

        return dungeon;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    private boolean edgePos(Position pos){
        return pos.getY() == 0 || pos.getY() == this.height - 1 ||
                pos.getX() == 0 || pos.getX() == this.width - 1;

    }

    private List<Position> getNeighbours(Position pos){
        List<Position> neighbours = new ArrayList<Position>();

        neighbours.add(new Position(pos.getX()-1, pos.getY()-1));
        neighbours.add(new Position(pos.getX(), pos.getY()-1));
        neighbours.add(new Position(pos.getX()+1, pos.getY()-1));
        neighbours.add(new Position(pos.getX()-1, pos.getY()));
        neighbours.add(new Position(pos.getX()+1, pos.getY()));
        neighbours.add(new Position(pos.getX()-1, pos.getY()+1));
        neighbours.add(new Position(pos.getX(), pos.getY()+1));
        neighbours.add(new Position(pos.getX()+1, pos.getY()+1));

        return neighbours;
    }

    private void connectSeeds(List<Position> seeds, List<Position> dungeonList){
        Position first = seeds.remove(0);
        Position next;
        while(seeds.size() > 0){
            next = getClosestSeed(first, seeds);
            addConnections(first, next, dungeonList);
            first = next;
        }
    }

    private void addConnections (Position first, Position second, List<Position> dungeonList){
        int firstX = first.getX();
        int secondX = second.getX();
        int firstY = first.getY();
        int secondY = second.getY();

        while(firstX != secondX){
            if(firstX < secondX){
                firstX++;
            }
            else{
                firstX--;
            }

            dungeonList.add(new Position(firstX, firstY));
        }

        while(firstY != secondY){
            if(firstY < secondY){
                firstY++;
            }
            else{
                firstY--;
            }

            dungeonList.add(new Position(firstX, firstY));
        }
    }

    private Position getClosestSeed(Position seed, List<Position> seeds){
        int curPos = -1;
        int curDis = Integer.MAX_VALUE;

        Position current;
        int nextDis;
        for(int i = 0; i < seeds.size(); i++){
            current = seeds.get(i);
            nextDis = distance(seed, current);
            if(nextDis < curDis){
                curPos = i;
                curDis = nextDis;
            }
        }

        return seeds.remove(curPos);
    }

    private int distance(Position seedOne, Position seedTwo){
        int xDis = seedOne.getX() - seedTwo.getX();
        xDis *= xDis;

        int yDis = seedOne.getY() - seedTwo.getY();
        yDis *= yDis;

        return (int)Math.sqrt(xDis + yDis);
    }

    private void copyListToArray(List<Position> dungeonList){
        for(Position pos : dungeonList){
            dungeon[pos.getX()][pos.getY()] = Tile.FLOOR;
        }
    }

    private void initializeDungeon(){
        for(int i = 0; i < this.height; i++){
            for(int j = 0; j < this.width; j++){
                dungeon[j][i] = Tile.WALL;
            }
        }
    }

    private List<Position> generateSeeds(){
        List<Position> seeds = new ArrayList<Position>(seedCount);

        Random rand = new Random();
        int count = 0;
        while(count < seedCount){
            int x = 1 + rand.nextInt(this.width-2);
            int y = 1 + rand.nextInt(this.height-2);

            if(!posExists(x, y, seeds)){
                seeds.add(new Position(x, y));
                count++;
            }
        }

        return seeds;
    }

    public boolean posExists(int x, int y, List<Position> positions){

        for(Position pos : positions){
            if(x == pos.getX() && y == pos.getY()){
                return true;
            }
        }

        return false;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(this.width*this.height);

        for(int i = 0; i < this.height; i++){
            for(int j = 0; j < this.width; j++){
                sb.append(dungeon[j][i].getValue());
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private static class Position {
        int x, y;

        public Position(int x, int y){
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
        DungeonGenerator dg = null;
        try {
            dg = new CaveGenerator(80, 20, 5, 300);
        }
        catch(InvalidSizeException ex){
            ex.printStackTrace();
            System.exit(0);
        }
        dg.generate();
        System.out.println(dg);
    }
}
