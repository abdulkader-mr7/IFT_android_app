package com.tamilquran.ift.model.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.tamilquran.ift.model.entity.BookEntity;
import com.tamilquran.ift.model.entity.SamarasamEntity;

import java.util.List;

@Dao
public interface BookDao {

    @Query("SELECT * FROM iftbooks ORDER BY sno DESC")
    List<BookEntity> getIftBooks();

    @Query("SELECT COALESCE(MAX(sno), 0) FROM iftbooks")
    int getMaxIftBookSno();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIftBook(BookEntity book);

    @Query("SELECT * FROM samarasam ORDER BY sno DESC")
    List<SamarasamEntity> getSamarasamBooks();

    @Query("SELECT COALESCE(MAX(sno), 0) FROM samarasam")
    int getMaxSamarasamSno();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSamarasam(SamarasamEntity book);
}
