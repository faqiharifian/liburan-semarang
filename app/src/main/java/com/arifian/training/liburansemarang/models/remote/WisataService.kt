package com.arifian.training.liburansemarang.models.remote

import com.arifian.training.liburansemarang.models.remote.responses.BaseResponse
import com.arifian.training.liburansemarang.models.remote.responses.WisataResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by faqih on 30/10/17.
 */

interface WisataService {
    @GET("read_wisata.php")
    fun wisata(@Query("sort") sort: String): Call<WisataResponse>

    @Multipart
    @POST("create_wisata.php")
    fun wisataPost(@Part file: MultipartBody.Part,
                   @Part("nama_wisata") nama_wisata: RequestBody,
                   @Part("gambar_wisata") gambar_wisata: RequestBody,
                   @Part("deksripsi_wisata") deksripsi_wisata: RequestBody,
                   @Part("event_wisata") event_wisata: RequestBody,
                   @Part("latitude_wisata") latitude_wisata: RequestBody,
                   @Part("longitude_wisata") longitude_wisata: RequestBody,
                   @Part("alamat_wisata") alamat_wisata: RequestBody): Call<BaseResponse>
}