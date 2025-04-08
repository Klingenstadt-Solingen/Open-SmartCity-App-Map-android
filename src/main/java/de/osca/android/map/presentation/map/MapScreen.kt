package de.osca.android.map.presentation.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.screen_wrapper.ScreenWrapper
import de.osca.android.essentials.utils.extensions.SetSystemStatusBar
import de.osca.android.map.presentation.MapViewModel
import kotlinx.coroutines.launch

/**
 * @param initializeCoordinates
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    initializeCoordinates: LatLng,
    initializeCategory: String? = null,
    initializePoi: String? = null,
    mapViewModel: MapViewModel = hiltViewModel(),
    masterDesignArgs: MasterDesignArgs = mapViewModel.defaultDesignArgs
) {
    val design = mapViewModel.mapDesignArgs

    mapViewModel.selectedCategory.value = mapViewModel.poiCategories.firstOrNull {
        it.sourceId == initializeCategory || it.objectId == initializeCategory
    }

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            if (mapViewModel.selectedCategory.value != null) BottomSheetValue.Collapsed else BottomSheetValue.Expanded,
            LocalDensity.current
        ) {
            true
        },
    )

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val cameraPositionState = rememberCameraPositionState()

    SetSystemStatusBar(
        !(design.mIsStatusBarWhite ?: masterDesignArgs.mIsStatusBarWhite), Color.Transparent
    )

    LaunchedEffect(Unit) {
        mapViewModel.initializePOIAndCategory()
    }

    BackHandler {
        if (bottomSheetState.bottomSheetState.isExpanded) {
            coroutineScope.launch {
                bottomSheetState.bottomSheetState.collapse()
            }
        } else {
            navController.navigate("dashboard")
        }
    }

    ScreenWrapper(
        withTopBar = false,
        overrideStatusBar = true,
        screenWrapperState = mapViewModel.wrapperState,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        retryAction = {
            mapViewModel.initializePOIAndCategory()
        },
        masterDesignArgs = masterDesignArgs,
        moduleDesignArgs = design
    ) {
        BottomSheetScaffold(
            modifier = Modifier.semantics {
                testTag = "BottomSheetScaffold"
            },
            sheetBackgroundColor = design.mSheetBackColor ?: masterDesignArgs.mSheetBackColor,
            scaffoldState = bottomSheetState,
            sheetShape = masterDesignArgs.mShapeBottomSheet,
            sheetElevation = masterDesignArgs.mSheetElevation,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetContent(
                        mapViewModel = mapViewModel,
                        bottomSheetState = bottomSheetState,
                        cameraPositionState = cameraPositionState
                    )
                }
            },
            sheetPeekHeight = 40.dp
        ) {
            Map(
                initialLocation = initializeCoordinates,
                mapViewModel = mapViewModel,
                bottomSheetState = bottomSheetState,
                cameraPositionState = cameraPositionState,
                initializePoi = initializePoi
            )
        }
    }
}
