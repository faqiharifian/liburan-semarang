package com.arifian.training.liburansemarang.models.remote.responses

import com.arifian.training.liburansemarang.models.Wisata
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by faqih on 30/10/17.
 */

class WisataResponse : BaseResponse() {
    @SerializedName("wisata")
    @Expose
    var wisata: List<Wisata>? = null

}
