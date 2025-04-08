package de.osca.android.map.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import de.osca.android.essentials.domain.entity.navigation.NavigationItem
import de.osca.android.map.R

sealed class MapNavItems {

    enum class PoiType {
        Detail,
        Categories
    }

    object MapNavItem : NavigationItem(
        title = R.string.map_title,
        route = "$POI_ROUTE/{$ARG_TYPE}?$ARG_POI_ID={$ARG_POI_ID}",
        arguments = listOf(
            navArgument(ARG_POI_ID) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(ARG_TYPE) {
                type = NavType.EnumType(type = PoiType::class.java)
                defaultValue = PoiType.Categories
            }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "solingen://$POI_ROUTE/{$ARG_TYPE}?$ARG_POI_ID={$ARG_POI_ID}"
            },
            navDeepLink {
                uriPattern = "solingen://$POI_ROUTE"
            },
        ),
        icon = R.drawable.ic_circle
    )

    companion object {
        const val ARG_POI_ID = "object"
        const val ARG_TYPE = "type"

        const val POI_ROUTE = "poi"

        fun getMapCategoryRoute(id: String?): String {
            val routeBuilder = StringBuilder()
            routeBuilder.append("$POI_ROUTE/${PoiType.Categories}")
            if (!id.isNullOrEmpty()) {
                routeBuilder.append("?$ARG_POI_ID=$id")
            }
            return routeBuilder.toString()
        }
    }
}