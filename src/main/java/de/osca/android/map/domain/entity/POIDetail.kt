package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName
import de.osca.android.essentials.utils.extensions.representsBoolean

data class POIDetail(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("majortitle")
    val majorTitle: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("subtitle")
    val subtitle: String? = null,
    @SerializedName("value")
    val value: String? = null,
    @SerializedName("position")
    val position: Int = 0,
    @SerializedName("iconURL")
    val iconURL: String? = null,
    @SerializedName("iconName")
    val iconName: String? = null,
    @SerializedName("iconPath")
    val iconPath: String? = null,
    @SerializedName("iconMimetype")
    val iconMimetype: String? = null,
    @SerializedName("symbolName")
    val symbolName: String? = null,
    @SerializedName("symbolPath")
    val symbolPath: String? = null,
    @SerializedName("symbolMimetype")
    val symbolMimetype: String? = null,
    @SerializedName("filterField")
    val filterField: String? = null,
) {
    val symbolUrl get() = if(symbolPath != null && symbolPath.isNotEmpty()) "$symbolPath${if(symbolPath.get(symbolPath.length-1) == '/') "" else "/" }$symbolName$symbolMimetype" else null
    val iconUrl get() = if(iconPath != null && iconPath.isNotEmpty()) "$iconPath${if(iconPath.get(iconPath.length-1) == '/') "" else "/" }$iconName$iconMimetype" else null

    val isBoolean get() = value.representsBoolean()
}