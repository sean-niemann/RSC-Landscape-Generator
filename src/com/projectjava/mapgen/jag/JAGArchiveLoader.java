package com.projectjava.mapgen.jag;

import com.projectjava.mapgen.jag.bzip.BZip;

public class JAGArchiveLoader implements FileLoader {

    private static BinaryFileLoader binaryLoader = new BinaryFileLoader();
    
    @Override
    public byte[] load(String file) {
        byte[] fileData = binaryLoader.load(file);
        
        // reading the archive headers
        int decompLen = ((fileData[0] & 0xff) << 16) + ((fileData[1] & 0xff) << 8) + (fileData[2] & 0xff);
        int compLen = ((fileData[3] & 0xff) << 16) + ((fileData[4] & 0xff) << 8) + (fileData[5] & 0xff);
        
        byte data[] = new byte[fileData.length - 6];
        
        for(int j1 = 0; j1 < fileData.length - 6; j1++) {
            data[j1] = fileData[j1 + 6];
        }
        
        if(decompLen != compLen) {
            byte[] decomp = new byte[decompLen];
            // decompress the archive
            BZip.decompress(decomp, decompLen, data, compLen, 0);
            return decomp;
        }
        return data;
    }
    
    public JAGArchive loadArchive(String file) {
        byte[] archive = load(file);
        return new JAGArchive(archive);
    }

}