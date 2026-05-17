package com.tamilquran.ift.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StatFs;

import com.tamilquran.ift.constants.DbConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class DatabaseBootstrap {

    public interface BootstrapCallback {
        void onSuccess();

        void onInsufficientStorage();

        void onError(String message);
    }

    private DatabaseBootstrap() {
    }

    public static boolean hasEnoughStorage(Context context) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        double availableMb = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong()) / 1048576.0;
        return availableMb >= DbConstants.MIN_DISK_MB;
    }

    public static void bootstrap(Context context, BootstrapCallback callback) {
        if (!hasEnoughStorage(context)) {
            callback.onInsufficientStorage();
            return;
        }

        File dbFile = context.getDatabasePath(DbConstants.DB_NAME);
        List<int[]> favorites = new ArrayList<>();

        try {
            if (dbFile.exists()) {
                Integer dbVersion = readDbVersion(dbFile);
                if (dbVersion != null && dbVersion != DbConstants.BUILD_VERSION) {
                    favorites = readFavorites(dbFile);
                    if (!dbFile.delete()) {
                        callback.onError("Unable to upgrade database.");
                        return;
                    }
                    AppDatabase.resetInstance();
                }
            }

            if (!dbFile.exists()) {
                unzipDatabase(context, dbFile);
            }

            if (!favorites.isEmpty()) {
                restoreFavorites(dbFile, favorites);
            }

            callback.onSuccess();
        } catch (IOException e) {
            callback.onError(e.getMessage() != null ? e.getMessage() : "Database setup failed.");
        }
    }

    private static Integer readDbVersion(File dbFile) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        try {
            android.database.Cursor cursor = db.rawQuery("SELECT version FROM db_info LIMIT 1", null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return null;
        } finally {
            db.close();
        }
    }

    private static List<int[]> readFavorites(File dbFile) {
        List<int[]> favorites = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        try {
            android.database.Cursor cursor = db.rawQuery(
                    "SELECT sura, ayah FROM alquran WHERE liked = 1", null);
            while (cursor.moveToNext()) {
                favorites.add(new int[]{cursor.getInt(0), cursor.getInt(1)});
            }
            cursor.close();
        } finally {
            db.close();
        }
        return favorites;
    }

    private static void restoreFavorites(File dbFile, List<int[]> favorites) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        try {
            for (int[] pair : favorites) {
                db.execSQL("UPDATE alquran SET liked = 1 WHERE sura = ? AND ayah = ?",
                        new Object[]{pair[0], pair[1]});
            }
        } finally {
            db.close();
        }
    }

    private static void unzipDatabase(Context context, File dbFile) throws IOException {
        File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create database directory.");
        }

        try (InputStream assetStream = context.getAssets().open(DbConstants.ZIP_ASSET_NAME);
             ZipInputStream zipInputStream = new ZipInputStream(assetStream)) {
            ZipEntry entry = zipInputStream.getNextEntry();
            if (entry == null) {
                throw new IOException("Invalid database archive.");
            }
            try (OutputStream outputStream = new FileOutputStream(dbFile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = zipInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, read);
                }
            }
            zipInputStream.closeEntry();
        }
    }
}
