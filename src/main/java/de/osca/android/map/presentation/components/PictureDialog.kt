package de.osca.android.map.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.map.domain.entity.POIImage
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureDialog(
    masterDesignArgs: MasterDesignArgs,
    moduleDesignArgs: ModuleDesignArgs,
    pictures: List<POIImage>,
    activePicture: POIImage,
    setShowDialog: (Boolean) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(initialPage = pictures.indexOf(activePicture) ?: 1){
        pictures.size
    }
    Dialog(
        onDismissRequest = {
            setShowDialog(false)
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)

    ) {
        Column(Modifier
            .fillMaxSize(1f),
            verticalArrangement = Arrangement.SpaceEvenly) {
            Row(Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f),
                horizontalArrangement = Arrangement.End) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                setShowDialog(false)
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .background(MaterialTheme.colors.background)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit"
                        )
                    }
            }
            Row(Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)) {
                HorizontalPager(state = pagerState,Modifier.fillMaxWidth()) { index ->
                    Image(
                        painter = rememberAsyncImagePainter(pictures[index].imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                rotationZ = rotation,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .transformable(state = state)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
            Row(Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f),
                horizontalArrangement = Arrangement.Center) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(100))
                        .background(MaterialTheme.colors.background)
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                scale = 1f
                                rotation = 0f
                                offset = Offset.Zero
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage.dec()
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Go back"
                        )
                    }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                scale = 1f
                                rotation = 0f
                                offset = Offset.Zero
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage.inc()
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Go forward"
                        )
                    }
                }
            }
        }
    }
}
