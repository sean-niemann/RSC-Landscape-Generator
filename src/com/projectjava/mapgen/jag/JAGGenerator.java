package com.projectjava.mapgen.jag;

import com.projectjava.mapgen.Generator;
import com.projectjava.mapgen.util.Config;

import java.awt.image.BufferedImage;
import java.io.File;

public class JAGGenerator extends Generator {
    
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
    
    public JAGGenerator() {
        super();
        freeLand = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "jag" + File.separator + "land.jag");
        mapsFree = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "jag" + File.separator + "maps.jag");
        length = freeLand.getFiles().length;
        if(members) {
            memLand = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "jag" + File.separator + "land.mem");
            mapsMem = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "jag" + File.separator + "maps.mem");
            length += memLand.getFiles().length;
        }
    }
    
    int fj = 0;

    @Override
    public void process() {
        for(int z = 0; z < Config.MAX_Z_SECTOR; z++) {
            for(int y = 0; y < (Config.MAX_Y_SECTOR - 37); y++) {
                for(int x = 0; x < (Config.MAX_X_SECTOR - 48); x++) {
                    String file = "m" + z + (x + 48) / 10 + (x + 48) % 10 + (y + 37) / 10 + (y + 37) % 10;
                    byte[] data = freeLand.load(file + ".hei");
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
                    if(members) {
                        data = memLand.load(file + ".hei");
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
                        }
                    }
                }    
            }
        }
    }
    
    private void unpackTiles(String fileName, byte[] data, int height) {
        int off = 0;
        int lastValue = 0;

        for (int tile = 0; tile < 2304;) {
            int value = data[off++] & 0xff;

            if (value < 128) {
                tileGroundElevation[tile++] = (byte)value;
                lastValue = value;
            }
            if (value >= 128) {
                for (int i = 0; i < value - 128; i++) {
                    tileGroundElevation[tile++] = (byte) lastValue;
                }
            }
        }

        lastValue = 64;
        for (int w = 0; w < 48; w++) {
            for (int h = 0; h < 48; h++) {
                lastValue = tileGroundElevation[h * 48 + w] + lastValue & 0x7f;
                tileGroundElevation[h * 48 + w] = (byte)(lastValue * 2);
            }

        }

        lastValue = 0;
        for (int tile = 0; tile < 2304;) {
            int value = data[off++] & 0xff;
            if (value < 128) {
                tileGroundTexture[tile++] = (byte) value;
                lastValue = value;
            }
            if (value >= 128) {
                for (int i = 0; i < value - 128; i++) {
                    tileGroundTexture[tile++] = (byte)lastValue;
                }
            }
        }

        lastValue = 35;
        for (int w = 0; w < 48; w++) {
            for (int h = 0; h < 48; h++) {
                lastValue = tileGroundTexture[h * 48 + w] + lastValue & 0x7f;
                tileGroundTexture[h * 48 + w] = (byte)(lastValue * 2);
            }
        }

        data = mapsFree.load(fileName + ".dat");
        off = 0;

        if(data == null) {
            if(members) {
                data = mapsMem.load(fileName + ".dat");
            } else {
                return;
            }
        }

        for(int tile = 0; tile < 2304; tile++) {
            wallsNorthsouth[tile] = data[off++];
        }

        for(int tile = 0; tile < 2304; tile++) {
            wallsEastwest[tile] = data[off++];
        }

        for(int tile = 0; tile < 2304; tile++)
            wallsDiagonal[tile] = data[off++] & 0xff;

        for(int tile = 0; tile < 2304; tile++) {
            int value = data[off++] & 0xff;
            if(value > 0) {
                wallsDiagonal[tile] = value + 12000;
            }
        }

        for(int tile = 0; tile < 2304;) {
            int value = data[off++] & 0xff;
            if(value < 128) {
                tileRoofType[tile++] = (byte)value;
            } else {
                for(int i = 0; i < value - 128; i++) {
                    tileRoofType[tile++] = 0;
                }
            }
        }

        lastValue = 0;
        for(int tile = 0; tile < 2304;) {
            int value = data[off++] & 0xff;
            if(value < 128) {
                tileGroundOverlay[tile++] = (byte) value;
                lastValue = value;
            } else {
                for(int i = 0; i < value - 128; i++) {
                    tileGroundOverlay[tile++] = (byte) lastValue;
                }
            }
        }

        for(int tile = 0; tile < 2304;) {
            int value = data[off++] & 0xff;
            if(value < 128) {
                tileObjectRotation[tile++] = (byte)value;
            } else {
                for(int l10 = 0; l10 < value - 128; l10++) {
                    tileObjectRotation[tile++] = 0;
                }
            }
        }

        data = mapsFree.load(fileName + ".loc");

        if(data != null && data.length > 0) {
            int index = 0;
            for(int tile = 0; tile < 2304;) {
                int value = data[index++] & 0xff;
                if(value < 128)
                    wallsDiagonal[tile++] = value + 48000;
                else
                    tile += value - 128;
            }
            return;
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
            case 't': return tileGroundTexture[x * 48 + y] & 0xff;
            case 'o': return tileGroundOverlay[x * 48 + y] & 0xff;
            case 'd': return wallsDiagonal[x * 48 + y];
            case 'v': return wallsEastwest[x * 48 + y] & 0xff;
            case 'h': return wallsNorthsouth[x * 48 + y] & 0xff;
            default: return -1;
        }
    }
    
}