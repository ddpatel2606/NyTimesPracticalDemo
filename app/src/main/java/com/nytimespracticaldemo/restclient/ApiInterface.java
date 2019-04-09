package com.nytimespracticaldemo.restclient;

import com.nytimespracticaldemo.model.MostPopularModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("/svc/mostpopular/v2/mostviewed/all-sections/{id}.json")
    Call<MostPopularModel> getMostPopularNewsData(@Path("id") int groupId, @Query("api-key") String apikey);
}
