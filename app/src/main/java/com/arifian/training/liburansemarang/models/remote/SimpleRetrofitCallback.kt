package com.arifian.training.liburansemarang.models.remote

import android.app.Activity
import android.app.ProgressDialog
import android.util.Log
import android.widget.Toast
import com.arifian.training.liburansemarang.models.remote.responses.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

/**
 * Created by faqih on 29/09/17.
 */

abstract class SimpleRetrofitCallback<T : BaseResponse>(internal var activity: Activity) : Callback<T> {
    var progress: ProgressDialog? = null

    constructor(activity: Activity, progress: ProgressDialog) : this(activity) {
        this.progress = progress
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if(progress != null){
            progress!!.dismiss()
        }

        if (response.code() == 200) {

            if (response.isSuccessful) {
                val baseResponse = response.body()
                if (baseResponse != null) {
                    if (!baseResponse.success!!) {
                        Toast.makeText(activity, baseResponse.message, Toast.LENGTH_SHORT).show()
                    } else {
                        onSuccess(baseResponse)
                    }
                }
            } else {
                Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        if(progress != null){
            progress!!.dismiss()
        }
        if (!call.isCanceled) {
            if (t is SocketTimeoutException) {
                Log.e("fail call", "timeout")
                Toast.makeText(activity, "timeout", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("fail call", t.message)
                Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    abstract fun onSuccess(response: T)
}
