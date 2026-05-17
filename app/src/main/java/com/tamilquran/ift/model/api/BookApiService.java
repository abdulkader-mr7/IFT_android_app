package com.tamilquran.ift.model.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface BookApiService {

    @GET
    Call<ResponseBody> downloadFile(@Url String url);

    @Streaming
    @GET
    Call<ResponseBody> downloadStreaming(@Url String url);
}
