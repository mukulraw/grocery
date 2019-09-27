package com.mrtecks.grocery;

import com.mrtecks.grocery.homePOJO.homeBean;
import com.mrtecks.grocery.productsPOJO.productsBean;
import com.mrtecks.grocery.seingleProductPOJO.singleProductBean;
import com.mrtecks.grocery.subCat1POJO.subCat1Bean;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AllApiIneterface {



    @GET("grocery/api/getHome.php")
    Call<homeBean> getHome();

    @Multipart
    @POST("grocery/api/getSubCat1.php")
    Call<subCat1Bean> getSubCat1(
            @Part("cat") String cat
    );

    @Multipart
    @POST("grocery/api/getSubCat2.php")
    Call<subCat1Bean> getSubCat2(
            @Part("subcat1") String cat
    );

    @Multipart
    @POST("grocery/api/getProducts.php")
    Call<productsBean> getProducts(
            @Part("subcat2") String cat
    );

    @Multipart
    @POST("grocery/api/getProductById.php")
    Call<singleProductBean> getProductById(
            @Part("id") String cat
    );
}
