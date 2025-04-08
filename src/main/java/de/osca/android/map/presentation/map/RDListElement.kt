package de.osca.android.map.presentation.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.map.domain.entity.POI
import de.osca.android.map.domain.entity.POICategory
import de.osca.android.map.presentation.MapViewModel
import kotlinx.coroutines.launch

@Composable
fun RDListElement(
    mapViewModel: MapViewModel,
    masterDesignArgs: MasterDesignArgs,
    moduleDesignArgs: ModuleDesignArgs,
    category: POICategory? = null,
    poi: POI? = null,
    userLocation: LatLng? = null,
    fromSearching: String? = null,
    cameraPositionState: CameraPositionState,
    onCLick: ((POI, String?) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()

    BaseCardContainer(
        masterDesignArgs = masterDesignArgs,
        moduleDesignArgs = moduleDesignArgs,
        useContentPadding = false,
        onClick = {
            if(poi != null) {
                if(onCLick != null) {
                    onCLick(poi, fromSearching)
                } else {
                    mapViewModel.showPOIDetails(poi, fromSearching)

                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                poi.geoPoint.toLatLng(), 17.0f)
                        )
                    }
                }
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(category?.iconUrl),
                contentDescription = "categoryIcon",
                modifier = Modifier
                    .size(45.dp)
            )

            Spacer(modifier = Modifier
                .width(8.dp)
            )

            Column(modifier = Modifier
                .weight(1.0f)
            ) {
                Text(
                    text = poi?.name.toString(),
                    style = masterDesignArgs.normalTextStyle,
                    color = moduleDesignArgs.mCardTextColor ?: masterDesignArgs.mCardTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if(poi?.details != null && userLocation != null) {
                    val distance = Coordinates(
                        latitude = userLocation.latitude,
                        longitude = userLocation.longitude
                    ).distanceTo(poi.geoPoint)
                    val details = mapViewModel.getDetails(poi.details)

                    val distanceFormatted = when(distance) {
                         in 0.0f..999.9f -> { "${distance.toInt()} m" }
                         in 1000.0f..10000.0f -> { "${distance / 1000.0f} km" }
                        else -> { "${distance / 1000.0f} km" }
                    }
                    Text(
                        text = "Entfernung $distanceFormatted",
                        style = masterDesignArgs.subtitleTextStyle,
                        color = moduleDesignArgs.mCardTextColor ?: masterDesignArgs.mCardTextColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier
                .width(8.dp)
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "nextArrowIcon",
                tint = masterDesignArgs.highlightColor,
                modifier = Modifier
                    .size(25.dp),
            )
        }
    }
}