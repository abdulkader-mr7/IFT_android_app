package com.tamilquran.ift.model.database;

import androidx.room.Dao;
import androidx.room.Query;

import com.tamilquran.ift.model.entity.SuraHeaderEntity;

import java.util.List;

@Dao
public interface SuraDao {

    @Query("SELECT * FROM alquran_head ORDER BY surano")
    List<SuraHeaderEntity> getAllSuraHeaders();

    @Query("SELECT * FROM alquran_head WHERE surano = :suraNo LIMIT 1")
    SuraHeaderEntity getSuraHeader(int suraNo);
}
