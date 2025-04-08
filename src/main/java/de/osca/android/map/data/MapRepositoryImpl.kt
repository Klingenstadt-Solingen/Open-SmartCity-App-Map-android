package de.osca.android.map.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.osca.android.map.domain.boundary.MapRepository
import de.osca.android.map.domain.entity.*
import de.osca.android.networkservice.utils.RequestHandler
import java.lang.Exception
import java.net.URL
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapApiService: MapApiService,
    private val requestHandler: RequestHandler,
) : MapRepository {
    override suspend fun getCategoryIcon(url: String): Bitmap? {
        return try {
            val url = URL(url)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (ex: Exception) {
            getPlaceholderIcon()
        }
    }

    override suspend fun getPOIs(skip: Int, limit: Int): List<POI> {
        return requestHandler.makeRequest {
            mapApiService.getPoi(skip = skip, limit = limit)
        } ?: emptyList()
    }

    override suspend fun getAllPOIs(): List<POI> {
        return requestHandler.makeRequest {
            mapApiService.getAllPOIs()
        } ?: emptyList()
    }

    override suspend fun getPOIsByLocation(poiNearByRequest: POINearByRequest): List<POI> {
        return requestHandler.makeRequest {
            mapApiService.getPOIsByLocation(poiNearByRequest)
        } ?: emptyList()
    }

    override suspend fun getPoiById(poiId: String): POI? {
        return requestHandler.makeRequest {
            mapApiService.getPoiById(poiId = poiId)
        }
    }

    override suspend fun getSearchPOIs(search: SearchBody): List<POI> {
        return requestHandler.makeRequest {
            mapApiService.getSearchPoi(search = search)
        } ?: emptyList()
    }

    override suspend fun getPOIsByCat(skip: Int, limit: Int, poiCategory: POICategory): List<POI> {
        val poiFilter = POIFiltered(poiCategory.sourceId ?: "", emptyList())
        return requestHandler.makeRequest {
            mapApiService.getPoiByFilter(poiFilter)
        }?.items ?: emptyList()
    }

    override suspend fun getPOICategories(): List<POICategory> {
        return requestHandler.makeRequest(mapApiService::getMapCategories) ?: emptyList()
    }

    override suspend fun getFilterCategories(poiFilter: POIFilter): List<FilterResult> {
        return requestHandler.makeRequest {
            mapApiService.getFilterByCategory(poiFilter)
        } ?: emptyList()
    }

    override suspend fun getPOIsForFilter(filter: POIFiltered): POIFilterWrapper? {
        return requestHandler.makeRequest {
            mapApiService.getPoiByFilter(filter)
        }
    }

    private fun getPlaceholderIcon(): Bitmap? {
        return try {
            val placeholderUrl =
                URL("https://icons.iconarchive.com/icons/paomedia/small-n-flat/128/map-marker-icon.png")
            BitmapFactory.decodeStream(placeholderUrl.openConnection().getInputStream())
        } catch (ex: Exception) {
            null
        }
    }
}