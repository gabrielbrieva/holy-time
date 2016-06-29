package com.tuxan.holytime.api;

import com.tuxan.holytime.data.dto.MeditationContent;
import com.tuxan.holytime.data.dto.Page;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @GET("sync/{weekNumber}")
    Call<List<MeditationContent>> getSyncList(@Path("weekNumber") int weekNumber);

    @GET("meditations/{weekNumber}")
    Call<Page<MeditationContent>> getPaginatedContent(@Path("weekNumber") int weekNumber,
                                                      @Query("page") int page);

    @GET("meditation/{id}")
    Call<MeditationContent> getContentDetail(@Path("id") String id);

}
