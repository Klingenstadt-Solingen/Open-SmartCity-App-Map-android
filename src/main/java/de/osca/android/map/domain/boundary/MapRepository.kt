package de.osca.android.map.domain.boundary

import android.graphics.Bitmap
import de.osca.android.essentials.utils.constants.DEFAULT_API_LIST_RESPONSE_LIMIT
import de.osca.android.map.domain.entity.*

interface MapRepository {
    suspend fun getCategoryIcon(url: String): Bitmap?
    suspend fun getPOIs(skip: Int = 0, limit: Int = DEFAULT_API_LIST_RESPONSE_LIMIT): List<POI>
    suspend fun getAllPOIs(): List<POI>
    suspend fun getPOIsByLocation(poiNearByRequest: POINearByRequest): List<POI>
    suspend fun getPoiById(poiId: String): POI?
    suspend fun getSearchPOIs(search: SearchBody): List<POI>
    suspend fun getPOIsByCat(skip: Int = 0, limit: Int = DEFAULT_API_LIST_RESPONSE_LIMIT, poiCategory: POICategory): List<POI>
    suspend fun getPOICategories(): List<POICategory>

    suspend fun getFilterCategories(poiFilter: POIFilter): List<FilterResult>
    suspend fun getPOIsForFilter(filter: POIFiltered): POIFilterWrapper?
}