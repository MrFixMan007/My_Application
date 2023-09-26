package com.example.myapplication.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductApi {
    @GET("products/1")
    Call<Product> getProductById();
}
