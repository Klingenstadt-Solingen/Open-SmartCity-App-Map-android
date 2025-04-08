package de.osca.android.map.data

import de.osca.android.essentials.utils.annotations.UnwrappedResponse
import de.osca.android.map.domain.entity.*
import retrofit2.Response
import retrofit2.http.*

interface MapApiService {

    @GET("classes/POICategory")
    suspend fun getMapCategories(): Response<List<POICategory>>

    @GET("classes/POI")
    suspend fun getPoi(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<POI>>

    @POST("functions/poi-nearby")
    suspend fun getPOIsByLocation(@Body poiNearByRequest: POINearByRequest): Response<List<POI>>

    @POST("functions/poi-all")
    suspend fun getAllPOIs(): Response<List<POI>>

    @POST("functions/poi-filter")
    suspend fun getFilterByCategory(@Body poiFilter: POIFilter): Response<List<FilterResult>>

    @POST("functions/elastic-search")
    suspend fun getSearchPoi(@Body search: SearchBody): Response<List<POI>>

    @POST("functions/poi-filtered")
    suspend fun getPoiByFilter(@Body poiFiltered: POIFiltered): Response<POIFilterWrapper>

    @GET("classes/POI/{poiId}")
    @UnwrappedResponse
    suspend fun getPoiById(@Path(value = "poiId", encoded = true) poiId: String): Response<POI?>
}