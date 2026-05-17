package com.tamilquran.ift.model.database;

import androidx.room.Dao;
import androidx.room.Query;

import com.tamilquran.ift.model.entity.DbInfoEntity;

@Dao
public interface DbInfoDao {

    @Query("SELECT version FROM db_info LIMIT 1")
    Integer getVersion();
}
