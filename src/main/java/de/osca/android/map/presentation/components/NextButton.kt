package de.osca.android.map.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    label: String = "Next",
    onClick: () -> Unit,
    iconTint: Color,
    textColor: Color,
    textStyle: TextStyle,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(start = 0.dp, end = 0.dp),
    ) {
        Text(
            text = label,
            style = textStyle,
            color = textColor,
        )

        Spacer(
            modifier =
                Modifier
                    .width(8.dp),
        )

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "backArrowIcon",
            tint = iconTint,
            modifier =
                Modifier
                    .size(25.dp),
        )
    }
}
