package com.medina.juanantonio.presentation.ui.home.modals.logout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medina.juanantonio.presentation.ui.theme.RunsomTheme
import com.medina.juanantonio.resources.Res
import com.medina.juanantonio.resources.rocket_graphic
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AlertBottomSheetModal(
    title: String,
    description: String,
    positiveButtonText: String,
    drawableResource: DrawableResource? = null,
    onPositiveButtonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (drawableResource != null) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(0.4F)
                        .aspectRatio(1F),
                    painter = painterResource(drawableResource),
                    contentDescription = ""
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(0.8F),
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(0.8F),
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Button(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            onClick = onPositiveButtonClick,
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = positiveButtonText
            )
        }
    }
}

@Preview
@Composable
fun PreviewAlertBottomSheetModal() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            AlertBottomSheetModal(
                title = "This is the Title",
                description = "This is the Description",
                drawableResource = Res.drawable.rocket_graphic,
                positiveButtonText = "Logout"
            )
        }
    }
}

@Preview
@Composable
fun PreviewNoDrawableAlertBottomSheetModal() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            AlertBottomSheetModal(
                title = "This is the Title",
                description = "This is the Description",
                positiveButtonText = "Logout"
            )
        }
    }
}
