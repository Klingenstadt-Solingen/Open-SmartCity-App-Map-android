package de.osca.android.map.domain.entity

import com.google.gson.annotations.SerializedName

data class POIImage(
    @SerializedName("imageName")
    val imageName: String? = null,
    @SerializedName("imagePath")
    val imagePath: String? = null,
    @SerializedName("imageMimetype")
    val imageMimetype: String? = null
){
    val imageUrl get() = "$imagePath${if(imagePath?.get(imagePath.length-1) == '/') "" else "/" }$imageName$imageMimetype"
}