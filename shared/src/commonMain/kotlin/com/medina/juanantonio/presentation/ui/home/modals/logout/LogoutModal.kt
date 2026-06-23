package com.medina.juanantonio.presentation.ui.home.modals.logout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun LogoutModal(
    onPositiveButtonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Logout Strava",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Are you sure you want to logout? This will remove your Strava Account and delete all your contracts and activities from this device.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )


        Button(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            onClick = onPositiveButtonClick,
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Logout"
            )
        }
    }
}

@Preview
@Composable
fun PreviewLogoutModal() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            LogoutModal()
        }
    }
}
