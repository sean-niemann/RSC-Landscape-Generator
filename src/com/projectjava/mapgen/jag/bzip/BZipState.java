package com.projectjava.mapgen.jag.bzip;

public class BZipState {
    
    protected int tt[], nextIn, availIn, totalInLo32, totalInHi32, availOut, decompressedSize, totalOutLo32,
        totalOutHi32, stateOutLen, bsBuff, bsLive, blocksize100k, blockNo, origPtr, tpos, k0, unzftab[],
        nblockUsed, cftab[], nInUse, mtfbase[], limit[][], base[][], perm[][], minLens[], saveNblock;
    
    protected boolean blockRandomised, inUse[], inUse_16[];
    
    protected byte setToUnseq[], mtfa[], selector[], selectorMtf[], len[][], input[], output[], stateOutCh;
    
    
    protected BZipState() {
        unzftab = new int[256];
        cftab = new int[257];
        inUse = new boolean[256];
        inUse_16 = new boolean[16];
        setToUnseq = new byte[256];
        mtfa = new byte[4096];
        mtfbase = new int[16];
        selector = new byte[18002];
        selectorMtf = new byte[18002];
        len = new byte[6][258];
        limit = new int[6][258];
        base = new int[6][258];
        perm = new int[6][258];
        minLens = new int[6];
    }
    
}