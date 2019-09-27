package com.mrtecks.grocery;

import com.mrtecks.grocery.homePOJO.homeBean;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AllApiIneterface {

    /*@Multipart
    @POST("grocery/api/login.php")
    Call<verifyBean> login(
            @Part("phone") String client,
            @Part("token") String token
    );
*/


    @GET("grocery/api/getHome.php")
    Call<homeBean> getHome();



}
