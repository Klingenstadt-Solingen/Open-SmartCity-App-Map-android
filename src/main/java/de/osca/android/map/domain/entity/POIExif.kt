package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class POIExif(
    @SerializedName("ImageDescription")
    val imageDescription: String? = null,
    @SerializedName("Copyright")
    val copyright: String? = null,
)
