package de.osca.android.map.presentation.components

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.HorizontalScrollList
import de.osca.android.essentials.presentation.component.design.MainButton
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.OpenEmailElement
import de.osca.android.essentials.presentation.component.design.OpenPhoneElement
import de.osca.android.essentials.presentation.component.design.OpenWebsiteElement
import de.osca.android.essentials.presentation.component.design.SimpleSpacedList
import de.osca.android.essentials.utils.extensions.getBounds
import de.osca.android.essentials.utils.extensions.getLastDeviceLocation
import de.osca.android.essentials.utils.extensions.openMapRouteTo
import de.osca.android.essentials.utils.extensions.shareText
import de.osca.android.map.R
import de.osca.android.map.domain.entity.POI
import de.osca.android.map.domain.entity.POIImage
import de.osca.android.map.domain.entity.poi_details.POITwoPairValueType
import de.osca.android.map.presentation.MapViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun POIDetailsContent(
    poi: POI,
    mapViewModel: MapViewModel,
    masterDesignArgs: MasterDesignArgs,
    cameraPositionState: CameraPositionState,
    bottomSheetState: BottomSheetScaffoldState
) {
    val context = LocalContext.current
    val design = mapViewModel.mapDesignArgs

    val sharePOIVal = remember { mutableStateOf(false) }
    val distanceToPoi = remember { mutableStateOf(0.0f) }
    val iconSize = remember { mutableStateOf(35.dp) }
    val showImageDialog = remember { mutableStateOf<POIImage?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(
                poi.geoPoint.toLatLng(), 15.0f
            )
        )
    }

    BackHandler {
        if(showImageDialog.value != null) {
            showImageDialog.value = null
        } else {
            if (bottomSheetState.bottomSheetState.isExpanded) {
                coroutineScope.launch {
                    bottomSheetState.bottomSheetState.collapse()
                }
            } else {
                if (mapViewModel.markerPOIs.isNotEmpty()) {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            getBounds(mapViewModel.poiList.map { it.geoPoint }, poi.geoPoint.toLatLng())
                        )
                    }
                }

                if (mapViewModel.wasSearching != null || mapViewModel.selectedCategory.value == null) {
                    mapViewModel.showMainMenu()
                } else {
                    if (poi.category != null) {
                        mapViewModel.showFullList(poi.category!!)
                    }
                }
            }
        }
    }


    if (sharePOIVal.value) {
        sharePOI(
            context = context,
            title = poi.name ?: "",
            poiLocation = poi.address ?: "",
            appStoreLink = design.appStoreLink,
            poiGeoPoint = poi.geoPoint.toLatLng(),
            poiText = stringResource(id = design.poiShareText),
            cityName = design.cityName,
            sharePress = sharePOIVal
        )
    }

    if (mapViewModel.mapDesignArgs.showDistanceIfAvailable) {
        context.getLastDeviceLocation { result ->
            if (result != null) {
                distanceToPoi.value = Coordinates(
                    latitude = result.latitude,
                    longitude = result.longitude
                ).distanceTo(poi.geoPoint)
            }
        }
    }

    if(showImageDialog.value != null) {
        PictureDialog(
            masterDesignArgs = masterDesignArgs,
            moduleDesignArgs = design,
            pictures = poi.images,
            activePicture = showImageDialog.value!!,
            setShowDialog = {
                showImageDialog.value = null
            }
        )
    }

    SimpleSpacedList(
        masterDesignArgs = masterDesignArgs,
        overrideSpace = 16.dp,
        borderSpace = 16.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            if(mapViewModel.markerPOIs.isNotEmpty()) {
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        getBounds(mapViewModel.poiList.map { it.geoPoint }, poi.geoPoint.toLatLng())
                                    )
                                }
                            }

                            if(mapViewModel.wasSearching != null || mapViewModel.selectedCategory.value == null) {
                                mapViewModel.showMainMenu()
                            } else {
                                if (poi.category != null) {
                                    mapViewModel.showFullList(poi.category!!)
                                }
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "backArrowIcon",
                        tint = masterDesignArgs.highlightColor,
                        modifier = Modifier
                            .size(25.dp)
                    )

                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                    )

                    Text(
                        text = "Zurück",
                        style = masterDesignArgs.bodyTextStyle,
                        color = design.mButtonBackgroundColor
                            ?: masterDesignArgs.mButtonBackgroundColor
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 100.dp)
    ) {
        Text(
            text = poi.name ?: "",
            style = masterDesignArgs.bigTextStyle,
            color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )

        if (mapViewModel.mapDesignArgs.showDistanceIfAvailable) {
            val distanceFormatted = when(distanceToPoi.value) {
                in 0.0f..999.9f -> { "${distanceToPoi.value.toInt()} m" }
                in 1000.0f..10000.0f -> { "${distanceToPoi.value / 1000.0f} km" }
                else -> { "${distanceToPoi.value / 1000.0f} km" }
            }
            Text(
                text = "${poi.category?.name} - Entfernung ca. $distanceFormatted",
                modifier = Modifier
                    .padding(bottom = 20.dp),
                color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor
            )
        }

        Text(
            text = "${poi.district}",
            style = masterDesignArgs.normalTextStyle,
            color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor
        )
        Text(
            text = "${poi.address}",
            style = masterDesignArgs.normalTextStyle,
            color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor
        )
        Text(
            text = "${poi.zip ?: ""} ${poi.city ?: ""}",
            style = masterDesignArgs.normalTextStyle,
            color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor
        )

        if (mapViewModel.mapDesignArgs.showRouteButton && poi.showRouteTo) {
            MainButton(
                buttonText = stringResource(id = R.string.global_show_route),
                onClick = {
                    context.openMapRouteTo(poi.geoPoint)
                },
                masterDesignArgs = masterDesignArgs,
                moduleDesignArgs = design
            )
        }

        IconButton(
            onClick = {
                sharePOIVal.value = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                tint = design.mButtonBackgroundColor ?: masterDesignArgs.mButtonBackgroundColor,
                contentDescription = "share"
            )
        }

        if (poi.images.isNotEmpty()) {
            HorizontalScrollList(
                space = 16.dp,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 28.dp)
            ) {
                items(
                    poi.images,
                    itemContent = { imageData ->
                        BaseCardContainer(
                            moduleDesignArgs = design,
                            useContentPadding = false,
                            onClick = {
                                showImageDialog.value = imageData
                            },
                            masterDesignArgs = masterDesignArgs
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(imageData.imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(130.dp)
                            )
                        }
                    }
                )
            }
        } else {
            Divider(
                color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
            )
        }

        SimpleSpacedList(
            masterDesignArgs = masterDesignArgs,
            overrideSpace = 8.dp
        ) {
            mapViewModel.getDetails(poi.details.sortedBy { it.position }).forEach { detail ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(detail.iconUrl != null) {
                        Icon(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current).data(detail.iconUrl).apply(block = {
                                    iconSize.value = 25.dp
                                    error(R.drawable.ic_circle)
                                }).build()
                            ),
                            contentDescription = "detailIcon",
                            tint = masterDesignArgs.highlightColor,
                            modifier = Modifier
                                .size(iconSize.value)
                                .padding(end = if (iconSize.value == 25.dp) 8.dp else 0.dp)
                        )
                    }

                    Text(
                        text = detail.majorTitle ?: detail.title ?: detail.subtitle ?: "Detail",
                        color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor,
                        style = masterDesignArgs.bodyTextStyle
                    )
                }

                Column {
                    for (value in detail.values) {
                        when (detail.type) {
                            POITwoPairValueType.TEXT -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (detail.majorTitle != null) "${value.subtitle} ${value.value}" else value.value,
                                        color = design.mSheetTextColor
                                            ?: masterDesignArgs.mSheetTextColor,
                                        style = masterDesignArgs.normalTextStyle,
                                        modifier = Modifier
                                            .weight(2f)
                                    )
                                }
                            }
                            POITwoPairValueType.OPEN_HOURS -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    if(value.subtitle != null && !value.subtitle.contains("null")) {
                                        Text(
                                            text = value.subtitle,
                                            style = masterDesignArgs.normalTextStyle,
                                            color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                    }

                                    Text(
                                        text = value.value,
                                        style = masterDesignArgs.normalTextStyle,
                                        color = design.mSheetTextColor ?: masterDesignArgs.mSheetTextColor,
                                        modifier = Modifier
                                            .weight(2f)
                                    )
                                }
                            }
                            POITwoPairValueType.PHONE -> {
                                OpenPhoneElement(
                                    phone = value.value,
                                    withTitle = false,
                                    context = context,
                                    masterDesignArgs = masterDesignArgs,
                                    moduleDesignArgs = design
                                )
                            }
                            POITwoPairValueType.URL -> {
                                //

                                OpenWebsiteElement(
                                    url = value.value,
                                    withTitle = false,
                                    context = context,
                                    masterDesignArgs = masterDesignArgs,
                                    moduleDesignArgs = design
                                )
                            }
                            POITwoPairValueType.EMAIL -> {
                                OpenEmailElement(
                                    email = value.value,
                                    withTitle = false,
                                    context = context,
                                    masterDesignArgs = masterDesignArgs,
                                    moduleDesignArgs = design
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun sharePOI(
    context: Context,
    title: String = "",
    poiText: String = "",
    poiLocation: String = "",
    poiGeoPoint: LatLng,
    appStoreLink: String = "",
    @StringRes cityName: Int = -1,
    sharePress: MutableState<Boolean>
) {
    var header = "$poiText\n\n$title\n$poiLocation\n\n"
    header += "In GoogleMaps anzeigen: https://maps.google.com/?q=${poiGeoPoint.latitude},${poiGeoPoint.longitude}"
    val details = "\n\n${stringResource(id = cityName)}-App:\n${appStoreLink}"

    context.shareText(
        title = stringResource(id = R.string.global_share_text),
        text = "$header$details"
    )

    sharePress.value = false
}