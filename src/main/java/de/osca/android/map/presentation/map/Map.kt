package de.osca.android.map.presentation.map

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.PermissionChecker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.utils.extensions.getLastDeviceLocation
import de.osca.android.essentials.utils.extensions.shortToast
import de.osca.android.map.R
import de.osca.android.map.presentation.MapViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun Map(
    initialLocation: LatLng,
    mapViewModel: MapViewModel,
    bottomSheetState: BottomSheetScaffoldState,
    masterDesignArgs: MasterDesignArgs = mapViewModel.defaultDesignArgs,
    cameraPositionState: CameraPositionState,
    initializePoi: String? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val location = remember { mutableStateOf(initialLocation) }
    val userLocationFound = remember { mutableStateOf(false) }

    val markerPOIs = remember { mapViewModel.markerPOIs }
    val hasLocationPermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED

    context.getLastDeviceLocation { result ->
        result?.let { latLng ->
            location.value = LatLng(
                latLng.latitude,
                latLng.longitude
            )
            userLocationFound.value = true
        } ?: with(context) {
            shortToast(text = getString(R.string.global_no_location))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(masterDesignArgs.mCardBackColor)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                contentPadding = PaddingValues(bottom = 50.dp),
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission, mapStyleOptions = if(mapViewModel.mapDesignArgs.mapStyle != null) MapStyleOptions.loadRawResourceStyle(context, mapViewModel.mapDesignArgs.mapStyle!!) else null),
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    tiltGesturesEnabled = false,
                    mapToolbarEnabled = false,
                    indoorLevelPickerEnabled = false,
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = false
                ),
                onMapClick = { latLng ->
                },
                onMapLoaded = {
                    mapViewModel.setClusterManagerAndPOIs(context, location.value, userLocationFound.value, initializePoi)

                    coroutineScope.launch {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(location.value, 15.0f)
                        )
                    }
                },
            ) {
                markerPOIs.forEach { markerOption ->
                    Marker(
                        icon = markerOption.icon,
                        title = markerOption.title,
                        snippet = markerOption.snippet,
                        state = MarkerState(markerOption.position),
                        onClick = { marker ->
                            mapViewModel.onMarkerClicked(marker)

                            coroutineScope.launch {
                                bottomSheetState.bottomSheetState.expand()

                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(marker.position, 17.0f)
                                )
                            }

                            true
                        }
                    )
                }
            }
        }
    }
}