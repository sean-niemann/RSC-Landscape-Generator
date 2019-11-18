package com.projectjava.mapgen.rscd;

import com.projectjava.mapgen.Generator;
import com.projectjava.mapgen.util.Config;
import com.projectjava.mapgen.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class RSCDGenerator extends Generator {
    
    private Area area;
    
    private ZipFile tileArchive;
    
    public RSCDGenerator() {
        super();
        String fileName = Config.INPUT_DIRECTORY + "rscd" + File.separator + "Landscape.rscd";
        try {
            tileArchive = new ZipFile(fileName);
        } catch(IOException e) {
            System.err.println("Cannot locate landscape file: " + fileName);
            System.exit(1);
        }
        length = 1292;
    }

    @Override
    public void process() {
        for(int z = 0; z < Config.MAX_Z_SECTOR; z++) {
            height = z;
            for(int y = 0; y < (Config.MAX_Y_SECTOR - 37); y++) {
                for(int x = 0; x < (Config.MAX_X_SECTOR - 48); x++) {
                    int xx = Integer.parseInt("" + (x + 48) / 10 + (x + 48) % 10);
                    int yy = Integer.parseInt("" + (y + 37) / 10 + (y + 37) % 10);
                    int zz = z;
                    unpackTiles(zz, xx, yy);
                    tempImage = generateSectorImage();
                    switch(z) {
                        case 0:
                            level0Image.getGraphics().drawImage(tempImage, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
                            break;
                        case 1:
                            level1Image.getGraphics().drawImage(tempImage, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
                            break;
                        case 2:
                            level2Image.getGraphics().drawImage(tempImage, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
                            break;
                        case 3:
                            level3Image.getGraphics().drawImage(tempImage, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
                            break;
                    }
                }    
            }
        }
    }

    @Override
    public int getAt(char type, int x, int y) {
        if(x < 0 || x >= 96 || y < 0 || y >= 96) {
            return 0;
        }
        if(x >= 48 && y < 48) {
            x -= 48;
        } else if(x < 48 && y >= 48) {
            y -= 48;
        } else if(x >= 48 && y >= 48) {
            x -= 48;
            y -= 48;
        }
        switch(type) {
            case 't': return area.getTile(x, y).groundTexture & 0xff;
            case 'o': return area.getTile(x, y).groundOverlay & 0xff;
            case 'd': return area.getTile(x, y).diagonalWalls;
            case 'v': return area.getTile(x, y).verticalWall & 0xff;
            case 'h': return area.getTile(x, y).horizontalWall & 0xff;
            default: return -1;
        }
    }
    
    private void unpackTiles(int height, int sectionX, int sectionY) {
        Area s = null;
        try {
            String filename = "h" + height + "x" + sectionX + "y" + sectionY;
            ZipEntry e = tileArchive.getEntry(filename);
            ByteBuffer tiledata = Util.streamToBuffer(new BufferedInputStream(tileArchive.getInputStream(e)));
            s = Area.unpack(tiledata);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        area = s;
    }
    
}
