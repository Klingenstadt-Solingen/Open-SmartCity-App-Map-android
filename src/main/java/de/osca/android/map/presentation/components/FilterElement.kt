package de.osca.android.map.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.essentials.presentation.base.BaseViewModel
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.BaseDropDown
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.map.domain.entity.FilterResult

@Composable
fun FilterElement(
    masterDesignArgs: MasterDesignArgs,
    moduleDesignArgs: ModuleDesignArgs,
    filterResult: FilterResult,
    selectedIndexChanged: (selectedIndex: Int) -> Unit = { }
) {
    BaseCardContainer(
        masterDesignArgs = masterDesignArgs,
        moduleDesignArgs = moduleDesignArgs
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = filterResult.title,
                style = masterDesignArgs.bodyTextStyle,
                color = masterDesignArgs.mCardTextColor,
                modifier = Modifier
                    .weight(1f)
            )

            BaseDropDown(
                displayTexts = filterResult.values,
                onSelectedItemChanged = { selectedIndex ->
                    selectedIndexChanged(selectedIndex)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp),
                verticalPadding = 2.dp,
                horizontalPadding = 12.dp,
                reset = true,
                masterDesignArgs = masterDesignArgs,
                moduleDesignArgs = moduleDesignArgs
            )
        }
    }
}