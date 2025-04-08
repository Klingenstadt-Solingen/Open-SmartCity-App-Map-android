package de.osca.android.map.presentation.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import de.osca.android.essentials.presentation.component.design.BaseListContainer
import de.osca.android.essentials.presentation.component.design.BaseTextField
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.SimpleSpacedList
import de.osca.android.essentials.utils.extensions.getBounds
import de.osca.android.essentials.utils.extensions.getLastDeviceLocation
import de.osca.android.essentials.utils.extensions.shortToast
import de.osca.android.map.R
import de.osca.android.map.domain.entity.POICategory
import de.osca.android.map.presentation.MapViewModel
import de.osca.android.map.presentation.components.BackButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RDFullListContent(
    mapViewModel: MapViewModel,
    masterDesignArgs: MasterDesignArgs = mapViewModel.defaultDesignArgs,
    searchTextValue: MutableState<String>,
    selectedCategory: POICategory?,
    cameraPositionState: CameraPositionState,
) {
    val context = LocalContext.current
    val design = mapViewModel.mapDesignArgs
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val location = remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        mapViewModel.selectedCategory.value = selectedCategory
        coroutineScope.launch {
            cameraPositionState.animate(
                getBounds(
                    mapViewModel.poiList.map { it.geoPoint },
                    cameraPositionState.position.target,
                ),
            )
        }
    }

    context.getLastDeviceLocation { result ->
        result?.let { latLng ->
            location.value =
                LatLng(
                    latLng.latitude,
                    latLng.longitude,
                )
        } ?: with(context) {
            shortToast(text = getString(R.string.global_no_location))
        }
    }

    SimpleSpacedList(
        masterDesignArgs = masterDesignArgs,
        overrideSpace = 16.dp,
        borderSpace = 16.dp,
    ) {
        BackButton(
            onClick = {
                if (mapViewModel.selectedCategory.value != null) {
                    mapViewModel.filterMarkersByCategory(
                        mapViewModel.selectedCategory.value!!,
                        true,
                    ) {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                getBounds(
                                    mapViewModel.poiList.map { it.geoPoint },
                                    cameraPositionState.position.target,
                                ),
                            )
                        }
                    }

                    if (design.skipFilterWhenEmpty && mapViewModel.filterCategories.isEmpty()) {
                        mapViewModel.showMainMenu()
                    } else {
                        mapViewModel.showCategoryFilter(mapViewModel.selectedCategory.value!!)
                    }
                }
            },
            iconTint = masterDesignArgs.highlightColor,
            textColor =
                design.mButtonBackgroundColor
                    ?: masterDesignArgs.mButtonBackgroundColor,
            textStyle = masterDesignArgs.bodyTextStyle,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            BaseTextField(
                textFieldTitle = stringResource(id = R.string.map_search),
                textValue = searchTextValue,
                isOutlined = false,
                onTextChange = {
                    mapViewModel.search(it)
                },
                onClear = {
                    searchTextValue.value = ""
                },
                onDone = {
                    keyboardController?.hide()
                },
                masterDesignArgs = masterDesignArgs,
                moduleDesignArgs = design,
            )
        }

        var list = mapViewModel.searchPOIList(searchTextValue.value)

        BaseListContainer(
            text =
                mapViewModel.selectedCategory.value
                    ?.name
                    .toString(),
            textRowModifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            showMoreOption = false,
            masterDesignArgs = masterDesignArgs,
            moduleDesignArgs = design,
            content = null,
            contents =
                {
                    items(list) { poi ->
                        RDListElement(
                            mapViewModel = mapViewModel,
                            masterDesignArgs = masterDesignArgs,
                            moduleDesignArgs = design,
                            category = mapViewModel.selectedCategory.value,
                            poi = poi,
                            userLocation = location.value,
                            cameraPositionState = cameraPositionState,
                        )
                    }
                },
        )
    }

    mapViewModel.reloadBlocker = false
}
