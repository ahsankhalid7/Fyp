package com.example.uclone;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sajid on 1/26/2019.
 */

public class Client {
    private static Retrofit retrofit=null;
    public static  Retrofit getClient(String url)
    {
        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
