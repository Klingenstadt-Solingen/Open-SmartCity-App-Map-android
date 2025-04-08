package de.osca.android.map.domain.entity

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.annotations.SerializedName

data class FilterResult(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("field")
    val field: String = "",
    @SerializedName("values")
    val values: MutableList<String> = mutableStateListOf()
) {
    var selectedFilters = mutableStateListOf<String>()
}