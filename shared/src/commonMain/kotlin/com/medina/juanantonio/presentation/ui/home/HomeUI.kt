package com.medina.juanantonio.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.medina.juanantonio.domain.models.CoinsPHData
import com.medina.juanantonio.domain.models.entities.Contract
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import com.medina.juanantonio.domain.models.network.strava.StravaAthlete
import com.medina.juanantonio.presentation.composables.IconButtonWithTextLabel
import com.medina.juanantonio.presentation.composables.AnimatedBottomSheet
import com.medina.juanantonio.presentation.ui.home.composables.ContractDisplayItem
import com.medina.juanantonio.presentation.ui.home.modals.ModalDisplay
import com.medina.juanantonio.presentation.ui.home.modals.activity_form.SubmitActivityModal
import com.medina.juanantonio.presentation.ui.home.modals.contract_form.ContractModal
import com.medina.juanantonio.presentation.ui.home.modals.logout.LogoutModal
import com.medina.juanantonio.presentation.ui.home.modals.settings.SettingsModal
import com.medina.juanantonio.presentation.ui.theme.RunsomTheme
import com.medina.juanantonio.presentation.utils.formatAmount
import com.medina.juanantonio.presentation.utils.toFormattedDateTime
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronRight
import compose.icons.feathericons.Plus
import compose.icons.feathericons.RefreshCw
import compose.icons.feathericons.Settings
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun HomeScreenUI(
    viewModel: HomeViewModel = koinInject()
) {
    val athlete by viewModel.currentStravaAthlete.collectAsStateWithLifecycle()
    val coinsPHData by viewModel.currentCoinsPHData.collectAsStateWithLifecycle()
    val activeContracts by viewModel.activeContractsWithActivities.collectAsStateWithLifecycle(
        emptyList()
    )

    HomeScreenLayout(
        athlete = athlete,
        coinsPHData = coinsPHData,
        activeContractsList = activeContracts,
        onCoinsPhDataRefreshClick = viewModel::refreshCoinsPHData,
        onLoginWithStravaClick = viewModel::loginWithStrava,
        onLogoutStravaClick = viewModel::logout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenLayout(
    athlete: StravaAthlete?,
    coinsPHData: CoinsPHData?,
    activeContractsList: List<ContractWithActivities>,
    onCoinsPhDataRefreshClick: () -> Unit = {},
    onLoginWithStravaClick: () -> Unit = {},
    onLogoutStravaClick: (() -> Unit) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var modalDisplay by remember { mutableStateOf<ModalDisplay?>(null) }

    val isFeaturesEnabled = coinsPHData != null && athlete != null

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    if (athlete != null) {
                        AsyncImage(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                                .size(28.dp),
                            model = athlete.profile,
                            contentDescription = null,
                        )
                    }

                    Text(
                        text = if (athlete != null) {
                            "Welcome, ${athlete.fullName}!"
                        } else {
                            "Welcome, Runner!"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    if (athlete != null) {
                        Button(
                            modifier = Modifier,
                            onClick = {
                                modalDisplay = ModalDisplay.Logout()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "Logout",
                                color = Color(0xFFFC4C02)
                            )
                        }
                    } else {
                        Button(
                            modifier = Modifier,
                            onClick = onLoginWithStravaClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "Login With Strava",
                                color = Color(0xFFFC4C02)
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
                Box(
                    modifier = Modifier
                        .background(color = Color.White)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopEnd),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            modifier = Modifier.padding(top = 4.dp),
                            onClick = { modalDisplay = ModalDisplay.Settings() }
                        ) {
                            Icon(
                                modifier = Modifier.padding(10.dp),
                                imageVector = FeatherIcons.Settings,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            modifier = Modifier.padding(top = 4.dp, end = 4.dp),
                            onClick = onCoinsPhDataRefreshClick
                        ) {
                            Icon(
                                modifier = Modifier.padding(10.dp),
                                imageVector = FeatherIcons.RefreshCw,
                                contentDescription = null
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 8.dp, end = 12.dp)
                    ) {
                        Text(
                            text = "Last Updated: ",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = coinsPHData?.updateTime?.toFormattedDateTime() ?: "Never",

                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(
                                horizontal = 16.dp,
                                vertical = 42.dp
                            )
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "My Balance",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Text(
                            text = if (coinsPHData == null) "₱0.00" else "₱${coinsPHData.pesoBalance.formatAmount()}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = coinsPHData?.email
                                ?: "Make sure to setup your Coins.PH API Key",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        IconButtonWithTextLabel(
                            modifier = Modifier.padding(top = 16.dp),
                            imageVector = FeatherIcons.Plus,
                            enabled = isFeaturesEnabled,
                            text = "Create Contract"
                        ) {
                            coroutineScope.launch {
                                modalDisplay = ModalDisplay.ContractForm()
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = "Active Contracts",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = "Select a contract to submit an activity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                TextButton(
                    onClick = {}
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )

                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = FeatherIcons.ChevronRight,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        items(
            items = activeContractsList,
            key = { item -> item.contract.id }
        ) { contract ->
            ContractDisplayItem(
                modifier = Modifier.animateItem(),
                contract = contract,
                enabled = isFeaturesEnabled
            ) {
                coroutineScope.launch {
                    modalDisplay = ModalDisplay.SubmitActivityForm(contract)
                }
            }
        }
    }

    AnimatedBottomSheet(
        isVisible = modalDisplay != null,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        onDismissRequest = { modalDisplay = null }
    ) {
        when (val display = modalDisplay) {
            is ModalDisplay.ContractForm -> {
                ContractModal {
                    modalDisplay = null
                }
            }

            is ModalDisplay.SubmitActivityForm -> {
                SubmitActivityModal(contract = display.contract) {
                    modalDisplay = null
                }
            }

            is ModalDisplay.Settings -> {
                SettingsModal {
                    modalDisplay = null
                }
            }

            is ModalDisplay.Logout -> {
                LogoutModal(
                    onPositiveButtonClick = {
                        onLogoutStravaClick {
                            modalDisplay = null
                        }
                    }
                )
            }

            else -> {}
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreenLayout() {
    RunsomTheme {
        HomeScreenLayout(
            athlete = null,
            coinsPHData = null,
            activeContractsList = emptyList()
        )
    }
}

@Preview
@Composable
fun PreviewLoggedInHomeScreenLayout() {
    RunsomTheme {
        HomeScreenLayout(
            athlete = StravaAthlete(
                id = "",
                firstname = "AB",
                lastname = "CD",
                username = "test",
                country = "Philippines",
                city = "Manila",
                profile = ""
            ),
            coinsPHData = CoinsPHData(
                pesoBalance = "100",
                email = "test@gmail.com",
                updateTime = "1728428254627"
            ),
            activeContractsList = listOf(
                ContractWithActivities(

                    contract = Contract(
                        activityType = "Swim",
                        distance = 10,
                        pricePerKm = 10
                    ),
                    activities = emptyList()
                )
            )
        )
    }
}