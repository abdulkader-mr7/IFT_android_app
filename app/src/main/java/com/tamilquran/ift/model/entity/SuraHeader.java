package com.tamilquran.ift.model.entity;

public class SuraHeader {

    public final int suraNo;
    public final String name;
    public final String suraType;
    public final int verseCount;

    public SuraHeader(int suraNo, String name, String suraType, int verseCount) {
        this.suraNo = suraNo;
        this.name = name;
        this.suraType = suraType;
        this.verseCount = verseCount;
    }
}
