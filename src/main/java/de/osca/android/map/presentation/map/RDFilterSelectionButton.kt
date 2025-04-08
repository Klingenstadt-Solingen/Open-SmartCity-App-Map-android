package de.osca.android.map.presentation.map

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs

@Composable
fun RDFilterSelectionButton(
    isReady: MutableState<Boolean> = mutableStateOf(false),
    moduleDesignArgs: ModuleDesignArgs,
    masterDesignArgs: MasterDesignArgs,
    initialSelected: Boolean = false,
    filterText: String? = null,
    onSelect: ((selected: Boolean) -> Unit)? = null
) {
    val isSelected = remember {
        mutableStateOf(initialSelected)
    }

    Card(
        elevation = 0.dp,
        shape = RoundedCornerShape(50),
        backgroundColor = if(isReady.value)
                (if(isSelected.value)
                moduleDesignArgs.mButtonBackgroundColor ?: masterDesignArgs.mButtonBackgroundColor
            else
                moduleDesignArgs.mCardBackColor ?: masterDesignArgs.mCardBackColor)
        else
            moduleDesignArgs.mHintTextColor ?: masterDesignArgs.mHintTextColor,
        modifier = Modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(50.dp))
            .clickable {
                if(isReady.value) {
                    isSelected.value = !isSelected.value
                    if (onSelect != null) {
                        onSelect(isSelected.value)
                    }
                }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = filterText.toString(),
                style = masterDesignArgs.normalTextStyle,
                color = if (isSelected.value)
                    moduleDesignArgs.mButtonContentColor ?: masterDesignArgs.mButtonContentColor
                else
                    moduleDesignArgs.mButtonBackgroundColor ?: masterDesignArgs.mButtonBackgroundColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .border(
                        if(isSelected.value) 0.dp else 2.dp,
                        moduleDesignArgs.mButtonBackgroundColor ?: masterDesignArgs.mButtonBackgroundColor,
                        RoundedCornerShape(50)
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }
    }
}