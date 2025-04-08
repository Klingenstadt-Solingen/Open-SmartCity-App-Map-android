package de.osca.android.essentials.presentation.component.design

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import de.osca.android.essentials.R

@Composable
fun PoiCategoryElement(
    masterDesignArgs: MasterDesignArgs,
    moduleDesignArgs: ModuleDesignArgs,
    text: String = "",
    @DrawableRes icon: Int = -1,
    imageUrl: String? = null,
    iconSize: Dp = 75.dp,
    @ColorRes iconTint: Int = -1,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if((moduleDesignArgs.mConstrainHeight ?: masterDesignArgs.mConstrainHeight) > 0.dp)
            Modifier
                .height(moduleDesignArgs.mConstrainHeight ?: masterDesignArgs.mConstrainHeight)
                .then(modifier)
        else
            Modifier
                .then(modifier)
    ) {
        Card(
            shape = RoundedCornerShape(moduleDesignArgs.mShapeCard ?: masterDesignArgs.mShapeCard),
            elevation = moduleDesignArgs.mCardElevation ?: masterDesignArgs.mCardElevation,
            backgroundColor = moduleDesignArgs.mCardBackColor ?: masterDesignArgs.mCardBackColor,
            modifier = if(onClick != null)
                Modifier
                    .fillMaxSize()
                    .clickable {
                        onClick()
                    }
            else
                Modifier
                    .fillMaxSize()
        ) {
            Box(modifier = Modifier
                .padding(moduleDesignArgs.mContentPaddingForMiniCards ?: masterDesignArgs.mContentPaddingForMiniCards)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = if(icon >= 0)
                            painterResource(id = icon)
                        else if(imageUrl != null)
                            rememberAsyncImagePainter(imageUrl)
                        else
                            painterResource(id = R.drawable.ic_circle),
                        tint = if(iconTint >= 0) colorResource(id = iconTint) else (moduleDesignArgs.mButtonBackgroundColor ?: masterDesignArgs.mButtonBackgroundColor),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                    )

                    Text(
                        text = text,
                        color = moduleDesignArgs.mCardTextColor ?: masterDesignArgs.mCardTextColor,
                        textAlign = TextAlign.Center,
                        style = masterDesignArgs.subtitleTextStyle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}