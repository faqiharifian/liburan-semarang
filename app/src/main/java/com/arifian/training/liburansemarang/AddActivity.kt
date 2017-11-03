package com.arifian.training.liburansemarang

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.arifian.training.liburansemarang.Utils.FileUtils
import com.arifian.training.liburansemarang.Utils.GlideApp
import com.arifian.training.liburansemarang.databinding.ActivityAddBinding
import com.arifian.training.liburansemarang.models.remote.SimpleRetrofitCallback
import com.arifian.training.liburansemarang.models.remote.responses.BaseResponse
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_IMAGE = 1
        const val REQUEST_PERMISSION = 2
        const val REQUEST_PLACE = 3

        fun newInstance(): CreateFragment {

            val args = Bundle()

            val fragment = CreateFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var mBinding: ActivityAddBinding
    var uri: Uri? = null
    lateinit var place: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.imageView.setOnClickListener {
            openGallery()
        }

        mBinding.btnMaps.setOnClickListener {
            val intent = PlacePicker.IntentBuilder()
            startActivityForResult(intent.build(this), REQUEST_PLACE)
        }

        mBinding.btnSubmit.setOnClickListener{
            submit()
        }

        requestPermission()

        GlideApp.with(this)
                .load(R.drawable.add_photo)
                .into(mBinding.imageView)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("result", requestCode.toString()+" - "+resultCode.toString())
        if(resultCode == Activity.RESULT_OK && data != null && requestCode == REQUEST_IMAGE){
            uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
            mBinding.imageView.setImageBitmap(bitmap)
        }else if(resultCode == Activity.RESULT_OK && data != null && requestCode == REQUEST_PLACE){
            place = PlacePicker.getPlace(this, data)
            val alamat = place.address
            mBinding.statusMaps.text = alamat!!
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_PERMISSION)
    private fun requestPermission() {
        val perms = Manifest.permission.READ_EXTERNAL_STORAGE
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Butuh lokasi",
                    REQUEST_PERMISSION, perms)
        }
    }

    private fun check(et: EditText): Boolean{
        if(TextUtils.isEmpty(et.text)){
            et.error = "Harus diisi"
            return false
        }
        return true
    }

    private fun submit() {
        val progress = ProgressDialog(this)
        progress.isIndeterminate = true
        progress.setMessage("Loading")
        progress.setCancelable(false)
        progress.show()

        if(check(mBinding.edtNama)
                && check(mBinding.edtDeskripsi)
                && check(mBinding.edtAlamat)
                && check(mBinding.edtEvent)){
            if(uri == null){
                Toast.makeText(this, "Gambar harus ada", Toast.LENGTH_SHORT).show()
            }else{
//                val file = RealPathUtils(this).getFile(uri!!)
                val file = File(FileUtils.getPath(this, uri))

                var namaGambar = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                namaGambar += "."+file.extension
                val sFile = RequestBody.create(MediaType.parse("image/*"), file)

                val fileToUpload = MultipartBody.Part.createFormData("file", namaGambar, sFile)

                WisataApplication.get(this)
                        .getService(this)
                        .wisataPost(
                                fileToUpload,
                                RequestBody.create(MediaType.parse("text/plain"), mBinding.edtNama.text.toString()),
                                RequestBody.create(MediaType.parse("text/plain"), namaGambar),
                                RequestBody.create(MediaType.parse("text/plain"), mBinding.edtDeskripsi.text.toString()),
                                RequestBody.create(MediaType.parse("text/plain"), mBinding.edtEvent.text.toString()),
                                RequestBody.create(MediaType.parse("text/plain"), place.latLng.latitude.toString()),
                                RequestBody.create(MediaType.parse("text/plain"), place.latLng.longitude.toString()),
                                RequestBody.create(MediaType.parse("text/plain"), mBinding.edtAlamat.text.toString())

                        )
                        .enqueue(object : SimpleRetrofitCallback<BaseResponse>(this, progress) {
                            override fun onSuccess(response: BaseResponse) {
                                Toast.makeText(this@AddActivity, response.message, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        })
            }
        }
    }
}
