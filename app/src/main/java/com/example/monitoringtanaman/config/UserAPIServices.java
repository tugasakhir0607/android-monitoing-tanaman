package com.example.monitoringtanaman.config;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserAPIServices {

    @POST("api_login")
    Call<ResponseBody> api_login(@Body RequestBody file);

    @POST("api_signup")
    Call<ResponseBody> api_signup(@Body RequestBody file);

    @POST("api_getTanaman")
    Call<ResponseBody> getTanaman(@Body RequestBody file);

    @POST("api_getTanamanDetail")
    Call<ResponseBody> getTanamanDetail(@Body RequestBody file);

    @POST("api_inputTanaman")
    Call<ResponseBody> inputTanaman(@Body RequestBody file);

    @POST("api_updateTanaman")
    Call<ResponseBody> updateTanaman(@Body RequestBody file);

    @POST("api_getKelembaban")
    Call<ResponseBody> getKelembaban(@Body RequestBody file);

    @POST("api_getPenyiraman")
    Call<ResponseBody> getPenyiraman(@Body RequestBody file);

    @POST("api_getGrafikKelembapan")
    Call<ResponseBody> getGrafikKelembapan(@Body RequestBody file);

    @POST("api_updateProfil")
    Call<ResponseBody> updateProfil(@Body RequestBody file);

    @POST("api_updatePassword")
    Call<ResponseBody> updatePassword(@Body RequestBody file);

    @POST("api_updatePenyiraman")
    Call<ResponseBody> updatePenyiraman(@Body RequestBody file);

    @POST("api_inputGaleri")
    Call<ResponseBody> inputGaleri(@Body RequestBody file);

    @POST("api_getGaleri")
    Call<ResponseBody> getGaleri(@Body RequestBody file);

    @POST("api_deleteGaleri")
    Call<ResponseBody> deleteGaleri(@Body RequestBody file);

    @POST("api_getEvaluasi")
    Call<ResponseBody> getEvaluasi(@Body RequestBody file);
}
