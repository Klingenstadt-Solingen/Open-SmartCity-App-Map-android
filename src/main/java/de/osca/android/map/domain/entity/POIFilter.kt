package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class POIFilter(
    @SerializedName("category")
    val category: String = ""
)