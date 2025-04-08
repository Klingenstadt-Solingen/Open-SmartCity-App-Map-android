package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class POIFiltered(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("filter")
    val filter: List<FilterForPOI> = emptyList()
)

data class FilterForPOI(
    @SerializedName("field")
    val field: String = "",
    @SerializedName("value")
    val value: String = ""
)