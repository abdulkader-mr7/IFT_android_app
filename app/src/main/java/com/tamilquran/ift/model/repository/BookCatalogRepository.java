package com.tamilquran.ift.model.repository;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.tamilquran.ift.constants.ApiConstants;
import com.tamilquran.ift.model.api.BookApiService;
import com.tamilquran.ift.model.api.BookManifestItem;
import com.tamilquran.ift.model.api.BookManifestResponse;
import com.tamilquran.ift.model.database.AppDatabase;
import com.tamilquran.ift.model.entity.BookEntity;
import com.tamilquran.ift.model.entity.BookItem;
import com.tamilquran.ift.model.entity.SamarasamEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookCatalogRepository {

    public enum CatalogType {
        IFT_BOOKS,
        SAMARASAM
    }

    public interface DownloadProgressListener {
        void onProgress(int percent);

        void onComplete(File file);

        void onError(String message);
    }

    private final Context context;
    private final AppDatabase database;
    private final BookApiService apiService;
    private final File rootDir;

    public BookCatalogRepository(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(context);
        this.rootDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_BOOKS_URL)
                .client(client)
                .build();
        apiService = retrofit.create(BookApiService.class);
    }

    public List<BookItem> getBooks(CatalogType type) {
        List<BookItem> items = new ArrayList<>();
        if (type == CatalogType.IFT_BOOKS) {
            for (BookEntity entity : database.bookDao().getIftBooks()) {
                items.add(mapBook(entity.sno, entity.bookFilename, entity.bookTitle,
                        entity.category, entity.author, entity.size));
            }
        } else {
            for (SamarasamEntity entity : database.bookDao().getSamarasamBooks()) {
                items.add(mapBook(entity.sno, entity.bookFilename, entity.bookTitle,
                        entity.category, entity.author, entity.size));
            }
        }
        return items;
    }

    public int syncCatalog(CatalogType type) throws IOException {
        String baseUrl = type == CatalogType.IFT_BOOKS
                ? ApiConstants.BASE_BOOKS_URL
                : ApiConstants.BASE_SAMARASAM_URL;
        String manifestName = type == CatalogType.IFT_BOOKS
                ? ApiConstants.BOOKS_MANIFEST
                : ApiConstants.SAMARASAM_MANIFEST;

        File manifestFile = downloadToCache(baseUrl + manifestName, manifestName + ".tmp");
        String json = readFile(manifestFile);
        BookManifestResponse response = new Gson().fromJson(json, BookManifestResponse.class);
        List<BookManifestItem> items = type == CatalogType.IFT_BOOKS
                ? response.iftBooks : response.samarasam;

        int maxSno = type == CatalogType.IFT_BOOKS
                ? database.bookDao().getMaxIftBookSno()
                : database.bookDao().getMaxSamarasamSno();

        int inserted = 0;
        if (items != null) {
            for (BookManifestItem item : items) {
                int sno = Integer.parseInt(item.sno);
                if (sno > maxSno) {
                    if (type == CatalogType.IFT_BOOKS) {
                        BookEntity entity = new BookEntity();
                        entity.sno = sno;
                        entity.bookFilename = item.bookFilename;
                        entity.bookTitle = item.bookTitle;
                        entity.category = item.category;
                        entity.author = item.author;
                        entity.size = Integer.parseInt(item.size);
                        database.bookDao().insertIftBook(entity);
                    } else {
                        SamarasamEntity entity = new SamarasamEntity();
                        entity.sno = sno;
                        entity.bookFilename = item.bookFilename;
                        entity.bookTitle = item.bookTitle;
                        entity.category = item.category;
                        entity.author = item.author;
                        entity.size = Integer.parseInt(item.size);
                        database.bookDao().insertSamarasam(entity);
                    }
                    inserted++;
                }
            }
        }
        manifestFile.delete();
        return inserted;
    }

    public void downloadFile(
            CatalogType type,
            String filename,
            String extension,
            DownloadProgressListener listener
    ) {
        String baseUrl = type == CatalogType.IFT_BOOKS
                ? ApiConstants.BASE_BOOKS_URL
                : ApiConstants.BASE_SAMARASAM_URL;
        File dir = getStorageDir(type);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File target = new File(dir, filename + extension);
        try {
            Response<ResponseBody> response = apiService
                    .downloadStreaming(baseUrl + filename + extension)
                    .execute();
            if (!response.isSuccessful() || response.body() == null) {
                listener.onError("Download failed.");
                return;
            }
            writeWithProgress(response.body(), target, listener);
            listener.onComplete(target);
        } catch (IOException e) {
            listener.onError(e.getMessage() != null ? e.getMessage() : "Download failed.");
        }
    }

    public File getPdfFile(CatalogType type, String filename) {
        return new File(getStorageDir(type), filename + ".pdf");
    }

    public File getCoverFile(CatalogType type, String filename) {
        return new File(getStorageDir(type), filename + ".png");
    }

    private File getStorageDir(CatalogType type) {
        String sub = type == CatalogType.IFT_BOOKS ? ".ift/.books" : ".ift/.samarasam";
        File dir = new File(rootDir, sub);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public boolean deletePdf(CatalogType type, String filename) {
        File file = getPdfFile(type, filename);
        return !file.exists() || file.delete();
    }

    private File downloadToCache(String url, String name) throws IOException {
        File cache = new File(context.getCacheDir(), name);
        Response<ResponseBody> response = apiService.downloadStreaming(url).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Unable to download manifest.");
        }
        writeStream(response.body(), cache);
        return cache;
    }

    private void writeWithProgress(ResponseBody body, File target, DownloadProgressListener listener)
            throws IOException {
        long total = body.contentLength();
        try (InputStream input = body.byteStream();
             OutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            long downloaded = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
                downloaded += read;
                if (total > 0) {
                    listener.onProgress((int) ((downloaded * 100) / total));
                }
            }
        }
    }

    private void writeStream(ResponseBody body, File target) throws IOException {
        try (InputStream input = body.byteStream();
             OutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        }
    }

    private String readFile(File file) throws IOException {
        try (InputStream input = new java.io.FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            int read = input.read(bytes);
            return new String(bytes, 0, read, "UTF-8");
        }
    }

    private BookItem mapBook(int sno, String filename, String title, String cat, String author, int size) {
        return new BookItem(sno, filename, title, cat, author, size);
    }
}
