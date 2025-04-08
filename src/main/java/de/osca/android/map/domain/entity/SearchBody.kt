package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class SearchBody(
    @SerializedName("index")
    val index: String? = "new_poi",
    @SerializedName("query")
    val query: String? = "",
    @SerializedName("raw")
    val raw: Boolean = false
) {
}