package com.tamilquran.ift.model.entity;

public class BookItem {

    public final int sno;
    public final String bookFilename;
    public final String bookTitle;
    public final String category;
    public final String author;
    public final int sizeBytes;

    public BookItem(int sno, String bookFilename, String bookTitle, String category, String author, int sizeBytes) {
        this.sno = sno;
        this.bookFilename = bookFilename;
        this.bookTitle = bookTitle;
        this.category = category;
        this.author = author;
        this.sizeBytes = sizeBytes;
    }
}
