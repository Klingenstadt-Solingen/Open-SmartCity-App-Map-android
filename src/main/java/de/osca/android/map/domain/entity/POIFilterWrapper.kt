package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class POIFilterWrapper(
    @SerializedName("items")
    val items: List<POI> = emptyList()
)