package de.osca.android.map.presentation.args

import androidx.compose.ui.unit.Dp
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.essentials.presentation.component.design.WidgetDesignArgs

interface MapDesignArgs : ModuleDesignArgs, WidgetDesignArgs {
    val showDistanceIfAvailable: Boolean
    val showRouteButton: Boolean
    val showImages: Boolean
    val categoryColumnCount: Int
    val mapCardHeight: Dp

    val poiShareText: Int
    val appStoreLink: String
    val cityName: Int

    val showSamplePOIs: Int?

    val mapInitialZoomLevel: Float
    val skipFilterWhenEmpty: Boolean

    val showDefaultAll: Boolean
    val mapStyle: Int?
}