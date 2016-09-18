package com.projectjava.mapgen.jag;

public class JAGFile {
    
    private int hash;
    
    private byte[] data;
    
    public JAGFile(int hash, byte[] data) {
        this.hash = hash;
        this.data = data;
    }
    
    public int getHash() {
        return hash;
    }
    
    public byte[] getData() {
        return data;
    }

}