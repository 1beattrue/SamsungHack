package edu.mirea.onebeattrue.samsunghack.presentation.main.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import edu.mirea.onebeattrue.samsunghack.R

class YandexMapActivity : ComponentActivity() {

    private lateinit var mapView: MapView

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(
            this,
            "Tapped the point (${point.longitude}, ${point.latitude})",
            Toast.LENGTH_SHORT
        ).show()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_yandex_map)
        mapView = findViewById(R.id.mapview)

        mapView.mapWindow.map.move(
            CameraPosition(
                Point(55.751225, 37.629540),
                /* zoom = */ 17.0f,
                /* azimuth = */ 150.0f,
                /* tilt = */ 30.0f
            )
        )

        val imageProvider = ImageProvider.fromResource(this, R.drawable.placemark_icon)
        val placemark = mapView.mapWindow.map.mapObjects.addPlacemark().apply {
            geometry = Point(59.935493, 30.327392)
            setIcon(imageProvider)
        }

        placemark.addTapListener(placemarkTapListener)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, YandexMapActivity::class.java)
        }
    }
}