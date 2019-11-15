package com.projectjava.mapgen.util;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Util {
    
    public static int decodeColor(int i) {
        if(i >= 0) {
            return i == 0xbc614e ? 0 : TEXTURE_COLORS[i];
        }
        i = -(i + 1);
        int j = i >> 10 & 0x1f;
        int k = i >> 5 & 0x1f;
        int l = i & 0x1f;
        return (j << 19) + (k << 11) + (l << 3);
    }
    
    public static int encodeColor(int r, int g, int b) {
        return -1 - (r / 8) * 1024 - (g / 8) * 32 - b / 8;
    }
    
    public static Color convertLongToRGB(int value) {
        return new Color((value >> 16) & 0xff, (value >> 8) & 0xff, value & 0xff);
    }
    
    public static BufferedImage flipXAxis(BufferedImage image) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);
        return image;
    }
    
    public static ByteBuffer streamToBuffer(BufferedInputStream in) throws IOException {
        byte[] buffer = new byte[in.available()];
        in.read(buffer, 0, buffer.length);
        return ByteBuffer.wrap(buffer);
    }
    
    public static BufferedImage joinImages(BufferedImage... images) {
        int rows = images.length;
        int cols = 1;
        int chunkWidth, chunkHeight;
        int type;
        type = images[0].getType();
        chunkWidth = images[0].getWidth();
        chunkHeight = images[0].getHeight();
        BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows + Config.IMAGE_GAP * 3, type);
        int num = 0;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                finalImg.createGraphics().drawImage(images[num], chunkWidth * j, chunkHeight * i + Config.IMAGE_GAP * num, null);
                num++;
            }
        }
        return finalImg;
    }

    public static void readFully(String s, byte[] data, int i) {
        try {
            InputStream inputstream = new BufferedInputStream(new FileInputStream(s));
            DataInputStream datainputstream = new DataInputStream(inputstream);
            datainputstream.readFully(data, 0, i);
            datainputstream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private static final int[] TEXTURE_COLORS = {
        1579036, 4227324, 1579036, 13131780, 1579036,
        1579036, 7350292, 1579036, 0, 0, 11034660, 5797972, 0, 3160132, 1,
        16316668, 0, 0, 1579036, 0, 2052, 10500100, 10500100, 3162164, 0,
        4218996, 0, 1579036, 1579036, 5777436, 6303780, 14704644, 8421508,
        0, 0, 8421508, 0, 6316132, 14211276, 1579020, 0, 0, 542740, 0,
        3162164, 13131780, 13131780, 5783604, 9455636, 11044972, 0,
        1579036, 5777436, 6303780, 14704644
    };

    public static final int[] TILE_COLORS = {
        -16913, 1, 3, 3, -16913, -27685, 25, 12345678,
        -26426, -1, 31, 3, -4534, 32, -9225, -3172, 15, -2, -1,
        -2, -2, -2, -17793, -14594, 1
    };
    
    public static final int[] TILE_UNKNOWN = {
        1, 3, 2, 4, 2, 2, 3, 5, 1, 5, 3, 4, 2, 2,
        2, 2, 2, 2, 3, 4, 4, 0, 2, 1, 3
    };
    
    public static final int[] DOOR_UNKNOWN = {
        0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1,
        0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0,
        1, 1, 1, 1,
    };
    
}