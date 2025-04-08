package de.osca.android.map.domain.entity.poi_details

import com.google.gson.annotations.SerializedName

data class POIDetailValue(
    @SerializedName("subtitle")
    val subtitle: String? = null,
    @SerializedName("value")
    val value: String = ""
)