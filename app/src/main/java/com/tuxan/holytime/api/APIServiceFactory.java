package com.tuxan.holytime.api;

import com.tuxan.holytime.utils.Utils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIServiceFactory {

    public static APIService createService(final String apiKey) {

        // logging for http requests
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        HttpUrl url = request.url().newBuilder().addQueryParameter("apikey", apiKey).build();
                        request = request.newBuilder().url(url).build();
                        request = request.newBuilder().header("Accept", "application/json").build();

                        return chain.proceed(request);
                    }
                }).build();

        return new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService.class);
    }


}
