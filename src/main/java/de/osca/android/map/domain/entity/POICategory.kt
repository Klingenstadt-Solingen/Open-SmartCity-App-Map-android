package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class POICategory(
    @SerializedName("objectId")
    val objectId: String? = null,
    @SerializedName("position")
    val position: Int = 0,
    @SerializedName("iconPath")
    val iconPath: String? = null,
    @SerializedName("iconName")
    val iconName: String? = null,
    @SerializedName("iconMimetype")
    val iconMimeType: String? = null,
    @SerializedName("symbolName")
    val symbolName: String? = null,
    @SerializedName("symbolPath")
    val symbolPath: String? = null,
    @SerializedName("symbolMimetype")
    val symbolMimetype: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("showCategory")
    val showCategory: String? = "true",
    @SerializedName("metathema")
    val metathema: String? = null,
    @SerializedName("mapTitle")
    val mapTitle: String? = null,
    @SerializedName("sourceId")
    val sourceId: String? = null,
    @SerializedName("filterFields")
    val filterFields: List<FilterForPOI> = emptyList(),
    @SerializedName("defaultThematicView")
    val defaultThematicView: String? = null
) {
    val iconUrl get() = if(!iconPath.isNullOrEmpty()) "$iconPath${if(iconPath[iconPath.length-1] == '/') "" else "/" }$iconName$iconMimeType" else null
    val symbolUrl get() = if(!symbolPath.isNullOrEmpty()) "$symbolPath${if(symbolPath[symbolPath.length-1] == '/') "" else "/" }$symbolName$symbolMimetype" else null
}