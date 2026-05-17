package com.tamilquran.ift.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "alquran",
        indices = {@Index("sura"), @Index("liked")}
)
public class VerseEntity {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "sura")
    public int sura;

    @ColumnInfo(name = "ayah")
    public int ayah;

    @ColumnInfo(name = "iftcontent")
    public String tamilContent;

    @ColumnInfo(name = "acontent")
    public String arabicContent;

    @ColumnInfo(name = "liked")
    public int liked;
}
