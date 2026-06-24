package com.medina.juanantonio.presentation.ui.home.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medina.juanantonio.domain.models.entities.Contract
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import com.medina.juanantonio.presentation.utils.TimeUtils.formatRemainingTime
import kotlin.time.Clock

@Composable
fun ContractDisplayItem(
    modifier: Modifier = Modifier,
    contract: ContractWithActivities,
    enabled: Boolean = true,
    onItemClick: () -> Unit = {},
    onItemLongClick: () -> Unit = {},
) {
    val totalAmount = contract.contract.pricePerKm * contract.contract.distance
    val currentKmCount = contract.activities.sumOf { it.wholeKilometer }
    val currentAmount = currentKmCount * contract.contract.pricePerKm
    val progress = (currentAmount.toFloat() / totalAmount.toFloat())

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 500
        ),
        label = "progress"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (enabled) onItemLongClick()
                    },
                    onTap = {
                        if (enabled) onItemClick()
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .background(color = Color(0xFFFAFAFA))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = contract.contract.title.ifBlank {
                        "${contract.contract.activityType} for ${contract.contract.distance}KM"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "₱${contract.contract.pricePerKm}.00/KM",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Minimum of ${contract.contract.minimumDistanceKm}km per activity",
                    style = MaterialTheme.typography.bodySmall
                )

                if (contract.contract.maximumActivities != -1) {
                    Text(
                        text = "${contract.activities.size}/${contract.contract.maximumActivities} Activities Submitted",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "₱${minOf(currentAmount, totalAmount)}.00",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = "₱$totalAmount.00",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
                        trackColor = Color(0xFFEEEEEE),
                        color = Color(0xFF00E676)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (contract.contract.deadline != -1L) {
                                "${
                                    formatRemainingTime(
                                        Clock.System.now().toEpochMilliseconds(),
                                        contract.contract.deadline
                                    )
                                } left"
                            } else "No Time Limit",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = if (currentAmount == 0) {
                                "Goal: ${contract.contract.distance}KM"
                            } else {
                                "Remaining: ${contract.contract.distance - currentKmCount}KM left"
                            },
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewContractDisplayItemLayout() {
    ContractDisplayItem(
        contract = ContractWithActivities(
            contract = Contract(
                activityType = "Run",
                distance = 100,
                pricePerKm = 50
            ),
            emptyList()
        )
    )
}

@Preview
@Composable
fun PreviewCustomizeContractDisplayItemLayout() {
    ContractDisplayItem(
        contract = ContractWithActivities(
            contract = Contract(
                activityType = "Run",
                distance = 100,
                pricePerKm = 50,
                title = "Spartan 50K Run",
                maximumActivities = 3,
                minimumDistanceKm = 5
            ),
            emptyList()
        )
    )
}
