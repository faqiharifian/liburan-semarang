package com.arifian.training.liburansemarang


import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.arifian.training.liburansemarang.Utils.PreferenceUtils.Companion.SORT_LATEST
import com.arifian.training.liburansemarang.models.Wisata
import com.arifian.training.liburansemarang.models.remote.SimpleRetrofitCallback
import com.arifian.training.liburansemarang.models.remote.responses.BaseResponse
import com.arifian.training.liburansemarang.models.remote.responses.WisataResponse
import com.arifian.training.liburansemarang.models.remote.responses.route.Response
import com.arifian.training.liburansemarang.models.remote.responses.route.StepsItem
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback


/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, com.google.android.gms.location.LocationListener{

    var mMap: GoogleMap? = null
    var wisataArrayList: List<Wisata>? = null

    var current: Marker? = null
    var destinationMarker: Marker? = null
    var destination: LatLng? = null
    var origin:LatLng? = null
    var route:ArrayList<LatLng> = ArrayList()

    protected var mGoogleApiClient: GoogleApiClient? = null
    protected var locationRequest: LocationRequest? = null
    var REQUEST_CHECK_SETTINGS = 100

    var markerMap: HashMap<Marker, Wisata> = HashMap()

    companion object {
        const val REQUEST_LOCATION = 100

        fun newInstance(): MapFragment {

            val args = Bundle()

            val fragment = MapFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mGoogleApiClient = GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
        mGoogleApiClient!!.connect()

        locationRequest = LocationRequest.create()
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest!!.setInterval(30 * 1000)
        locationRequest!!.setFastestInterval(5 * 1000)

        getWisata()

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.e("resume", "resume")

//        gpsTracker = GPSTracker(activity)
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!

        updateMarker()

        mMap!!.setOnMarkerClickListener { marker: Marker? ->
            //            val origin = LatLng(gpsTracker!!.latitude, gpsTracker!!.longitude)
            destination = marker!!.position
            destinationMarker = marker
            marker.showInfoWindow()

            requestRoute()

            val wisata = markerMap.get(marker)
            Log.e("id", wisata!!.idWisata)

            WisataApplication.get(activity)
                    .wisataService!!
                    .updatePengunjung(wisata!!.idWisata!!)
                    .enqueue(object: SimpleRetrofitCallback<BaseResponse>(activity){
                        override fun onSuccess(response: BaseResponse) {

                        }
                    })
            true
        }
    }

    private fun requestRoute() {
        mMap!!.clear()
        updateMarker()

        if(origin != null)
            WisataApplication.get(activity)
                    .wisataService!!
                    .getRoute(origin!!.latitude.toString()+","+origin!!.longitude.toString(), destination!!.latitude.toString()+","+destination!!.longitude.toString(), getString(R.string.google_maps_key))
                    .enqueue(object: Callback<Response>{
                        override fun onFailure(call: Call<Response>?, t: Throwable?) {
                            Log.e("error", t!!.message)
                        }

                        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
                            if(response!!.isSuccessful){
                                var option = PolylineOptions()
                                option.color(ResourcesCompat.getColor(resources, R.color.colorRouteLine, activity.theme))
                                option.width(6f)

                                for(step: StepsItem? in response.body()!!.routes!![0]!!.legs!![0]!!.steps!!){
                                    option.points.add(LatLng(step!!.endLocation!!.lat!!, step!!.endLocation!!.lng!!))
                                }
                                activity.runOnUiThread{
                                    if(origin != null) {
                                        var duration = response!!.body()!!.routes!![0]!!.legs!![0]!!.duration!!.text
                                        var distance = response!!.body()!!.routes!![0]!!.legs!![0]!!.distance!!.text
                                        mMap!!.addPolyline(option)
                                        current = mMap!!.addMarker(MarkerOptions()
                                                .position(origin!!)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_a))
                                                .title("origin"))
                                        destinationMarker!!.snippet = duration+" "+distance
                                        val bounds = LatLngBounds.Builder()
                                                .include(origin)
                                                .include(destination).build()
                                        val displaySize = Point()
                                        activity.windowManager.defaultDisplay.getSize(displaySize)
                                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, displaySize.y, 100))
                                    }
                                }
                            }
                        }
                    })
    }

    private fun getWisata() {
        val progressBar = ProgressBar(activity, null, android.R.attr.progressBarStyleSmall)

        progressBar.visibility = View.VISIBLE
        WisataApplication.get(activity)
                .getService(activity)
                .wisata(SORT_LATEST)
                .enqueue(object : SimpleRetrofitCallback<WisataResponse>(activity) {
                    override fun onSuccess(response: WisataResponse) {
                        wisataArrayList = response.wisata!!
                        updateMarker()
                    }
                })
    }

    private fun updateMarker(){
        if(mMap != null && wisataArrayList != null){
            val bounds = LatLngBounds.Builder()
            for(wisata: Wisata in wisataArrayList!!){
                val marker = LatLng(wisata.latitudeWisata!!.toDouble(), wisata.longitudeWisata!!.toDouble())
                var markerM = mMap!!.addMarker(MarkerOptions().position(marker))
                markerM.tag = wisata.idWisata
                markerMap.put(markerM, wisata)
                bounds.include(marker)
            }
            if(current == null)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 13))

            requestPermission()
        }
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_LOCATION)
    private fun requestPermission() {
        val perms = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(activity, "Butuh lokasi",
                    REQUEST_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    override fun onConnected(p0: Bundle?) {
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)
        builder.setAlwaysShow(true)
        val result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                )

        result.setResultCallback(this)
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onResult(p0: LocationSettingsResult) {
        val status = p0.getStatus();
        when (status.getStatusCode()) {
            LocationSettingsStatusCodes.SUCCESS ->{
                if(Build.VERSION.SDK_INT >= 23) {
                    if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this)
//                        gpsTracker = GPSTracker(activity)
//                        origin = LatLng(gpsTracker.latitude, gpsTracker.longitude)
                    }
                }else{
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this)
//                    gpsTracker = GPSTracker(activity)
//                    origin = LatLng(gpsTracker.latitude, gpsTracker.longitude)
                }
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)

                } catch (e: IntentSender.SendIntentException) {

                    //failed to show
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
            }
        }
    }
    override fun onLocationChanged(p0: Location?) {
        origin = LatLng(p0!!.latitude, p0!!.longitude)
        if(current != null){
            current!!.position = origin
            requestRoute()
        }
    }
}
