package com.projectjava.mapgen.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    // The size of a packed sector
    public static final int SECTOR_WIDTH = 48;
    
    // The size of a packed sector
    public static final int SECTOR_HEIGHT = 48;

    // The gap between each floor (for accurate coordinates)
    public static final int IMAGE_GAP = 96;
    
    // The width of the image
    public static final int SECTOR_IMAGE_WIDTH = SECTOR_WIDTH * 3;
    
    // The height of the image
    public static final int SECTOR_IMAGE_HEIGHT = SECTOR_HEIGHT * 3;
    
    // The maximum amount of x sectors
    public static final int MAX_X_SECTOR = 65;
    
    // The maximum amount of y sectors
    public static final int MAX_Y_SECTOR = 56;
    
    // The maximum height of a sector
    public static final int MAX_Z_SECTOR = 4;
    
    // The final width of the image
    public static final int FINAL_IMAGE_WIDTH = SECTOR_IMAGE_WIDTH * 16;
    
    // The final height of the image
    public static final int FINAL_IMAGE_HEIGHT = SECTOR_IMAGE_HEIGHT * 19;
    
    // The type of image we will generate
    public static final String IMAGE_EXTENSION = "png";
    
    // The directory where all files are loaded from
    public static final String INPUT_DIRECTORY = System.getProperty("user.dir") + File.separator + "input" + File.separator;
    
    // The directory where the final image will be produced
    public static final String OUTPUT_DIR = "";
    //System.getProperty("user.dir") + File.separator + "out" + File.separator;

    // You can fill these with extra filenames if you wish.
    public static HashMap<Integer, String> allPossibleHashes = new HashMap<Integer, String>();
    public static ArrayList<String> existingFilenames = new ArrayList<String>();
}