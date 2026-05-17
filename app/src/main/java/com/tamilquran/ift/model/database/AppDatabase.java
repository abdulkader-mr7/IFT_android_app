package com.tamilquran.ift.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tamilquran.ift.constants.DbConstants;
import com.tamilquran.ift.model.entity.BookEntity;
import com.tamilquran.ift.model.entity.DbInfoEntity;
import com.tamilquran.ift.model.entity.SamarasamEntity;
import com.tamilquran.ift.model.entity.SuraHeaderEntity;
import com.tamilquran.ift.model.entity.VerseEntity;

import java.io.File;

@Database(
        entities = {
                VerseEntity.class,
                SuraHeaderEntity.class,
                DbInfoEntity.class,
                BookEntity.class,
                SamarasamEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract VerseDao verseDao();
    public abstract SuraDao suraDao();
    public abstract DbInfoDao dbInfoDao();
    public abstract BookDao bookDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DbConstants.DB_NAME
                    )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public static void resetInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}
