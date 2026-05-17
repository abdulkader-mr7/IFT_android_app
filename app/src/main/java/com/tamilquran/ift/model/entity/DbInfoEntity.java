package com.tamilquran.ift.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "db_info")
public class DbInfoEntity {

    @PrimaryKey
    @ColumnInfo(name = "version")
    public int version;
}
