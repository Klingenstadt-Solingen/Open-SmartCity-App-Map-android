package de.osca.android.map.presentation

import android.content.Context
import android.graphics.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.scale
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import de.osca.android.essentials.domain.entity.BooleanLiteralType
import de.osca.android.essentials.presentation.base.BaseViewModel
import de.osca.android.essentials.utils.extensions.*
import de.osca.android.essentials.utils.strings.EssentialsStrings
import de.osca.android.map.R
import de.osca.android.map.domain.boundary.MapRepository
import de.osca.android.map.domain.entity.*
import de.osca.android.map.domain.entity.poi_details.POIDetailValue
import de.osca.android.map.domain.entity.poi_details.POITwoPairValue
import de.osca.android.map.domain.entity.poi_details.POITwoPairValueType
import de.osca.android.map.presentation.args.MapDesignArgs
import de.osca.android.map.presentation.components.MapBottomSheetState
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val mapDesignArgs: MapDesignArgs,
    private val repository: MapRepository,
    private val essentialsStrings: EssentialsStrings
) : BaseViewModel() {
    val isPoiLoadFinished = mutableStateOf(true)
    val poiList = mutableStateListOf<POI>()
    val searchPoiList = mutableStateListOf<POI>()
    val selectedCategory = mutableStateOf<POICategory?>(null)
    val poiCategories = mutableStateListOf<POICategory>()
    val bottomSheetContentState = mutableStateOf<MapBottomSheetState>(MapBottomSheetState.MainMenu)
    val googleMap = mutableStateOf<GoogleMap?>(null)
    var wasLoadedFirstTime = false
    var reloadBlocker = false
    val tempLatLngPoints = mutableStateListOf<LatLng>()
    val markerPOIs = mutableStateListOf<MarkerOptions>()
    val allMarkerPOIs = mutableStateListOf<MarkerOptions>()
    val filterCategories = mutableStateListOf<FilterResult>()
    val listCount = mutableStateOf(0)
    var wasSearching: String? = null

    /**
     * call this function to initialize categories and all POIs.
     * it sets the screen to loading, fetches the data from parse and when
     * it finished successful then displays the content and when an error
     * occurred it displays an message screen
     */
    fun initializePOIAndCategory() { // READY
        viewModelScope.launch {
            wrapperState.loading()
            async {
                fetchPoiCategories()
            }
        }
    }

    /**
     * fetches all categories from parse and when successfully loaded then
     * displays the content
     */
    fun fetchPoiCategories(): Job = launchDataLoad { // READY
        val result = repository.getPOICategories()
        poiCategories.resetWith(result)
        poiCategories.sortBy { it.position }
        poiCategories.resetWith(poiCategories.filter { it.showCategory == "true" })

        wrapperState.displayContent()
    }

    fun fetchSamplePOIs(
        count: Int? = 3,
        distance: Int = 1000,
        context: Context,
        latLng: LatLng
    ) { // READY
        if (count != null) {
            launchDataLoad {
                // categories
                fetchPoiCategories()

                // load POIs
                poiList.clear()
                markerPOIs.clear()
                allMarkerPOIs.clear()

                val poiNearByRequest = POINearByRequest(
                    lat = latLng.latitude,
                    lon = latLng.longitude,
                    distance = distance,
                    limit = count,
                    random = true
                )

                poiList.resetWith(repository.getPOIsByLocation(poiNearByRequest))

                // setCategories
                setCategories(true)

            }
        }
    }

    fun fetchAllPOIs(): Job = launchDataLoad { // READY
        val result = repository.getAllPOIs()

        poiList.clear()
        markerPOIs.clear()
        allMarkerPOIs.clear()

        poiList.addAll(result)

        setCategories()
    }

    fun findInAllPOIs(searchText: String, isSearching: Boolean = false): Job = launchDataLoad {
        if (searchText.length > 3) {
            isPoiLoadFinished.value = false

            val searchBody = SearchBody(query = searchText)
            val result = repository.getSearchPOIs(searchBody)

            wasSearching = if (isSearching) {
                searchPoiList.clear()
                searchPoiList.addAll(result)

                searchText
            } else {
                null
            }

            searchPoiList.forEach { sPoi ->
                sPoi.category = poiCategories.firstOrNull { it.sourceId == sPoi.poiCategory }
            }
        }
    }

    /**
     * fetches all POIs from parse and when successfully loaded then
     * displays the content
     */
    fun getPOIsForFilter(
        selectedCategory: String,
        reset: Boolean,
        callback: () -> Unit = { }
    ): Job = launchDataLoad { // READY
        isPoiLoadFinished.value = false

        wasSearching = null

        poiList.clear()
        markerPOIs.clear()
        allMarkerPOIs.clear()
        listCount.value = 0

        val filterObjectList = mutableStateListOf<FilterForPOI>()
        filterCategories.forEach { filter ->
            filter.selectedFilters.forEach {
                filterObjectList.add(
                    FilterForPOI(
                        field = filter.field,
                        value = it
                    )
                )
            }
        }

        if (reset) {
            filterObjectList.clear()
            filterCategories.forEach { filter ->
                filter.selectedFilters.clear()
            }
        }

        val poiFiltered = POIFiltered(
            category = selectedCategory,
            filter = filterObjectList
        )

        val result = repository.getPOIsForFilter(poiFiltered)?.items ?: emptyList()

        poiList.resetWith(result)

        listCount.value = poiList.size

        setCategories(callback = callback)
    }

    fun setUserLocationMarker(context: Context, userLocation: LatLng): MarkerOptions { // READY
        val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.googlemapbluedot)
        val bmpScaled = bmp.scale(100, 100, false)
        val icon = BitmapDescriptorFactory.fromBitmap(bmpScaled)
        val userMarkerOption = MarkerOptions()
        userMarkerOption.position(userLocation)
        userMarkerOption.icon(icon)

        return userMarkerOption
    }

    /**
     * set icon from POI for this item
     * @property poi the POI which contains the icon
     * @property item the item which gets the icon assigned
     */
    fun setMarkerIcon(poi: POI, markerOption: MarkerOptions) { // READY
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var icon: String? = null
                val thematicView = poi.category?.defaultThematicView
                icon = if (thematicView != null && thematicView.isNotEmpty()) {
                    poi.details.firstOrNull { it.filterField == thematicView }?.symbolUrl
                        ?: poi.category?.symbolUrl
                } else {
                    poi.category?.symbolUrl
                }
                icon?.let {
                    repository.getCategoryIcon(it)?.let { bmp ->
                        bmp.config?.let { config ->
                            val bmpCopy =
                                bmp.copy(config, true).scale(bmp.width * 2, bmp.height * 2)

                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(bmpCopy))
                            markerPOIs.add(markerOption)
                            allMarkerPOIs.add(markerOption)
                        }
                    }
                }
            }
        }
    }

    /**
     * assign each POI the corresponding category
     */
    fun setCategories(isWidget: Boolean = false, callback: () -> Unit = { }) { // READY
        poiList.forEach { poi ->
            poi.category = poiCategories.firstOrNull { it.sourceId == poi.poiCategory }

            addMarkerFor(poi)
        }

        callback()

        // zoom to bounds
        if (!isWidget) {
            googleMap.value?.animateCamera(
                getBounds(poiList.map { it.geoPoint }, googleMap.value?.cameraPosition?.target!!)
            )
        }

        isPoiLoadFinished.value = true

        filter()
    }

    /**
     * logic to create marker and add to map
     */
    fun addMarkerFor(poi: POI) { // READY
        val markerOptions = MarkerOptions()
        markerOptions.title(poi.name)
        markerOptions.position(LatLng(poi.geoPoint.latitude, poi.geoPoint.longitude))

        setMarkerIcon(poi, markerOptions)
    }

    /**
     * set visibility of the markers based on the selected category
     * @property category the category which should be visible
     */
    fun filterMarkersByCategory(
        category: POICategory,
        reset: Boolean = false,
        callback: () -> Unit = { }
    ) { // READY
        getPOIsForFilter(category.sourceId ?: "", reset, callback)
    }

    /**
     * show the details of the selected item
     * @property item contains the details which should be displayed
     */
    fun onMarkerClicked(marker: Marker) { // READY
        poiList.firstOrNull { it.name == marker.title }?.let {
            showPOIDetails(it, wasSearching)
        }

        tempLatLngPoints.resetWith(
            poiList.map { it.geoPoint.toLatLng() }
        )
    }

    fun showPOIDetails(poi: POI, fromSearching: String? = null) { // READY
        wasSearching = fromSearching
        bottomSheetContentState.value = MapBottomSheetState.ShowDetails(poi)
    }

    /**
     * shows the main menu
     */
    fun showMainMenu() { // READY
        bottomSheetContentState.value = MapBottomSheetState.MainMenu

        if (wasSearching != null) {
            findInAllPOIs(wasSearching!!, true)
        }
    }

    fun showCategoryFilter(category: POICategory) { // READY
        bottomSheetContentState.value = MapBottomSheetState.ShowCategoryFilter(category)
    }

    fun showFullList(category: POICategory) { // READY
        bottomSheetContentState.value = MapBottomSheetState.ShowFullList(category)
    }

    fun fetchOnePOI(initializePoi: String): Job = launchDataLoad {
        val result = repository.getPoiById(initializePoi) // only one with objectId

        if (result != null) {
            poiList.clear()
            markerPOIs.clear()
            allMarkerPOIs.clear()

            poiList.add(result)

            setCategories()

            showPOIDetails(result)
        }
    }

    /**
     * initialize cluster manager and start adding the markers for the POIs
     */
    fun setClusterManagerAndPOIs(
        context: Context,
        location: LatLng,
        userLocationFound: Boolean,
        initializePoi: String? = null
    ) { // READY
        if (!wasLoadedFirstTime) {
            markerPOIs.clear()
            allMarkerPOIs.clear()
            isPoiLoadFinished.value = true

            if (selectedCategory.value != null && poiCategories.isNotEmpty()) {
                val cat = poiCategories.first { it.sourceId == selectedCategory.value?.sourceId }

                getFilterCategories(cat.sourceId.toString()) {
                    if (mapDesignArgs.skipFilterWhenEmpty && it) {
                        showFullList(cat)
                    } else {
                        showCategoryFilter(cat)
                    }
                }
                filterMarkersByCategory(cat)
            } else if (initializePoi != null) {
                fetchOnePOI(initializePoi)
            } else {
                if (mapDesignArgs.showDefaultAll) {
                    fetchAllPOIs()
                }
            }

            if (userLocationFound) {
                setUserLocationMarker(context, location)
            }

            googleMap.value?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    mapDesignArgs.mapInitialZoomLevel
                )
            )

            wasLoadedFirstTime = true
        }
    }

    /**
     * search through all POIs and set visibility of matching markers
     * @property searchText the text which the POI must contain
     */
    fun search(searchText: String): Job = launchDataLoad { // READY
        if (searchText.length >= 2) {
            val filtered =
                allMarkerPOIs.filter { it.title?.contains(searchText.lowercase()) ?: false }

            markerPOIs.resetWith(filtered)
        } else if (searchText.isBlank()) {
            showAllMarkers()
        }
    }

    fun searchPOIList(searchText: String): List<POI> { // READY
        return if (searchText.length >= 2) {
            poiList.filter { it.searchContents.contains(searchText.lowercase()) }
        } else {
            poiList
        }
    }

    /**
     * show all markers
     */
    fun showAllMarkers() { // READY
        markerPOIs.resetWith(allMarkerPOIs)

        //filter()
    }

    fun filter() { // READY
        val filtered = allMarkerPOIs.filter { it.isVisible } // TODO: visible?
        markerPOIs.resetWith(filtered)
    }

    fun getDetails(details: List<POIDetail>): List<POITwoPairValue> {
        val list = mutableListOf<POITwoPairValue>()

        for (detail in details) {
            val type = getPOITwoPairValueType(detail.type.toString(), detail.title.toString())
            val obj = POITwoPairValue(
                majorTitle = detail.majorTitle,
                title = detail.title.toString(),
                subtitle = detail.subtitle.toString(),
                type = type,
                iconUrl = detail.iconUrl
            )
            obj.addValue(
                POIDetailValue(
                    subtitle = obj.majorTitle
                        ?: if (type == POITwoPairValueType.OPEN_HOURS) obj.subtitle else obj.title,
                    value = when (detail.isBoolean) {
                        true -> essentialsStrings.localizedBoolean(
                            value = detail.value.toBoolean() ?: false,
                            type = BooleanLiteralType.YES_NO
                        )

                        false -> detail.value ?: "---"
                    }
                )
            )

            addToList(obj, list)
        }

        return list
    }

    fun addToList(item: POITwoPairValue, list: MutableList<POITwoPairValue>) {
        val indexOfFound = list.indexOfFirst {
            (if (item.majorTitle != null) it.majorTitle else it.title) == (item.majorTitle
                ?: item.title)
        }

        if (indexOfFound >= 0) {
            list[indexOfFound].addValue(item.values.first())
        } else {
            list.add(item)
        }
    }

    fun getPOITwoPairValueType(type: String, title: String): POITwoPairValueType {
        return when (type) {
            "tel" -> POITwoPairValueType.PHONE
            "mail" -> POITwoPairValueType.EMAIL
            "url" -> POITwoPairValueType.URL
            else -> {
                when (title) {
                    "Öffnungszeiten" -> POITwoPairValueType.OPEN_HOURS
                    else -> POITwoPairValueType.TEXT
                }
            }
        }
    }

    fun getFilterCategories(categoryId: String, onResult: (skip: Boolean) -> Unit): Job =
        launchDataLoad(onFailure = { filterCategories.clear() }) {
            val poiFilter = POIFilter(categoryId)
            val result = repository.getFilterCategories(poiFilter)
            filterCategories.resetWith(result)

            onResult(filterCategories.isEmpty())
        }
}




