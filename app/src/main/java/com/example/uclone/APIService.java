package com.example.uclone;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                            "Authorization:key=AAAAEMfKiZM:APA91bH-V9kAzZCyMEfd20N5eIdwvBjak3zEQ8X6eBA_PXq0I71hDTrlLbBMe5Sq9g4K4cu-DyqHFdols3k1ozovJVrtcBATvJjGp8b-6YOUirKNfCjDdgrcdY3E54omED2uzclkUdNE"
            }
    )

    @POST("fcm/send")
    Call<MyResponce> sendNotification(@Body Sender body);

}
