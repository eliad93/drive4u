package com.example.eliad.drive4u.Notifications;

import com.example.eliad.drive4u.Notifications.MyResponse;
import com.example.eliad.drive4u.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAARcbg40E:APA91bFp4GBY2qwd5wb9Mxv5nmlDsqHXkNH2j3awTzkVYhSDbi5RvglIDH2u0WtPjop3awN-uUyDRPqnKBKbZ_niVQ45-UrxAkGNC8hwECu8Ee6cFk1v-DiXzHx-vccDNALwblnkxTM2"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
