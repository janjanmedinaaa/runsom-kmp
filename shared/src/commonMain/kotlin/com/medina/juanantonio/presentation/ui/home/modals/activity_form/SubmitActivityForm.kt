package com.medina.juanantonio.presentation.ui.home.modals.activity_form

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import com.medina.juanantonio.domain.models.network.strava.StravaActivity
import com.medina.juanantonio.presentation.composables.LoadingOverlay
import com.medina.juanantonio.presentation.ui.home.composables.ActivityDisplayItem
import com.medina.juanantonio.presentation.ui.theme.RunsomTheme
import com.medina.juanantonio.presentation.utils.formatAmount
import compose.icons.FeatherIcons
import compose.icons.feathericons.Info
import org.koin.compose.koinInject

@Composable
fun SubmitActivityModal(
    viewModel: SubmitActivityViewModel = koinInject(),
    contract: ContractWithActivities,
    onSubmit: (Boolean) -> Unit = {}
) {
    val isActivityListLoading by viewModel.activityListLoadingState.collectAsStateWithLifecycle()
    val activityList by viewModel.stravaActivityList.collectAsStateWithLifecycle()
    var calculatedReward by viewModel.calculatedReward

    val currentKmCount = contract.activities.sumOf { it.wholeKilometer }
    val remainingKmAvailable = contract.contract.distance - currentKmCount

    val loading by viewModel.isLoadingState

    LaunchedEffect(contract) {
        viewModel.getStravaActivities(
            contractWithActivities = contract,
            maxRedeemableKm = remainingKmAvailable
        )
    }

    LaunchedEffect(viewModel.selectedActivityIndex.value) {
        val currIndex = viewModel.selectedActivityIndex.value
        calculatedReward = activityList.getOrNull(currIndex)?.let {
            it.maxRedeemableKm * contract.contract.pricePerKm
        } ?: 0
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        SubmitActivityForm(
            activityType = contract.contract.activityType,
            activityList = activityList,
            selectedActivityIndex = viewModel.selectedActivityIndex,
            isActivityListLoading = isActivityListLoading,
            calculatedReward = calculatedReward,
            onSubmit = {
                viewModel.submitActivity(
                    contractId = contract.contract.id,
                    onFinish = onSubmit
                )
            }
        )

        LoadingOverlay(
            visible = loading,
            backgroundAlpha = 0F
        )
    }
}

@Composable
fun SubmitActivityForm(
    activityType: String,
    activityList: List<StravaActivity>,
    selectedActivityIndex: MutableIntState,
    isActivityListLoading: Boolean = false,
    calculatedReward: Int,
    onSubmit: () -> Unit = {}
) {
    var selectedIndex by selectedActivityIndex

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Record Activity",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Text(
            text = "Choose an activity to apply toward your contract.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Recent $activityType Activities:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )

        AnimatedContent(
            targetState = isActivityListLoading
        ) { isLoading ->
            if (isLoading) {
                ActivitiesLoadingSpinner()
            } else {
                ActivityListDisplay(
                    items = activityList,
                    selectedIndex = selectedIndex
                ) {
                    selectedIndex = it
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "₱${calculatedReward.toString().formatAmount()}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Text(
            text = "Amount is computed based on the maximum kilometer you finish multiplied by Price per Kilometer.",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )

        Button(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            onClick = onSubmit,
            enabled = calculatedReward != 0 && selectedIndex != -1
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Submit Activity"
            )
        }
    }
}

@Composable
fun ActivitiesLoadingSpinner() {
    Box(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(48.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun ActivityListDisplay(
    items: List<StravaActivity>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit = {}
) {
    if (items.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(48.dp).padding(bottom = 8.dp),
                imageVector = FeatherIcons.Info,
                tint = Color.Black,
                contentDescription = null
            )

            Text(
                text = "No Activities Found",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            itemsIndexed(
                items = items,
                key = { _, item -> item.id }
            ) { index, activity ->
                ActivityDisplayItem(
                    modifier = Modifier.animateItem(),
                    selected = selectedIndex == index,
                    activity = activity,
                    onClick = {
                        onItemClick(index)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSubmitActivityForm() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            SubmitActivityForm(
                activityType = "Run",
                activityList = emptyList(),
                selectedActivityIndex = mutableIntStateOf(2),
                calculatedReward = 0,
                isActivityListLoading = false
            )
        }
    }
}

@Preview
@Composable
fun PreviewLoadingSubmitActivityForm() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            SubmitActivityForm(
                activityType = "Swim",
                activityList = emptyList(),
                selectedActivityIndex = mutableIntStateOf(1),
                calculatedReward = 0,
                isActivityListLoading = true
            )
        }
    }
}

@Preview
@Composable
fun PreviewFilledSubmitActivityForm() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            SubmitActivityForm(
                activityType = "Run",
                activityList = listOf(
                    StravaActivity(
                        id = "",
                        name = "Morning Run",
                        distance = 5000.12F,
                        type = "Swim",
                        startDate = "",
                        startDateLocal = "",
                        timezone = "",
                        utcOffset = 1F
                    )
                ),
                selectedActivityIndex = mutableIntStateOf(2),
                calculatedReward = 0,
                isActivityListLoading = false
            )
        }
    }
}
