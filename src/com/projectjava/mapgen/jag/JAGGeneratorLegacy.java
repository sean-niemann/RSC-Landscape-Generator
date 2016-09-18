package com.projectjava.mapgen.jag;

import com.projectjava.mapgen.Generator;
import com.projectjava.mapgen.util.Config;

import java.awt.image.BufferedImage;

import java.io.File;

public class JAGGeneratorLegacy extends Generator {
    
    private JAGArchiveLoader jagLoader = new JAGArchiveLoader();

    private JAGArchive freeLand, mapsFree, memLand, mapsMem;
    
    private boolean members = true;

    private byte[] tileGroundTexture = new byte[2304];

    private int[] wallsDiagonal = new int[2304];

    private byte[] tileGroundOverlay = new byte[2304];

    private byte[] wallsEastwest = new byte[2304];

    private byte[] wallsNorthsouth = new byte[2304];
    
    private byte[] tileGroundElevation = new byte[2304];
    
    private byte[] tileRoofType = new byte[2304];
    
    private byte[] tileObjectRotation = new byte[2304];
    
    public JAGGeneratorLegacy() {
        super();
        mapsFree = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "jag-legacy" + File.separator + "maps.jag");
        length = mapsFree.getFiles().length;
    }

    @Override
    public void process() {
        for(int z = 0; z < Config.MAX_Z_SECTOR; z++) {
            for(int y = 0; y < (Config.MAX_Y_SECTOR - 37); y++) {
                for(int x = 0; x < (Config.MAX_X_SECTOR - 48); x++) {
                    String file = "m" + z + (x + 48) / 10 + (x + 48) % 10 + (y + 37) / 10 + (y + 37) % 10 + ".jm";
                    byte[] data = mapsFree.load(file);
                    BufferedImage image = null;
                    if(data != null && data.length > 0) {
                        unpackTiles(file, data, z);
                        image = generateSectorImage();
                        switch(z) {
                            case 0:
                                level0Image.getGraphics().drawImage(image, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
                                break;
                            case 1:
                                level1Image.getGraphics().drawImage(image, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
                                break;
                            case 2:
                                level2Image.getGraphics().drawImage(image, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
                                break;
                            case 3:
                                level3Image.getGraphics().drawImage(image, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
                                break;
                        }
                        continue;
                    }
                }    
            }
        }
    }
    
    private void unpackTiles(String fileName, byte[] mapData, int height) {
        int val = 0;
        int off = 0;
        for (int tile = 0; tile < 2304; tile++) {
            val = val + mapData[off++] & 0xff;
            tileGroundElevation[tile] = (byte) val;
        }

        val = 0;
        for (int tile = 0; tile < 2304; tile++) {
            val = val + mapData[off++] & 0xff;
            tileGroundTexture[tile] = (byte) val;
        }

        for (int tile = 0; tile < 2304; tile++)
            wallsNorthsouth[tile] = mapData[off++];

        for (int tile = 0; tile < 2304; tile++)
            wallsEastwest[tile] = mapData[off++];

        for (int tile = 0; tile < 2304; tile++) {
            wallsDiagonal[tile] = (mapData[off] & 0xff) * 256 + (mapData[off + 1] & 0xff);
            off += 2;
        }

        for (int tile = 0; tile < 2304; tile++)
            tileRoofType[tile] = mapData[off++];

        for (int tile = 0; tile < 2304; tile++)
            tileGroundOverlay[tile] = mapData[off++];

        for (int tile = 0; tile < 2304; tile++)
            tileObjectRotation[tile] = mapData[off++];
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
            case 't': return tileGroundTexture[x * 48 + y] & 0xff;
            case 'o': return tileGroundOverlay[x * 48 + y] & 0xff;
            case 'd': return wallsDiagonal[x * 48 + y];
            case 'v': return wallsEastwest[x * 48 + y] & 0xff;
            case 'h': return wallsNorthsouth[x * 48 + y] & 0xff;
            default: return -1;
        }
    }
    
}