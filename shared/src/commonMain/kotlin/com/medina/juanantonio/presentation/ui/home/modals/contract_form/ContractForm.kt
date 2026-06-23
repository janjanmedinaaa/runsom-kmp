package com.medina.juanantonio.presentation.ui.home.modals.contract_form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medina.juanantonio.domain.ContractDataTypes
import com.medina.juanantonio.presentation.composables.LoadingOverlay
import com.medina.juanantonio.presentation.composables.SlideToActionButton
import com.medina.juanantonio.presentation.ui.theme.RunsomTheme
import com.medina.juanantonio.presentation.utils.formatAmount
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowRight
import org.koin.compose.koinInject

@Composable
fun ContractModal(
    viewModel: ContractFormViewModel = koinInject(),
    onSubmit: () -> Unit = {}
) {
    val loading by viewModel.isLoadingState

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ContractForm(
            selectedActivityState = viewModel.selectedActivityState,
            distanceState = viewModel.distanceState,
            pricePerKilometer = viewModel.pricePerKilometer,
            computedPriceState = viewModel.computedPriceState,
            selectedTimeLimitState = viewModel.selectedTimeLimitState,
            maximumActivityCount = viewModel.maximumActivityCountState,
            minimumActivityDistance = viewModel.minimumActivityDistanceState,
            contractTitle = viewModel.contractTitleState,
            onSubmit = {
                viewModel.submitContractForm(onSubmit)
            }
        )

        LoadingOverlay(
            visible = loading,
            backgroundAlpha = 0F
        )
    }
}

@Composable
fun ContractForm(
    selectedActivityState: MutableIntState,
    distanceState: TextFieldState,
    pricePerKilometer: TextFieldState,
    computedPriceState: MutableIntState,
    selectedTimeLimitState: MutableIntState,
    maximumActivityCount: TextFieldState,
    minimumActivityDistance: TextFieldState,
    contractTitle: TextFieldState,
    onSubmit: suspend () -> Unit = {}
) {
    var selectedPricePerKilometerIndex by remember { mutableIntStateOf(-1) }
    var selectedTimeLimit by remember { selectedTimeLimitState }
    val minimumActivityDistanceEnabled by remember {
        derivedStateOf {
            val maxActivities =
                maximumActivityCount.text.toString().toIntOrNull() ?: 0

            maxActivities != 1
        }
    }

    var computedPrice by computedPriceState

    LaunchedEffect(distanceState.text, pricePerKilometer.text) {
        try {
            val distance = distanceState.text.toString().toIntOrNull() ?: 0
            val pricePerKm = pricePerKilometer.text.toString().toIntOrNull() ?: 0

            computedPrice = distance * pricePerKm
        } catch (_: Exception) {

        }
    }

    LaunchedEffect(distanceState.text, selectedActivityState.value) {
        try {
            val distance = distanceState.text.toString().ifBlank { "0" }.toInt()

            if (selectedActivityState.value == -1 || distance == 0) return@LaunchedEffect
            val activity = ContractDataTypes.activityTypes[selectedActivityState.value]
            val distanceKm = "${distance}KM"

            contractTitle.setTextAndPlaceCursorAtEnd("$activity for $distanceKm")
        } catch (_: Exception) {

        }
    }

    LaunchedEffect(pricePerKilometer.text) {
        val pricePerKm = pricePerKilometer.text.toString()
        val currPricePerKmIndex = selectedPricePerKilometerIndex
        val priceOptions = ContractDataTypes.defaultPriceOptions

        if (currPricePerKmIndex != -1) {
            val (_, currPriceSelected) = priceOptions[currPricePerKmIndex]
            if (pricePerKm == currPriceSelected.toString()) return@LaunchedEffect
        }

        selectedPricePerKilometerIndex =
            priceOptions.indexOfFirst { (_, intValue) ->
                intValue.toString() == pricePerKm
            }
    }

    LaunchedEffect(maximumActivityCount.text) {
        val input = maximumActivityCount.text.toString().toIntOrNull() ?: 0
        if (input == 1) {
            minimumActivityDistance.setTextAndPlaceCursorAtEnd(distanceState.text.toString())
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1F)
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = "Create Activity Contract",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Text(
                text = "Set up a new activity contract with a distance target and a per-kilometer price.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFFAFAFA))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Activity Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Choose the activity that counts toward this contract.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    ) {
                        val activityOptions = ContractDataTypes.activityTypes
                        activityOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = activityOptions.size,
                                    baseShape = RoundedCornerShape(25)
                                ),
                                onClick = { selectedActivityState.value = index },
                                selected = index == selectedActivityState.value,
                                label = { Text(label) },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Color(0xFFEEEEEE)
                                )
                            )
                        }
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFFAFAFA))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Target Distance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "The total distance needed to complete this contract.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 8.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(25)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            modifier = Modifier.weight(1F),
                            state = distanceState,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W400),
                        )

                        Text(
                            text = "KM",
                            color = Color(0xFF848484),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFFAFAFA))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Reward per Kilometer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "The amount unlocked for every kilometer completed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    ) {
                        val priceOptions = ContractDataTypes.defaultPriceOptions
                        priceOptions.forEachIndexed { index, (label, intValue) ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = priceOptions.size,
                                    baseShape = RoundedCornerShape(25)
                                ),
                                onClick = {
                                    selectedPricePerKilometerIndex = index
                                    pricePerKilometer.setTextAndPlaceCursorAtEnd(
                                        intValue.toString()
                                    )
                                },
                                selected = index == selectedPricePerKilometerIndex,
                                label = { Text(label) },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Color(0xFFEEEEEE)
                                )
                            )
                        }
                    }

                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = "or set manually:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 8.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(25)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            modifier = Modifier.weight(1F),
                            state = pricePerKilometer,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W400),
                        )

                        Text(
                            text = "₱/KM",
                            color = Color(0xFF848484),
                            fontSize = 14.sp
                        )
                    }
                }
            }


            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                text = "-- Optional Configurations --",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFFAFAFA))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Contract Limits",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Set rules that define how this contract works.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "Time Limit",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        val contractExpiration = ContractDataTypes.defaultContractExpiration
                        contractExpiration.forEachIndexed { index, (label, _) ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = contractExpiration.size,
                                    baseShape = RoundedCornerShape(25)
                                ),
                                onClick = {
                                    selectedTimeLimit = index
                                },
                                selected = index == selectedTimeLimit,
                                label = {
                                    Text(
                                        text = label,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Color(0xFFEEEEEE)
                                )
                            )
                        }
                    }

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "Maximum Activity Count",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "The number of activities allowed for this contract. Just leave blank for no limits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(25)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            modifier = Modifier.weight(1F),
                            state = maximumActivityCount,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W400),
                        )

                        Text(
                            text = "# of Activities",
                            color = Color(0xFF848484),
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "Minimum KM Distance",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "The minimum kilometer distance required per activity. Minimum is 1 kilometer.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (minimumActivityDistanceEnabled) Color.Gray
                                else Color(0xF0E0E0E0),
                                shape = RoundedCornerShape(25)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            modifier = Modifier.weight(1F),
                            state = minimumActivityDistance,
                            enabled = minimumActivityDistanceEnabled,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.W400,
                                color = if (minimumActivityDistanceEnabled) Color.Black
                                else Color.Gray
                            ),
                        )

                        Text(
                            text = "KM",
                            color = Color(0xFF848484),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFFAFAFA))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Custom Title",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Personalize your contract with a name.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 8.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(25)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            modifier = Modifier.weight(1F),
                            state = contractTitle,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W400),
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "₱${computedPrice.toString().formatAmount()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Text(
                text = "Amount is computed based on Distance multiplied by Price per Kilometer",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )

            val maxActivity = maximumActivityCount.text.toString()
            val activityCountValidation =
                maxActivity.isBlank() || (maxActivity.toIntOrNull() ?: -1) >= 1
            val minActivityDist =
                minimumActivityDistance.text.toString().toIntOrNull() ?: 0
            val totalDistance =
                distanceState.text.toString().toIntOrNull() ?: 0

            val buttonEnabled =
                computedPrice != 0 // Computed Price is non-zero
                        && selectedActivityState.value != -1 // Selected an Activity Type
                        && contractTitle.text.isNotBlank() // Valid title, Generated or not
                        && activityCountValidation // Is Empty or is greater than 0
                        && minActivityDist >= 1 // Greater than 0
                        && totalDistance >= minActivityDist // Total distance is greater than Minumum Activity Distance

            SlideToActionButton(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                text = "Swipe to Place Contract",
                icon = {
                    Icon(
                        imageVector = FeatherIcons.ArrowRight,
                        contentDescription = null,
                        tint = if (buttonEnabled) Color.Black else Color.Gray
                    )
                },
                backgroundColor = ButtonDefaults.buttonColors().containerColor,
                enabled = buttonEnabled,
                onCompleted = onSubmit,
            )
        }
    }
}

@Preview
@Composable
fun PreviewContractForm() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            ContractForm(
                selectedActivityState = mutableIntStateOf(2),
                distanceState = TextFieldState(),
                pricePerKilometer = TextFieldState(),
                selectedTimeLimitState = mutableIntStateOf(2),
                maximumActivityCount = TextFieldState(),
                minimumActivityDistance = TextFieldState(),
                contractTitle = TextFieldState(),
                computedPriceState = mutableIntStateOf(20)
            )
        }
    }
}
