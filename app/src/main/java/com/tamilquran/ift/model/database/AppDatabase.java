package com.tamilquran.ift.model.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.tamilquran.ift.constants.DbConstants;
import com.tamilquran.ift.model.entity.BookEntity;
import com.tamilquran.ift.model.entity.DbInfoEntity;
import com.tamilquran.ift.model.entity.SamarasamEntity;
import com.tamilquran.ift.model.entity.SuraHeaderEntity;
import com.tamilquran.ift.model.entity.VerseEntity;

@Database(
        entities = {
                VerseEntity.class,
                SuraHeaderEntity.class,
                DbInfoEntity.class,
                BookEntity.class,
                SamarasamEntity.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    /**
     * Schema v1 -> v2: adds indexes on the verse table so per-sura loads and
     * favourite lookups use an index instead of a full table scan. Existing
     * installs keep their data; the indexes are created in place.
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_alquran_sura` ON `alquran` (`sura`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_alquran_liked` ON `alquran` (`liked`)");
        }
    };

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
                            .addMigrations(MIGRATION_1_2)
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
