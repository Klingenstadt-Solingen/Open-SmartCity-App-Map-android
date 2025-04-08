package de.osca.android.map.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.BaseListContainer
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.map.navigation.MapNavItems
import de.osca.android.map.presentation.MapViewModel

/**
 * @param initialLocation
 */
@Composable
fun MapWidget(
    navController: NavController,
    initialLocation: LatLng,
    category: String? = null,
    @DrawableRes iconUnderLine: Int = -1,
    underLineColor: Color = Color.White,
    mapViewModel: MapViewModel = hiltViewModel(),
    masterDesignArgs: MasterDesignArgs = mapViewModel.defaultDesignArgs
) {
    if(mapViewModel.mapDesignArgs.vIsWidgetVisible) {
        val context = LocalContext.current
        val design = mapViewModel.mapDesignArgs

        LaunchedEffect(Unit) {
            mapViewModel.fetchSamplePOIs(design.showSamplePOIs, 1000, context, initialLocation)
        }

        BaseListContainer(
            text = stringResource(id = design.vWidgetTitle),
            showMoreOption = design.vWidgetShowMoreOption,
            iconUnderLine = iconUnderLine,
            underLineColor = underLineColor,
            moduleDesignArgs = design,
            onMoreOptionClick = {
                navController.navigate(MapNavItems.getMapCategoryRoute(category))
            },
            masterDesignArgs = masterDesignArgs
        ) {
            BaseCardContainer(
                moduleDesignArgs = design,
                useContentPadding = false,
                overrideConstraintHeight = design.mapCardHeight,
                masterDesignArgs = masterDesignArgs
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                ) {
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = CameraPositionState(
                            CameraPosition(initialLocation, 15f, 0f, 0f)
                        ),
                        properties = MapProperties(mapStyleOptions = if(design.mapStyle != null) MapStyleOptions.loadRawResourceStyle(context, design.mapStyle!!) else null),
                        uiSettings = MapUiSettings(
                            compassEnabled = false,
                            rotationGesturesEnabled = false,
                            scrollGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            mapToolbarEnabled = false,
                            indoorLevelPickerEnabled = false,
                            myLocationButtonEnabled = false,
                            zoomControlsEnabled = false,
                            zoomGesturesEnabled = false
                        ),
                        onMapClick = { _ ->
                            navController.navigate(MapNavItems.getMapCategoryRoute(category))
                        },
                        onMapLoaded = {
                            // ...
                        },
                    ) {
                        mapViewModel.markerPOIs.forEach { markerOption ->
                            Marker(
                                icon = markerOption.icon,
                                title = markerOption.title,
                                snippet = markerOption.snippet,
                                state = MarkerState(markerOption.position)
                            )
                        }
                    }
                }
            }
        }
    }
}