package de.osca.android.map.domain.entity.poi_details

data class POITwoPairValue(
    val majorTitle: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val values: MutableList<POIDetailValue> = mutableListOf(),
    val type: POITwoPairValueType,
    val iconUrl: String? = null
) {
    fun addValue(subtitle: String, value: String) {
        values.add(POIDetailValue(subtitle, value))
    }

    fun addValue(detailValue: POIDetailValue) {
        values.add(detailValue)
    }
}