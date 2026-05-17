package com.tamilquran.ift.model.api;

import com.google.gson.annotations.SerializedName;

public class BookManifestItem {

    @SerializedName("sno")
    public String sno;

    @SerializedName("bookfilename")
    public String bookFilename;

    @SerializedName("booktitle")
    public String bookTitle;

    @SerializedName("cat")
    public String category;

    @SerializedName("author")
    public String author;

    @SerializedName("size")
    public String size;
}
