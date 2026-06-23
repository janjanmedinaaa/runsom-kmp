package com.medina.juanantonio.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun IconButtonWithTextLabel(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String,
    enabled: Boolean = true,
    contentDescription: String? = null,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier.size(48.dp),
            onClick = onClick,
            enabled = enabled,
            shapes = IconButtonShapes(shape = RoundedCornerShape(24.dp)),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(0xFFF1F1F1)
            )
        ) {
            Icon(
                imageVector = imageVector, contentDescription = contentDescription
            )
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}