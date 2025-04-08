package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class NewPoiWrapper(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("_source")
    val source: NewPoiId = NewPoiId(),
) {
}

data class NewPoiId(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = ""
) {
}