package com.nytimespracticaldemo.restclient;

import com.nytimespracticaldemo.model.MostPopularModel;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiInterface {


    @GET("/svc/mostpopular/v2/mostviewed/all-sections/{id}.json")
    Observable<MostPopularModel> getMostPopularNewsData(@Path("id") int groupId, @Query("api-key") String apikey);


}
