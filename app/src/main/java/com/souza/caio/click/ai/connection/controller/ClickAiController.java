package com.souza.caio.click.ai.connection.controller;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ClickAiController {

    @Multipart
    @POST("image-upscaling/v1/upscale")
    Call<ResponseBody> upscaleImage(
            @Header("x-api-key") String apiKey,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part targetWidth,
            @Part MultipartBody.Part targetHeight
    );

    @Multipart
    @POST("reimagine/v1/reimagine")
    Call<ResponseBody> reimagineImage(
            @Header("x-api-key") String apiKey,
            @Part MultipartBody.Part imageFile
    );

    @Multipart
    @POST("remove-background/v1")
    Call<ResponseBody> removeBackgroundImage(
            @Header("x-api-key") String apiKey,
            @Part MultipartBody.Part imageFile
    );

    @Multipart
    @POST("remove-text/v1")
    Call<ResponseBody> removeTextImage(
            @Header("x-api-key") String apiKey,
            @Part MultipartBody.Part imageFile
    );

    @Multipart
    @POST("replace-background/v1")
    Call<ResponseBody> replaceBackground(
            @Header("x-api-key") String apiKey,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part prompt
    );

    @Multipart
    @POST("text-to-image/v1")
    Call<ResponseBody> textToImage(
            @Header("x-api-key") String apiKey,
            @Part MultipartBody.Part prompt
    );
}
