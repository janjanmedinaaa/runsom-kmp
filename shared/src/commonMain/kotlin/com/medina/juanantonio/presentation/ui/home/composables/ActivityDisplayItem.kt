package com.medina.juanantonio.presentation.ui.home.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medina.juanantonio.domain.models.network.strava.StravaActivity
import com.medina.juanantonio.presentation.utils.formatDateTime

@Composable
fun ActivityDisplayItem(
    modifier: Modifier = Modifier,
    activity: StravaActivity,
    selected: Boolean,
    onClick: () -> Unit = {}
) {
    val cardShape = RoundedCornerShape(12.dp)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .then(
                if (selected) {
                    Modifier.border(
                        border = BorderStroke(2.dp, Color(0xFF181818)),
                        shape = cardShape
                    )
                } else Modifier
            ),
        shape = cardShape,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFFAFAFA))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1F).padding(end = 8.dp),
                    text = activity.name,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    modifier = Modifier
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(50))
                        .background(
                            color = Color(0xFFEEEEEE),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    text = "Claimable Distance: ${activity.maxRedeemableKm}KM",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row {
                    Text(
                        text = "Date: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = activity.startDate.formatDateTime(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Light
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Row {
                    Text(
                        text = "Total Distance: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = activity.distanceInKm,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewActivityDisplayItem() {
    ActivityDisplayItem(
        selected = false,
        activity = StravaActivity(
            id = "",
            name = "Sample Activity",
            distance = 1000.0F,
            type = "Swim",
            startDate = "",
            startDateLocal = "",
            timezone = "",
            utcOffset = -1F
        )
    )
}
