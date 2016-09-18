package com.projectjava.mapgen.rscd;

import com.projectjava.mapgen.util.Config;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Area {
    
    private Tile[] tiles;

    public Area() {
        tiles = new Tile[Config.SECTOR_WIDTH * Config.SECTOR_HEIGHT];
        for(int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile();
        }
    }

    public void setTile(int x, int y, Tile t) {
        setTile(x * Config.SECTOR_WIDTH + y, t);
    }

    public void setTile(int i, Tile t) {
        tiles[i] = t;
    }

    public Tile getTile(int x, int y) {
        return getTile(x * Config.SECTOR_WIDTH + y);
    }

    public Tile getTile(int i) {
        return tiles[i];
    }

    public static Area unpack(ByteBuffer in) throws IOException {
        int length = Config.SECTOR_WIDTH * Config.SECTOR_HEIGHT;
        if(in.remaining() < (10 * length)) {
            throw new IOException("Provided buffer too short");
        }
        Area area = new Area();
        for(int i = 0; i < length; i++) {
            area.setTile(i, Tile.unpack(in));
        }
        return area;
    }
    
}