package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class POINearByRequest(
    @SerializedName("lat")
    val lat: Double = 0.0,
    @SerializedName("lon")
    val lon: Double = 0.0,
    @SerializedName("distance")
    val distance: Int = 1000,
    @SerializedName("limit")
    val limit: Int = 3,
    @SerializedName("random")
    val random: Boolean = true,
) {
}