package de.osca.android.map.presentation.map

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.CameraPositionState
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.map.R
import de.osca.android.map.presentation.MapViewModel
import de.osca.android.map.presentation.components.MapBottomSheetState
import de.osca.android.map.presentation.components.POIDetailsContent
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(
    mapViewModel: MapViewModel,
    bottomSheetState: BottomSheetScaffoldState,
    masterDesignArgs: MasterDesignArgs = mapViewModel.defaultDesignArgs,
    cameraPositionState: CameraPositionState,
) {
    val scrollState = rememberScrollState()
    val searchTextValue = remember { mutableStateOf("") }
    val draggableIconTopPadding = 10.dp
    val coroutineScope = rememberCoroutineScope()

    fun showBottomSheet() {
        coroutineScope.launch {
            bottomSheetState.bottomSheetState.expand()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_draggable),
            tint = masterDesignArgs.mSheetTextColor,
            contentDescription = null,
            modifier =
                Modifier
                    .padding(top = draggableIconTopPadding)
                    .clickable {
                        coroutineScope.launch {
                            if (bottomSheetState.bottomSheetState.isExpanded) {
                                bottomSheetState.bottomSheetState.collapse()
                            } else {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        }
                    },
        )

        Column(
            modifier =
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .background(masterDesignArgs.mSheetBackColor)
                    .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val contentState = mapViewModel.bottomSheetContentState.value) {
                is MapBottomSheetState.MainMenu -> {
                    mapViewModel.reloadBlocker = true
                    RDCategoryContent(
                        mapViewModel = mapViewModel,
                        searchTextValue = searchTextValue,
                        cameraPositionState = cameraPositionState,
                    )
                }

                is MapBottomSheetState.ShowCategoryFilter -> {
                    mapViewModel.reloadBlocker = true
                    RDCategoryFilterContent(
                        mapViewModel = mapViewModel,
                        selectedCategory = contentState.category,
                        cameraPositionState = cameraPositionState,
                    )

                    showBottomSheet()
                }

                is MapBottomSheetState.ShowFullList -> {
                    mapViewModel.reloadBlocker = true
                    mapViewModel.selectedCategory.value = contentState.category
                    RDFullListContent(
                        mapViewModel = mapViewModel,
                        searchTextValue = searchTextValue,
                        selectedCategory = contentState.category,
                        cameraPositionState = cameraPositionState,
                    )
                }

                is MapBottomSheetState.ShowDetails -> {
                    mapViewModel.reloadBlocker = true
                    POIDetailsContent(
                        poi = contentState.poi,
                        masterDesignArgs = masterDesignArgs,
                        mapViewModel = mapViewModel,
                        cameraPositionState = cameraPositionState,
                        bottomSheetState = bottomSheetState,
                    )

                    coroutineScope.launch {
                        scrollState.scrollTo(0)
                    }
                }
            }
        }
    }
}
