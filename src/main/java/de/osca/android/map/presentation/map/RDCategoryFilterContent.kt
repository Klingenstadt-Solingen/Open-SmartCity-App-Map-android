package de.osca.android.map.presentation.map

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.maps.android.compose.CameraPositionState
import de.osca.android.essentials.domain.entity.BaseListState
import de.osca.android.essentials.presentation.component.design.BaseListContainer
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.SimpleSpacedList
import de.osca.android.essentials.utils.extensions.getBounds
import de.osca.android.essentials.utils.extensions.safeTake
import de.osca.android.map.domain.entity.POICategory
import de.osca.android.map.presentation.MapViewModel
import de.osca.android.map.presentation.components.BackButton
import de.osca.android.map.presentation.components.NextButton
import kotlinx.coroutines.launch

@Composable
fun RDCategoryFilterContent(
    mapViewModel: MapViewModel,
    masterDesignArgs: MasterDesignArgs = mapViewModel.defaultDesignArgs,
    selectedCategory: POICategory?,
    cameraPositionState: CameraPositionState,
) {
    val design = mapViewModel.mapDesignArgs
    val coroutineScope = rememberCoroutineScope()
    val listCount = remember { mapViewModel.listCount }

    if (listCount.value > 0) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                cameraPositionState.animate(
                    getBounds(
                        mapViewModel.poiList.map { it.geoPoint },
                        cameraPositionState.position.target,
                    ),
                )
            }
        }
    }

    SimpleSpacedList(
        masterDesignArgs = masterDesignArgs,
        overrideSpace = 16.dp,
        borderSpace = 16.dp,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            BackButton(
                onClick = {
                    mapViewModel.showMainMenu()
                },
                iconTint = masterDesignArgs.highlightColor,
                textColor =
                    design.mButtonBackgroundColor
                        ?: masterDesignArgs.mButtonBackgroundColor,
                textStyle = masterDesignArgs.bodyTextStyle,
            )

            NextButton(
                label = "${listCount.value} Punkte anzeigen",
                onClick = {
                    if (selectedCategory != null) {
                        mapViewModel.showFullList(selectedCategory)
                    }
                },
                iconTint = masterDesignArgs.highlightColor,
                textColor =
                    design.mButtonBackgroundColor
                        ?: masterDesignArgs.mButtonBackgroundColor,
                textStyle = masterDesignArgs.bodyTextStyle,
            )
        }

        if (mapViewModel.filterCategories.isNotEmpty()) {
            SimpleSpacedList(
                masterDesignArgs = masterDesignArgs,
            ) {
                val filters = mapViewModel.filterCategories
                for (filter in filters) {
                    BaseListContainer(
                        text = filter.title,
                        showMoreOption = filter.values.size > 0,
                        masterDesignArgs = masterDesignArgs,
                        moduleDesignArgs = design,
                        overrideSpace = 4.dp,
                    ) {
                        val filterValues =
                            if (it.value == BaseListState.Expanded) {
                                filter.values
                            } else {
                                filter.values.safeTake(6)
                            }

                        FlowRow(
                            mainAxisSpacing = 4.dp,
                            mainAxisSize = SizeMode.Expand,
                            crossAxisSpacing = 4.dp,
                        ) {
                            filterValues.forEach { filterValue ->
                                RDFilterSelectionButton(
                                    isReady = mutableStateOf(!cameraPositionState.isMoving),
                                    moduleDesignArgs = design,
                                    masterDesignArgs = masterDesignArgs,
                                    filterText = filterValue,
                                    onSelect = { isSelected ->
                                        if (selectedCategory != null) {
                                            if (isSelected) {
                                                filter.selectedFilters.add(filterValue)
                                            } else {
                                                filter.selectedFilters.remove(filterValue)
                                            }
                                            mapViewModel.filterMarkersByCategory(
                                                selectedCategory,
                                                false,
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
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    mapViewModel.reloadBlocker = false
}
