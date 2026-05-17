package com.tamilquran.ift.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "iftbooks")
public class BookEntity {

    @PrimaryKey
    @ColumnInfo(name = "sno")
    public int sno;

    @ColumnInfo(name = "bookfilename")
    public String bookFilename;

    @ColumnInfo(name = "booktitle")
    public String bookTitle;

    @ColumnInfo(name = "cat")
    public String category;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "size")
    public int size;
}
