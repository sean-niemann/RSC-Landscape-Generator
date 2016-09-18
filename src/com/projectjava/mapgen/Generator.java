package com.projectjava.mapgen;

import com.projectjava.mapgen.jag.JAGGenerator;
import com.projectjava.mapgen.jag.JAGGeneratorLegacy;
import com.projectjava.mapgen.rscd.RSCDGenerator;
import com.projectjava.mapgen.util.Config;
import com.projectjava.mapgen.util.Util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Generator {
    
    public static void main(String[] args) {
        if(args.length == 1) {
            switch(args[0]) {
                case "rscd":        new RSCDGenerator().begin();        break;
                case "jag":         new JAGGenerator().begin();         break;
                case "jag-legacy":  new JAGGeneratorLegacy().begin();   break;
            }
            return;
        }
        System.err.println("Usage: java -jar mapgen.jar [rscd/jag/jag-legacy]");
    }
    
    public int height;

    private int[] tileColors = new int[256];
    
    private int processed = 0;
    
    public int length = 0;
    
    public BufferedImage level0Image, level1Image, level2Image, level3Image, tempImage;
    
    public Generator() {
        for(int i = 0; i < 64; i++) {
            tileColors[i] = Util.encodeColor(255 - i * 4, 255 - (int) ((double) i * 1.75D), 255 - i * 4);
        }
        for(int i = 0; i < 64; i++) {
            tileColors[i + 64] = Util.encodeColor(i * 3, 144, 0);
        }
        for(int i = 0; i < 64; i++) {
            tileColors[i + 128] = Util.encodeColor(192 - (int) ((double) i * 1.5D), 144 - (int) ((double) i * 1.5D), 0);
        }
        for(int i = 0; i < 64; i++) {
            tileColors[i + 192] = Util.encodeColor(96 - (int) ((double) i * 1.5D), 48 + (int) ((double) i * 1.5D), 0);
        }
        level0Image = new BufferedImage(Config.FINAL_IMAGE_WIDTH, Config.FINAL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        level1Image = new BufferedImage(Config.FINAL_IMAGE_WIDTH, Config.FINAL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        level2Image = new BufferedImage(Config.FINAL_IMAGE_WIDTH, Config.FINAL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        level3Image = new BufferedImage(Config.FINAL_IMAGE_WIDTH, Config.FINAL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
    
    public void begin() {
        BufferedImage finalImage;
        File file = new File(Config.OUTPUT_DIR + "map." + Config.IMAGE_EXTENSION);
        process();
        finalImage = Util.joinImages(level0Image, level3Image, level1Image, level2Image);
        finalImage = Util.flipXAxis(finalImage);
        int x = Config.FINAL_IMAGE_WIDTH / 2;
        int y = Config.FINAL_IMAGE_HEIGHT;
        Graphics graphics = finalImage.getGraphics();
        graphics.setFont(new Font("Arial", Font.BOLD, 48));
        graphics.drawString("Ground Floor", x, 100);
        graphics.drawString("Underground", x, y + 100);
        graphics.drawString("First Floor", x, y * 2 + 100);
        graphics.drawString("Second Floor", x, y * 3 + 100);
        
        System.out.println("\nWriting image to " + Config.OUTPUT_DIR + "map.png");
        
        try {
            ImageIO.write(finalImage, Config.IMAGE_EXTENSION, file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public abstract void process();
    
    public abstract int getAt(char type, int x, int y);
    
    public BufferedImage generateSectorImage() {
        BufferedImage image = new BufferedImage(Config.SECTOR_IMAGE_WIDTH, Config.SECTOR_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        applySector(image);
        Util.flipXAxis(image);
        System.out.printf("\rProcessed: %.2f%%", ((double) (processed++ + 1.0D) / length) * 100.0D);
        return image;
    }
    
    private void applySector(BufferedImage image) {    
        Graphics gfx = image.getGraphics();
        for(int x = 0; x < Config.SECTOR_IMAGE_WIDTH - 1; ++x) {
            for(int y = 0; y < Config.SECTOR_IMAGE_HEIGHT - 1; ++y) {
                int texture = getAt('t', x, y);
                int color1 = tileColors[texture];
                int color2 = color1;
                int underlay = color1;
                int renderType = 0;
                if(height == 1 || height == 2) {
                    color1 = 0xbc614e;
                    color2 = 0xbc614e;
                    underlay = 0xbc614e;
                }
                if(getAt('o', x, y) > 0) {
                    int overlay = getAt('o', x, y);

                    if(overlay - 1 >= Util.TILE_UNKNOWN.length) continue; // err fixed weird npe error

                    int l5 = Util.TILE_UNKNOWN[overlay - 1];
                    int i19 = getUnknownValue(x, y);
                    color1 = color2 = Util.TILE_COLORS[overlay - 1];
                    if(l5 == 5) {
                        if(getAt('d', x, y) > 0 && getAt('d', x, y) < 24000)
                            if(getOverlayIfRequired(x - 1, y, underlay) != 0xbc614e && getOverlayIfRequired(x, y - 1, underlay) != 0xbc614e) {
                                color1 = getOverlayIfRequired(x - 1, y, underlay);
                                renderType = 0;
                            } else if(getOverlayIfRequired(x + 1, y, underlay) != 0xbc614e && getOverlayIfRequired(x, y + 1, underlay) != 0xbc614e) {
                                color2 = getOverlayIfRequired(x + 1, y, underlay);
                                renderType = 0;
                            } else if(getOverlayIfRequired(x + 1, y, underlay) != 0xbc614e && getOverlayIfRequired(x, y - 1, underlay) != 0xbc614e) {
                                color2 = getOverlayIfRequired(x + 1, y, underlay);
                                renderType = 1;
                            } else if(getOverlayIfRequired(x - 1, y, underlay) != 0xbc614e && getOverlayIfRequired(x, y + 1, underlay) != 0xbc614e) {
                                color1 = getOverlayIfRequired(x - 1, y, underlay);
                                renderType = 1;
                            }
                    } else if(l5 != 2 || getAt('d', x, y) > 0 && getAt('d', x, y) < 24000) {
                        if(getUnknownValue(x - 1, y) != i19 && getUnknownValue(x, y - 1) != i19) {
                            color1 = underlay;
                            renderType = 0;
                        } else if(getUnknownValue(x + 1, y) != i19 && getUnknownValue(x, y + 1) != i19) {
                            color2 = underlay;
                            renderType = 0;
                        } else if(getUnknownValue(x + 1, y) != i19 && getUnknownValue(x, y - 1) != i19) {
                            color2 = underlay;
                            renderType = 1;
                        } else if(getUnknownValue(x - 1, y) != i19 && getUnknownValue(x, y + 1) != i19) {
                            color1 = underlay;
                            renderType = 1;
                        }
                    }
                }
                drawTile(gfx, x, y, renderType, color1, color2);
            }
        }

        int color = 0x606060;

        for(int x = 0; x < Config.SECTOR_IMAGE_WIDTH - 1; x++) {
            for(int y = 0; y < Config.SECTOR_IMAGE_HEIGHT - 1; y++) {
                int tileValue = getAt('h', x, y);

                if(tileValue > 0 && Util.DOOR_UNKNOWN[tileValue - 1] == 0) {
                    drawLineY(gfx, x * 3, y * 3, 3, color);
                }

                tileValue = getAt('v', x, y);
                if(tileValue > 0 && Util.DOOR_UNKNOWN[tileValue - 1] == 0) {
                    drawLineX(gfx, x * 3, y * 3, 3, color);
                }

                tileValue = getAt('d', x, y);
                if(tileValue > 0 && tileValue < 12000 && Util.DOOR_UNKNOWN[tileValue - 1] == 0) {
                    setPixelColour(gfx, x * 3, y * 3, color);
                    setPixelColour(gfx, x * 3 + 1, y * 3 + 1, color);
                    setPixelColour(gfx, x * 3 + 2, y * 3 + 2, color);
                }

                if(tileValue > 12000 && tileValue < 24000 && Util.DOOR_UNKNOWN[tileValue - 12001] == 0) {
                    setPixelColour(gfx, x * 3 + 2, y * 3, color);
                    setPixelColour(gfx, x * 3 + 1, y * 3 + 1, color);
                    setPixelColour(gfx, x * 3, y * 3 + 2, color);
                }
            }
        }
    }

    public void drawTile(Graphics gfx, int x, int y, int type, int base1, int base2) {
        int xx = x * 3;
        int yy = y * 3;
        int color1 = Util.decodeColor(base1);
        int color2 = Util.decodeColor(base2);
        color1 = color1 >> 1 & 0x7f7f7f;
        color2 = color2 >> 1 & 0x7f7f7f;

        if(type == 0) {
            drawLineX(gfx, xx, yy, 3, color1);
            drawLineX(gfx, xx, yy + 1, 2, color1);
            drawLineX(gfx, xx, yy + 2, 1, color1);
            drawLineX(gfx, xx + 2, yy + 1, 1, color2);
            drawLineX(gfx, xx + 1, yy + 2, 2, color2);
        } else if(type == 1) {
            drawLineX(gfx, xx, yy, 3, color2);
            drawLineX(gfx, xx + 1, yy + 1, 2, color2);
            drawLineX(gfx, xx + 2, yy + 2, 1, color2);
            drawLineX(gfx, xx, yy + 1, 1, color1);
            drawLineX(gfx, xx, yy + 2, 2, color1);
        }
    }

    private void drawLineX(Graphics gfx, int x, int y, int length, int color) {
        gfx.setColor(Util.convertLongToRGB(color));
        gfx.drawLine(x, y, x + length, y);
    }

    private void drawLineY(Graphics gfx, int x, int y, int length, int color) {
        gfx.setColor(Util.convertLongToRGB(color));
        gfx.drawLine(x, y, x, y + length);
    }

    private void setPixelColour(Graphics gfx, int x, int y, int color) {
        gfx.setColor(Util.convertLongToRGB(color));
        gfx.drawLine(x, y, x, y);
    }
    
    public int getOverlayIfRequired(int x, int y, int underlay) {
        int texture = getAt('o', x, y);
        if(texture == 0) {
            return underlay;
        }
        return Util.TILE_COLORS[texture - 1];
    }

    public int getUnknownValue(int x, int y) {
        int texture = getAt('o', x, y);
        if(texture - 1 == 249) return 1; // err fixed weird oob error
        if(texture == 0) {
            return -1;
        }
        return Util.TILE_UNKNOWN[texture - 1] != 2 ? 0 : 1;
    }
    
}