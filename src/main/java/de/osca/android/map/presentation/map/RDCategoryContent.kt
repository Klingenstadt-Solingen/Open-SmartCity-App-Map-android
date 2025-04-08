package de.osca.android.map.presentation.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import de.osca.android.essentials.presentation.component.design.BaseListContainer
import de.osca.android.essentials.presentation.component.design.BaseTextField
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.MultiColumnList
import de.osca.android.essentials.presentation.component.design.PoiCategoryElement
import de.osca.android.essentials.presentation.component.design.SimpleSpacedList
import de.osca.android.essentials.utils.extensions.getLastDeviceLocation
import de.osca.android.essentials.utils.extensions.shortToast
import de.osca.android.map.R
import de.osca.android.map.presentation.MapViewModel
import kotlinx.coroutines.launch


@Composable
fun RDCategoryContent(
    mapViewModel: MapViewModel,
    masterDesignArgs: MasterDesignArgs = mapViewModel.defaultDesignArgs,
    cameraPositionState: CameraPositionState,
    searchTextValue: MutableState<String>
) {
    val context = LocalContext.current
    val design = mapViewModel.mapDesignArgs

    val originCategories = remember { mapViewModel.poiCategories }
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchResults = remember { mapViewModel.searchPoiList }
    val coroutineScope = rememberCoroutineScope()
    val location = remember { mutableStateOf<LatLng?>(null) }

    context.getLastDeviceLocation { result ->
        result?.let { latLng ->
            location.value = LatLng(
                latLng.latitude,
                latLng.longitude
            )
        } ?: with(context) {
            shortToast(text = getString(R.string.global_no_location))
        }
    }

    SimpleSpacedList(
        masterDesignArgs = masterDesignArgs,
        overrideSpace = 16.dp,
        borderSpace = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BaseTextField(
                textFieldTitle = stringResource(id = R.string.map_search),
                textValue = searchTextValue,
                isOutlined = false,
                onTextChange = {
                    searchResults.clear()
                },
                onClear = {
                    mapViewModel.showAllMarkers()
                    searchTextValue.value = ""
                    searchResults.clear()
                },
                onDone = {
                    mapViewModel.findInAllPOIs(searchTextValue.value, true)

                    keyboardController?.hide()
                },
                masterDesignArgs = masterDesignArgs,
                moduleDesignArgs = design
            )
        }

        if (searchResults.size <= 0) {
            BaseListContainer(
                text = stringResource(id = R.string.map_categories),
                textRowModifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                showMoreOption = false,
                masterDesignArgs = masterDesignArgs,
                moduleDesignArgs = design
            ) {
                val columnCount = mapViewModel.mapDesignArgs.categoryColumnCount
                MultiColumnList(
                    columnCount = columnCount
                ) {
                    originCategories.map { poiCategory ->
                        { modifier ->
                            PoiCategoryElement(
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = design,
                                text = poiCategory.name.toString(),
                                imageUrl = poiCategory.iconUrl,
                                onClick = {
                                    if (!mapViewModel.reloadBlocker) {
                                        mapViewModel.selectedCategory.value = poiCategory
                                        mapViewModel.getFilterCategories(poiCategory.sourceId.toString()) {
                                            if(design.skipFilterWhenEmpty && it) {
                                                mapViewModel.showFullList(poiCategory)
                                            } else {
                                                mapViewModel.showCategoryFilter(poiCategory)
                                            }
                                        }
                                        mapViewModel.filterMarkersByCategory(poiCategory)
                                    }
                                },
                                modifier = modifier
                            )
                        }
                    }
                }
            }
        } else {
            BaseListContainer(
                text = "Suchvorschläge",
                textRowModifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                showMoreOption = false,
                masterDesignArgs = masterDesignArgs,
                moduleDesignArgs = design
            ) {
                mapViewModel.searchPoiList.forEach { poi ->
                    RDListElement(
                        mapViewModel = mapViewModel,
                        masterDesignArgs = masterDesignArgs,
                        moduleDesignArgs = design,
                        category = poi.category,
                        poi = poi,
                        userLocation = location.value,
                        fromSearching = searchTextValue.value,
                        cameraPositionState = cameraPositionState,
                        onCLick = { poi, fromSearching ->
                            mapViewModel.poiList.clear()
                            mapViewModel.markerPOIs.clear()
                            mapViewModel.allMarkerPOIs.clear()

                            mapViewModel.poiList.add(poi)
                            mapViewModel.setCategories()

                            mapViewModel.showPOIDetails(poi, fromSearching)

                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        poi.geoPoint.toLatLng(), 17.0f)
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    mapViewModel.reloadBlocker = false
}