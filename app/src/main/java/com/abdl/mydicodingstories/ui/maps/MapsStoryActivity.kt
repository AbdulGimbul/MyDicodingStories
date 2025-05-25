package com.abdl.mydicodingstories.ui.maps

import android.content.Context
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.abdl.mydicodingstories.R
import com.abdl.mydicodingstories.databinding.ActivityMapsStoryBinding
import com.abdl.mydicodingstories.ui.MainViewModel
import com.abdl.mydicodingstories.utils.SessionManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject // Import for field injection

@AndroidEntryPoint
class MapsStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsStoryBinding
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.fetchStories()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        storyMarker()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }

            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)) //
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: $e")
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun storyMarker() {
        mainViewModel.listStory.observe(this) { stories ->
            stories.forEach { story ->
                if (story.lat != null && story.lon != null) {
                    val latLng = LatLng(story.lat, story.lon)
                    getAddressName(story.lat, story.lon, this@MapsStoryActivity) { address ->
                        runOnUiThread {
                            mMap.addMarker(
                                MarkerOptions().position(latLng).title(story.name).snippet(address)
                            )
                        }
                    }
                    boundsBuilder.include(latLng)
                }
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun getAddressName(
        lat: Double,
        lon: Double,
        context: Context,
        callback: (String?) -> Unit
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isNotEmpty()) {
                        callback(addresses[0].getAddressLine(0))
                    } else {
                        callback(null)
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e(TAG, "Geocoding error (API 33+): $errorMessage")
                    callback(null)
                }
            })
        } else {
            try {
                @Suppress("DEPRECATION")
                val list: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.isNotEmpty()) {
                    callback(list[0].getAddressLine(0))
                } else {
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Geocoding error (pre-API 33): ${e.message}", e)
                callback(null)
            }
        }
    }

    companion object {
        const val TAG = "MapsStoryActivity"
    }
}