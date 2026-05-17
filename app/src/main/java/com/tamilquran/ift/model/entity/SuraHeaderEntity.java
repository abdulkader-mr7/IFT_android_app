package com.tamilquran.ift.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alquran_head")
public class SuraHeaderEntity {

    @PrimaryKey
    @ColumnInfo(name = "surano")
    public int suraNo;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "versecnt")
    public int verseCount;

    @ColumnInfo(name = "bismi")
    public String bismi;

    @ColumnInfo(name = "bismi_arabic")
    public String bismiArabic;

    @ColumnInfo(name = "name_arabic")
    public String nameArabic;

    @ColumnInfo(name = "suratype")
    public String suraType;
}
