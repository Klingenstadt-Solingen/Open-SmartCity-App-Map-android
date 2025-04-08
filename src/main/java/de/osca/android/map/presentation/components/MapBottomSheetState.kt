package de.osca.android.map.presentation.components

import de.osca.android.map.domain.entity.POI
import de.osca.android.map.domain.entity.POICategory

sealed class MapBottomSheetState {
    object MainMenu : MapBottomSheetState()
    class ShowDetails(val poi: POI) : MapBottomSheetState()
    class ShowCategoryFilter(val category: POICategory) : MapBottomSheetState()
    class ShowFullList(val category: POICategory) : MapBottomSheetState()

    val isMainMenu get() = this is MainMenu
    val isCategoryFilter get() = this is ShowCategoryFilter
    val isFullList get() = this is ShowFullList
    val isDetails get() = this is ShowDetails
}