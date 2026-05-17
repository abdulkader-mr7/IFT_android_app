package com.tamilquran.ift.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookManifestResponse {

    @SerializedName("iftbooks")
    public List<BookManifestItem> iftBooks;

    @SerializedName("samarasam")
    public List<BookManifestItem> samarasam;
}
