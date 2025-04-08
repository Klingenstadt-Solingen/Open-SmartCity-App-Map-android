package de.osca.android.map.domain.entity

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import de.osca.android.essentials.domain.entity.Coordinates

data class POI(
    @SerializedName("objectId")
    val objectId: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("zip")
    val zip: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("district")
    val district: String? = null,
    @SerializedName("poiCategory")
    val poiCategory: String? = null,
    @SerializedName("showUserGeneratedContent")
    val showUserGeneratedContent: Boolean = false,
    @SerializedName("showRouteTo")
    val showRouteTo: Boolean = false,
    @SerializedName("routeType")
    val routeType: Boolean? = null,
    @SerializedName("geopoint")
    val geoPoint: Coordinates = Coordinates(),
    @SerializedName("details")
    val details: List<POIDetail> = emptyList(),
    @SerializedName("images")
    val images: List<POIImage> = emptyList()
) {
    var category: POICategory? = null

    val searchContents get() = "$name $address $city $district $poiCategory".lowercase()

    fun getDistanceInMetersFrom(coordinates: LatLng): Float {
        val startPoint = Location(LOCATION_START_POINT).apply {
            latitude = coordinates.latitude
            longitude = coordinates.longitude
        }

        val destination = Location(LOCATION_DESTINATION).apply {
            latitude = geoPoint.latitude
            longitude = geoPoint.longitude
        }

        return startPoint.distanceTo(destination)
    }

    companion object {
        const val LOCATION_START_POINT = "LOCATION_START_POINT"
        const val LOCATION_DESTINATION = "LOCATION_DESTINATION"
    }
}