package com.toharifqi.instageram.storymap

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.toharifqi.instageram.BaseApplication
import com.toharifqi.instageram.R
import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.core.ResultLoad.Error
import com.toharifqi.instageram.core.ResultLoad.Success
import com.toharifqi.instageram.databinding.ActivityStoryMapBinding
import com.toharifqi.instageram.storylist.StoryDomainData
import javax.inject.Inject

class StoryMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<StoryMapViewModel>
    private val viewModel: StoryMapViewModel by viewModels { viewModelFactory }

    private var userToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.title_activity_story_map)
        }

        userToken = intent.getStringExtra(TOKEN_EXTRA) ?: ""
        observeViewModel()
    }

    private fun observeViewModel() {
        with(viewModel) {
            stories.observe(this@StoryMapActivity) {
                when (it) {
                    is Success -> {
                        setStoryMarkers(it.data)
                    }
                    is Error   -> {
                        Toast.makeText(this@StoryMapActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMapToolbarEnabled = true
        }
        setMapStyle()
        viewModel.loadAllStoriesWithLocation(userToken)
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun setStoryMarkers(stories: List<StoryDomainData>?) {
        val sortedStoryBySameLocation = stories?.sortedWith(compareBy({ it.lat }, { it.lng }))
        var tempLatLng = LatLng(0.0, 0.0)
        sortedStoryBySameLocation?.forEachIndexed { index, story ->
            val locationOffset = index * COORDINATE_OFFSET
            if (story.lat != null && story.lng != null) {
                var latLng = LatLng(story.lat, story.lng)

                if (tempLatLng == latLng) {
                    latLng = LatLng(story.lat + locationOffset, story.lng + locationOffset)
                } else tempLatLng = latLng

                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
            }
        }

        val indonesiaCoordinate = LatLng(0.7893, 113.9213)
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(indonesiaCoordinate, -15f)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type    -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type   -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type    -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else                -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val TOKEN_EXTRA = "auth_token_extra"
        const val TAG = "StoryMapActivity"
        const val COORDINATE_OFFSET = 0.00002f
    }
}
