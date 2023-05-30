package hr.algebra.MapMyPath.ui.home.map
import Place
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.PolyUtil
import hr.algebra.MapMyPath.R
import hr.algebra.MapMyPath.databinding.FragmentMapsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.platform.android.AndroidLogHandler.close
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resumeWithException


@Suppress("DEPRECATION")
class MapsFragment : Fragment() {


    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()
    private lateinit var currentUserLocation : LatLng
    private lateinit var currentPoint : LatLng
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var isOverlayed = false
    private var polyline: Polyline? = null
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavView: NavigationView
    private var currentTypeSelection= "museum"
    private var radius = 15000





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            googleMap = map
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            enableMyLocation()
        }

        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = view.findViewById<NavigationView>(R.id.nav_view)

        val navigationViewRight = view.findViewById<NavigationView>(R.id.nav_view_right)

        navigationViewRight.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_overlay -> {
                    isOverlayed=!isOverlayed
                    Toast.makeText(requireContext(), "Overlay setting:${isOverlayed}", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_save_route -> {
                    // trenutno ne radi
                    // ovdje kao sejvati trenutni point rute preko currentUserLocation
                    Toast.makeText(requireContext(), "Current location point saved:${currentUserLocation.latitude},${currentUserLocation.longitude}", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.icon_1 -> {
                    if (!isOverlayed) {
                        googleMap.clear()
                    }
                    currentTypeSelection="museum"
                    Log.d(TAG, "Starting CoroutineScope")
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Log.d(TAG, "Before getNearbyPlaces request")
                                val response = getNearbyPlaces(currentTypeSelection,currentUserLocation,radius.toString())
                                Log.d(TAG, "After getNearbyPlaces request")
                                Log.d(TAG, "Response code: ${response.code}, message: ${response.message}")
                                if (response.isSuccessful) {
                                    val places = parseResponse(response.body?.string())
                                    places.forEach { place ->
                                        val directionsResponse = getDirections(currentUserLocation, place.location)
                                        Log.d(TAG, "Response code: ${directionsResponse.code}, message: ${directionsResponse.message}")
                                        if (directionsResponse.isSuccessful) {
                                            val routePoints = parseDirectionsResponse(directionsResponse.body?.string()) // You'll need to implement parseDirectionsResponse
                                            withContext(Dispatchers.Main) {
                                                val polylineOptions = PolylineOptions()
                                                    .addAll(routePoints)
                                                    .width(7f)
                                                    .color(Color.RED)
                                                currentPoint= LatLng(place.location.latitude,place.location.longitude)
                                                googleMap.addMarker(MarkerOptions().position(currentPoint).title("Marker"))
                                                polylineOptions.add(currentPoint)
                                                googleMap.addPolyline(polylineOptions)

                                            }


                                        } else {
                                            Log.e(Companion.TAG, "Failed to get directions: ${directionsResponse.message}")
                                        }
                                    }
                                } else {
                                    Log.e(Companion.TAG, "Failed to get nearby museums: ${response.message}")
                                }
                            } catch (e: Exception) {
                                Log.e(Companion.TAG, "Error: ", e)
                                e.printStackTrace()
                            }
                        }
                }
                R.id.icon_2 -> {
                    if (!isOverlayed) {
                        googleMap.clear()
                    }
                    currentTypeSelection="restaurant"
                    Log.d(TAG, "Starting CoroutineScope")


                    // Get museums nearby
                    CoroutineScope(Dispatchers.IO).launch {
                        try {

                            Log.d(TAG, "Before getNearbyPlaces request")



                            val response = getNearbyPlaces(currentTypeSelection,currentUserLocation,radius.toString())
                            Log.d(TAG, "After getNearbyPlaces request")
                            Log.d(TAG, "Response code: ${response.code}, message: ${response.message}")
                            if (response.isSuccessful) {
                                val places = parseResponse(response.body?.string()) // You'll need to implement parseResponse
                                // Get directions to each museum



                                places.forEach { place ->
                                    val directionsResponse = getDirections(currentUserLocation, place.location)
                                    Log.d(TAG, "Response code: ${directionsResponse.code}, message: ${directionsResponse.message}")
                                    if (directionsResponse.isSuccessful) {

                                        val routePoints = parseDirectionsResponse(directionsResponse.body?.string()) // You'll need to implement parseDirectionsResponse

                                        // Draw polyline on the map
                                        withContext(Dispatchers.Main) {
                                            val polylineOptions = PolylineOptions()
                                                .addAll(routePoints)
                                                .width(7f)
                                                .color(Color.BLUE)
                                            currentPoint= LatLng(place.location.latitude,place.location.longitude)
                                            googleMap.addMarker(MarkerOptions().position(currentPoint).title("Marker"))
                                            polylineOptions.add(currentPoint)
                                            googleMap.addPolyline(polylineOptions)

                                        }
                                    } else {
                                        Log.e(Companion.TAG, "Failed to get directions: ${directionsResponse.message}")
                                    }
                                }
                            } else {
                                Log.e(Companion.TAG, "Failed to get nearby museums: ${response.message}")
                            }
                        } catch (e: Exception) {
                            Log.e(Companion.TAG, "Error: ", e)
                            e.printStackTrace()
                        }
                    }
                }
                R.id.icon_3 -> {
                    if (!isOverlayed) {
                        googleMap.clear()
                    }
                    currentTypeSelection="atm"
                    Log.d(TAG, "Starting CoroutineScope")


                    // Get museums nearby
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d(TAG, "Before getNearbyPlaces request")



                            val response = getNearbyPlaces("atm",currentUserLocation,radius.toString())
                            Log.d(TAG, "After getNearbyPlaces request")
                            Log.d(TAG, "Response code: ${response.code}, message: ${response.message}")
                            if (response.isSuccessful) {
                                val places = parseResponse(response.body?.string()) // You'll need to implement parseResponse
                                // Get directions to each museum



                                places.forEach { place ->
                                    val directionsResponse = getDirections(currentUserLocation, place.location)
                                    Log.d(TAG, "Response code: ${directionsResponse.code}, message: ${directionsResponse.message}")
                                    if (directionsResponse.isSuccessful) {
                                        val routePoints = parseDirectionsResponse(directionsResponse.body?.string()) // You'll need to implement parseDirectionsResponse

                                        // Draw polyline on the map
                                        withContext(Dispatchers.Main) {
                                            val polylineOptions = PolylineOptions()
                                                .addAll(routePoints)
                                                .width(7f)
                                                .color(Color.GREEN)

                                            currentPoint= LatLng(place.location.latitude,place.location.longitude)
                                            googleMap.addMarker(MarkerOptions().position(currentPoint).title("Marker"))
                                            polylineOptions.add(currentPoint)
                                            googleMap.addPolyline(polylineOptions)
                                        }
                                    } else {
                                        Log.e(Companion.TAG, "Failed to get directions: ${directionsResponse.message}")
                                    }
                                }
                            } else {
                                Log.e(Companion.TAG, "Failed to get nearby museums: ${response.message}")
                            }
                        } catch (e: Exception) {
                            Log.e(Companion.TAG, "Error: ", e)
                            e.printStackTrace()
                        }
                    }
                }
                R.id.icon_4 -> {
                    if (!isOverlayed) {
                        googleMap.clear()
                    }
                    currentTypeSelection="movie_theater"
                    Log.d(TAG, "Starting CoroutineScope")


                    // Get museums nearby
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d(TAG, "Before getNearbyPlaces request")



                            val response = getNearbyPlaces("movie_theater",currentUserLocation,radius.toString())
                            Log.d(TAG, "After getNearbyPlaces request")
                            Log.d(TAG, "Response code: ${response.code}, message: ${response.message}")
                            if (response.isSuccessful) {
                                val places = parseResponse(response.body?.string()) // You'll need to implement parseResponse
                                // Get directions to each museum



                                places.forEach { place ->
                                    val directionsResponse = getDirections(currentUserLocation, place.location)
                                    Log.d(TAG, "Response code: ${directionsResponse.code}, message: ${directionsResponse.message}")
                                    if (directionsResponse.isSuccessful) {
                                        val routePoints = parseDirectionsResponse(directionsResponse.body?.string()) // You'll need to implement parseDirectionsResponse

                                        // Draw polyline on the map
                                        withContext(Dispatchers.Main) {
                                            val polylineOptions = PolylineOptions()
                                                .addAll(routePoints)
                                                .width(7f)
                                                .color(Color.YELLOW)

                                            currentPoint= LatLng(place.location.latitude,place.location.longitude)
                                            googleMap.addMarker(MarkerOptions().position(currentPoint).title("Marker"))
                                            polylineOptions.add(currentPoint)
                                            googleMap.addPolyline(polylineOptions)
                                        }
                                    } else {
                                        Log.e(Companion.TAG, "Failed to get directions: ${directionsResponse.message}")
                                    }
                                }
                            } else {
                                Log.e(Companion.TAG, "Failed to get nearby museums: ${response.message}")
                            }
                        } catch (e: Exception) {
                            Log.e(Companion.TAG, "Error: ", e)
                            e.printStackTrace()
                        }
                    }
                }
                R.id.icon_5 -> {
                    if (!isOverlayed) {
                        googleMap.clear()
                    }
                    currentTypeSelection="park"
                    Log.d(TAG, "Starting CoroutineScope")


                    // Get museums nearby
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d(TAG, "Before getNearbyPlaces request")



                            val response = getNearbyPlaces("park",currentUserLocation,radius.toString())
                            Log.d(TAG, "After getNearbyPlaces request")
                            Log.d(TAG, "Response code: ${response.code}, message: ${response.message}")
                            if (response.isSuccessful) {
                                val places = parseResponse(response.body?.string()) // You'll need to implement parseResponse
                                // Get directions to each museum



                                places.forEach { place ->
                                    val directionsResponse = getDirections(currentUserLocation, place.location)
                                    Log.d(TAG, "Response code: ${directionsResponse.code}, message: ${directionsResponse.message}")
                                    if (directionsResponse.isSuccessful) {
                                        val routePoints = parseDirectionsResponse(directionsResponse.body?.string()) // You'll need to implement parseDirectionsResponse

                                        // Draw polyline on the map
                                        withContext(Dispatchers.Main) {
                                            val polylineOptions = PolylineOptions()
                                                .addAll(routePoints)
                                                .width(7f)
                                                .color(Color.BLACK)

                                            currentPoint= LatLng(place.location.latitude,place.location.longitude)
                                            googleMap.addMarker(MarkerOptions().position(currentPoint).title("Marker"))
                                            polylineOptions.add(currentPoint)
                                            googleMap.addPolyline(polylineOptions)
                                        }
                                    } else {
                                        Log.e(Companion.TAG, "Failed to get directions: ${directionsResponse.message}")
                                    }
                                }
                            } else {
                                Log.e(Companion.TAG, "Failed to get nearby museums: ${response.message}")
                            }
                        } catch (e: Exception) {
                            Log.e(Companion.TAG, "Error: ", e)
                            e.printStackTrace()
                        }
                    }
                }
                R.id.icon_6 -> {
                    if (!isOverlayed) {
                        googleMap.clear()
                    }
                    currentTypeSelection="doctor"
                    Log.d(TAG, "Starting CoroutineScope")


                    // Get museums nearby
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d(TAG, "Before getNearbyPlaces request")



                            val response = getNearbyPlaces("doctor",currentUserLocation,radius.toString())
                            Log.d(TAG, "After getNearbyPlaces request")
                            Log.d(TAG, "Response code: ${response.code}, message: ${response.message}")
                            if (response.isSuccessful) {
                                val places = parseResponse(response.body?.string()) // You'll need to implement parseResponse
                                // Get directions to each museum



                                places.forEach { place ->
                                    val directionsResponse = getDirections(currentUserLocation, place.location)
                                    Log.d(TAG, "Response code: ${directionsResponse.code}, message: ${directionsResponse.message}")
                                    if (directionsResponse.isSuccessful) {
                                        val routePoints = parseDirectionsResponse(directionsResponse.body?.string()) // You'll need to implement parseDirectionsResponse

                                        // Draw polyline on the map
                                        withContext(Dispatchers.Main) {
                                            val polylineOptions = PolylineOptions()
                                                .addAll(routePoints)
                                                .width(7f)
                                                .color(Color.CYAN)

                                            currentPoint= LatLng(place.location.latitude,place.location.longitude)
                                            googleMap.addMarker(MarkerOptions().position(currentPoint).title("Marker"))
                                            polylineOptions.add(currentPoint)
                                            googleMap.addPolyline(polylineOptions)
                                        }
                                    } else {
                                        Log.e(Companion.TAG, "Failed to get directions: ${directionsResponse.message}")
                                    }
                                }
                            } else {
                                Log.e(Companion.TAG, "Failed to get nearby museums: ${response.message}")
                            }
                        } catch (e: Exception) {
                            Log.e(Companion.TAG, "Error: ", e)
                            e.printStackTrace()
                        }
                    }
                }
                R.id.icon_7 -> {
                    if (!isOverlayed) {
                        googleMap.clear()
                    }
                    currentTypeSelection="local_government"
                    Log.d(TAG, "Starting CoroutineScope")


                    // Get museums nearby
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d(TAG, "Before getNearbyPlaces request")



                            val response = getNearbyPlaces("local_government",currentUserLocation,radius.toString())
                            Log.d(TAG, "After getNearbyPlaces request")
                            Log.d(TAG, "Response code: ${response.code}, message: ${response.message}")
                            if (response.isSuccessful) {
                                val places = parseResponse(response.body?.string()) // You'll need to implement parseResponse
                                // Get directions to each museum



                                places.forEach { place ->
                                    val directionsResponse = getDirections(currentUserLocation, place.location)
                                    Log.d(TAG, "Response code: ${directionsResponse.code}, message: ${directionsResponse.message}")
                                    if (directionsResponse.isSuccessful) {
                                        val routePoints = parseDirectionsResponse(directionsResponse.body?.string()) // You'll need to implement parseDirectionsResponse

                                        // Draw polyline on the map
                                        withContext(Dispatchers.Main) {
                                            val polylineOptions = PolylineOptions()
                                                .addAll(routePoints)
                                                .width(7f)
                                                .color(Color.LTGRAY)

                                            currentPoint= LatLng(place.location.latitude,place.location.longitude)
                                            googleMap.addMarker(MarkerOptions().position(currentPoint).title("Marker"))
                                            polylineOptions.add(currentPoint)
                                            googleMap.addPolyline(polylineOptions)
                                        }
                                    } else {
                                        Log.e(Companion.TAG, "Failed to get directions: ${directionsResponse.message}")
                                    }
                                }
                            } else {
                                Log.e(Companion.TAG, "Failed to get nearby museums: ${response.message}")
                            }
                        } catch (e: Exception) {
                            Log.e(Companion.TAG, "Error: ", e)
                            e.printStackTrace()
                        }
                    }
                }
            }
            drawerLayout.closeDrawers()
            true
        }




    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync { map ->
                googleMap = map
                googleMap.isMyLocationEnabled = true
                val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val locationProvider = LocationManager.GPS_PROVIDER
                val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider)
                if (lastKnownLocation != null) {
                    currentUserLocation = LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude), 15f))
                    // Now that we have location, enable menu item selection
                    enableMenuItemSelection()
                } else {
                    // TODO: Fetch location using FusedLocationProviderClient
                }
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL // Set map type to satellite view
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun enableMenuItemSelection() {
        val drawerLayout = view?.findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = view?.findViewById<NavigationView>(R.id.nav_view)

        // Rest of your menu item selection code...
    }


    private suspend fun getNearbyPlaces(placeType: String, location: LatLng, selectedRadius: String): Response {
        Log.d(TAG, "Inside getNearbyPlaces, type: $placeType, location: $location")
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("maps.googleapis.com")
            .addPathSegments("maps/api/place/nearbysearch/json")
            .addQueryParameter("location", "${location.latitude},${location.longitude}")
            .addQueryParameter("radius", selectedRadius)
            .addQueryParameter("type", placeType)
            .addQueryParameter("key", "AIzaSyAk2Dt7UY2YnTTtV-zPOYhHt1_mYV-cJqU")
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).await()
        Log.d(TAG, "After getNearbyPlaces request, response code: ${response.code}, message: ${response.message}")
        val responseBody = response.body?.string()
        Log.d(TAG, "Response body: $responseBody")
        return response.newBuilder()
            .body(responseBody?.toResponseBody(response.body?.contentType()))
            .build()
    }




    private suspend fun getDirections(from: LatLng, to: LatLng): Response {
        Log.d(TAG, "Inside getDirections, from: $from, to: $to")
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("maps.googleapis.com")
            .addPathSegments("maps/api/directions/json")
            .addQueryParameter("origin", "${from.latitude},${from.longitude}")
            .addQueryParameter("destination", "${to.latitude},${to.longitude}")
            .addQueryParameter("key", "AIzaSyAk2Dt7UY2YnTTtV-zPOYhHt1_mYV-cJqU")
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).await()

        Log.d(TAG, "Finished getDirections, response code: ${response.code}, message: ${response.message}")
        return response
    }





    private suspend fun Call.await(): Response {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response) {
                        if (continuation.isCancelled) {
                            try {
                                close()
                            } catch (ex: Exception) {
                                // Ignore the exception
                            }
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isCancelled.not()) {
                        continuation.resumeWithException(e)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    cancel()
                } catch (ex: Exception) {
                    // Ignore the exception
                }
            }
        }
    }

    fun parseResponse(response: String?): List<Place> {
        Log.d(TAG, "Inside parseResponse")
        if (response.isNullOrEmpty()) {
            Log.d(TAG, "Empty or null response in parseResponse")
            return emptyList()
        }
        val places = mutableListOf<Place>()
        val jsonObject = JSONObject(response)
        val resultsArray = jsonObject.getJSONArray("results")
        for (i in 0 until resultsArray.length()) {
            val resultObject = resultsArray.getJSONObject(i)
            val name = resultObject.getString("name")
            val locationObject = resultObject.getJSONObject("geometry").getJSONObject("location")
            val latitude = locationObject.getDouble("lat")
            val longitude = locationObject.getDouble("lng")
            val location = LatLng(latitude, longitude)
            places.add(Place(name, location))
        }
        Log.d(TAG, "Finished parsing, found ${places.size} places")
        return places
    }

    fun parseDirectionsResponse(response: String?): List<LatLng> {
        val jsonObject = JSONObject(response)
        val routesArray = jsonObject.getJSONArray("routes")
        if (routesArray.length() > 0) {
            val routeObject = routesArray.getJSONObject(0)
            val overviewPolylineObject = routeObject.getJSONObject("overview_polyline")
            val points = overviewPolylineObject.getString("points")
            return PolyUtil.decode(points)
        }
        return emptyList()
    }

    private fun clearMap() {
        googleMap.clear()
    }

    private fun drawPolyline(routePoints: List<LatLng>,color: Int) {
        val polylineOptions = PolylineOptions()
            .addAll(routePoints)
            .width(7f)
            .color(Color.RED)
        googleMap.addPolyline(polylineOptions)
        routePoints.forEach { point ->
            googleMap.addMarker(MarkerOptions().position(point))
        }
    }

    companion object {
        private const val TAG = "MyActivity"
    }


}
