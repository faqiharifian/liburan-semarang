package com.arifian.training.liburansemarang.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.parceler.Parcel

/**
 * Created by faqih on 30/10/17.
 */

@Parcel
class Wisata(): Item(com.arifian.training.liburansemarang.models.Item.Companion.TYPE_ITEM) {

    constructor(idWisata: String?, namaWisata: String?, gambarWisata: String?, deksripsiWisata: String?, alamatWisata: String?, latitudeWisata: String?, longitudeWisata: String?, pengunjung: String?, favorite: String?): this() {
        this.idWisata = idWisata
        this.namaWisata = namaWisata
        this.gambarWisata = gambarWisata
        this.deksripsiWisata = deksripsiWisata
        this.alamatWisata = alamatWisata
        this.latitudeWisata = latitudeWisata
        this.longitudeWisata = longitudeWisata
        this.favorite = favorite
        this.pengunjung = pengunjung
    }

    @SerializedName("id_wisata")
    @Expose
    var idWisata: String? = null
    @SerializedName("nama_wisata")
    @Expose
    var namaWisata: String? = null
    @SerializedName("gambar_wisata")
    @Expose
    var gambarWisata: String? = null
    @SerializedName("deksripsi_wisata")
    @Expose
    var deksripsiWisata: String? = null
    @SerializedName("alamat_wisata")
    @Expose
    var alamatWisata: String? = null
    @SerializedName("event_wisata")
    @Expose
    var eventWisata: String? = null
    @SerializedName("latitude_wisata")
    @Expose
    var latitudeWisata: String? = null
    @SerializedName("longitude_wisata")
    @Expose
    var longitudeWisata: String? = null
    @SerializedName("favorite")
    @Expose
    var favorite: String? = "-1"
    @SerializedName("pengunjung")
    @Expose
    var pengunjung: String? = "-1"
}
