package com.arifian.training.liburansemarang

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.view.Menu
import android.view.MenuItem
import com.arifian.training.liburansemarang.Utils.Constants.Companion.KEY_WISATA
import com.arifian.training.liburansemarang.Utils.GlideApp
import com.arifian.training.liburansemarang.Utils.PreferenceUtils
import com.arifian.training.liburansemarang.database.DBHelper
import com.arifian.training.liburansemarang.databinding.ActivityDetailWisataBinding
import com.arifian.training.liburansemarang.models.Wisata
import com.arifian.training.liburansemarang.models.remote.SimpleRetrofitCallback
import com.arifian.training.liburansemarang.models.remote.WisataClient
import com.arifian.training.liburansemarang.models.remote.responses.BaseResponse
import org.parceler.Parcels

class DetailWisataActivity : AppCompatActivity() {

    companion object {
        const val KEY_IMAGE = "image"
    }

    internal lateinit var mBinding: ActivityDetailWisataBinding
    internal lateinit var wisata: Wisata

    internal var favorite: Boolean = false
    lateinit var pref: PreferenceUtils

    internal lateinit var db: DBHelper

    lateinit var favoriteMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val transition = Slide()
            transition.excludeTarget(android.R.id.statusBarBackground, true)
            window.enterTransition = transition
            window.returnTransition = transition
        }

        wisata = Parcels.unwrap(intent.getParcelableExtra<Parcelable>(KEY_WISATA))

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_wisata)

            ViewCompat.setTransitionName(mBinding.toolbarLayout, KEY_IMAGE)
        supportPostponeEnterTransition()
        supportStartPostponedEnterTransition()

        pref = PreferenceUtils(this)
        db = DBHelper(this)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(wisata.namaWisata)

        mBinding.wisata = wisata

        favorite = pref.isFavorite(wisata.idWisata.toString())

        GlideApp.with(this)
                .load(WisataClient.IMAGE_URL + wisata.gambarWisata!!)
                .into(mBinding.ivDetailGambar)

        mBinding.fab.setOnClickListener { view ->
            favoriteClicked()
        }
    }

    private fun favoriteClicked(){
        val message: String
        if(!favorite) {
            wisata.favorite = (wisata.favorite!!.toInt() - 1).toString()
            var id = db.insert(wisata)
            if (id <= 0) {
                message = "Favorite gagal ditambahkan ke database"
            } else {
                message = "Favorite ditambahkan ke database"

                updateFavorite()
            }
        }else{
            val count = db.delete(wisata)
            if(count <= 0){
                message = "Favorite gagal dihapus dr database"
            }else{
                message = "Favorite dihapus dr database"

                updateFavorite()
            }
        }
        Snackbar.make(mBinding.fab, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun updateFavorite(){
        favorite = !favorite

        if(favorite){
            pref.addFavorite(wisata.idWisata.toString())
        }else{
            pref.removeFavorite(wisata.idWisata.toString())
        }

        checkFavorite()
        updateFavoriteRemote()
    }

    private fun updateFavoriteRemote(){
        WisataApplication.get(this)
                .wisataService!!
                .updateFavorite(wisata.idWisata.toString(), (if(favorite) 1 else 0))
                .enqueue(object: SimpleRetrofitCallback<BaseResponse>(this){
                    override fun onSuccess(response: BaseResponse) {

                    }
                })
    }

    private fun checkFavorite() {
        if (favorite) {
            mBinding.fab.setImageResource(R.drawable.ic_action_favorite_true)
            favoriteMenu.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_action_favorite_true, theme)
        }else {
            mBinding.fab.setImageResource(R.drawable.ic_action_favorite_false)
            favoriteMenu.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_action_favorite_false, theme)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        favoriteMenu = menu!!.findItem(R.id.action_favorite)
        checkFavorite()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.action_favorite -> {
                favoriteClicked()
            }
            R.id.action_direction -> {
                WisataApplication.get(this)
                        .wisataService!!
                        .updatePengunjung(wisata.idWisata.toString())
                        .enqueue(object: SimpleRetrofitCallback<BaseResponse>(this){
                            override fun onSuccess(response: BaseResponse) {

                            }
                        })
                val gmmIntentUri = Uri.parse("google.navigation:q="+wisata.latitudeWisata+","+wisata.longitudeWisata)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.`package` = "com.google.android.apps.maps"
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                }
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }
}
