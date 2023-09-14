package com.souza.caio.click.ai.connection;

import com.souza.caio.click.ai.connection.controller.ClickAiController;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClickAiClient {
    private static final String BASE_URL = "https://clipdrop-api.co/";
    private static ClickAiClient apiClient;
    private static Retrofit retrofit;

    private ClickAiClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Increase the connection timeout (default is 10 seconds)
        httpClient.connectTimeout(2, TimeUnit.MINUTES);

        // Increase the read timeout (default is 10 seconds)
        httpClient.readTimeout(2, TimeUnit.MINUTES);
        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder().client(client).baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized ClickAiClient getInstance() {
        if (apiClient == null) {
            apiClient = new ClickAiClient();
        }
        return apiClient;
    }

    public ClickAiController getApi() {
        return retrofit.create(ClickAiController.class);
    }
}
