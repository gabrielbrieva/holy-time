package com.tuxan.holytime.api;

import com.tuxan.holytime.Utils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class APIServiceFactory {

    public static APIService createService(final String apiKey) {

        OkHttpClient okHttpClient = new OkHttpClient();

        // logging for http requests
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient.interceptors().add(httpLoggingInterceptor);

        // add apikey query parameter to all API requests
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                HttpUrl url = request.url().newBuilder()
                        .addQueryParameter("apikey", apiKey).build();
                request = request.newBuilder().url(url).build();

                request = request.newBuilder().header("Accept", "application/json").build();

                return chain.proceed(request);
            }
        });

        return new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .client(okHttpClient)
                .build()
                .create(APIService.class);
    }


}
