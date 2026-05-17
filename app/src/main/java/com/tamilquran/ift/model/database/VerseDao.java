package com.tamilquran.ift.model.database;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import com.tamilquran.ift.model.entity.VerseEntity;

import java.util.List;

@Dao
public interface VerseDao {

    @Query("SELECT * FROM alquran WHERE sura = :sura ORDER BY _id")
    List<VerseEntity> getVersesForSura(int sura);

    @Query("SELECT * FROM alquran WHERE sura = :sura AND ayah = :ayah LIMIT 1")
    VerseEntity getVerse(int sura, int ayah);

    @Query("SELECT sura, ayah, liked, _id, iftcontent, acontent FROM alquran WHERE liked = 1")
    List<VerseEntity> getFavoriteKeys();

    @Query("SELECT * FROM alquran WHERE liked = 1 ORDER BY _id")
    List<VerseEntity> getFavorites();

    @Query("SELECT COUNT(*) FROM alquran WHERE iftcontent LIKE '%' || :query || '%'")
    int countSearch(String query);

    @Query("SELECT * FROM alquran WHERE iftcontent LIKE '%' || :query || '%' ORDER BY sura, ayah LIMIT :limit OFFSET :offset")
    List<VerseEntity> searchVerses(String query, int offset, int limit);

    @Query("UPDATE alquran SET liked = :liked WHERE sura = :sura AND ayah = :ayah")
    void updateLiked(int sura, int ayah, int liked);
}
