package com.medina.juanantonio.presentation.ui.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medina.juanantonio.domain.models.network.RunsomChallenge
import com.medina.juanantonio.presentation.ui.theme.RunsomTheme
import com.medina.juanantonio.presentation.utils.TimeUtils.formatRemainingTime
import com.medina.juanantonio.presentation.utils.toMonthDayYear
import kotlin.time.Clock

@Composable
fun ChallengeDisplayItem(
    challenge: RunsomChallenge,
    modifier: Modifier = Modifier,
    onJoinClick: (RunsomChallenge) -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFFAFAFA))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 8.dp, end = 16.dp)
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = "${challenge.title} - ${challenge.distance}KM ${challenge.activityType}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = { onJoinClick(challenge) },
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "Join"
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Minimum of ${challenge.minimumDistanceKm}km per activity",
                    style = MaterialTheme.typography.bodySmall
                )

                if (challenge.maximumActivities != -1) {
                    Text(
                        text = "Max of ${challenge.maximumActivities} activities",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (challenge.hasValidDeadline) {
                        "${
                            formatRemainingTime(
                                Clock.System.now().toEpochMilliseconds(),
                                challenge.validUntil
                            )
                        } left"
                    } else if (challenge.dayLimit != -1) {
                        "${challenge.dayLimit} Day Limit"
                    } else {
                        "No Time Limit"
                    },
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = if (challenge.validUntil != -1L) {
                        "Valid Until ${challenge.validUntil.toMonthDayYear()}"
                    } else {
                        "No Expiration"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewChallengeDisplayItem() {
    RunsomTheme {
        ChallengeDisplayItem(
            challenge = RunsomChallenge(
                title = "Runsom 5k Challenge \uD83D\uDE80",
                description = "Runners are required to run 10 5KM in 1 Month. Anyone can you join.",
                activityType = "Swim",
                distance = 5,
                created = -1L,
                validUntil = -1L,
                minimumDistanceKm = 3,
                maximumActivities = 3,
                dayLimit = 5
            )
        )
    }
}
