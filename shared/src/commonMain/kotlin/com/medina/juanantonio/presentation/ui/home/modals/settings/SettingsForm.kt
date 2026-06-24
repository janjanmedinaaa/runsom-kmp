package com.medina.juanantonio.presentation.ui.home.modals.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medina.juanantonio.presentation.composables.CustomTextField
import com.medina.juanantonio.presentation.composables.LoadingOverlay
import com.medina.juanantonio.presentation.ui.theme.RunsomTheme
import org.koin.compose.koinInject

@Composable
fun SettingsModal(
    viewModel: SettingsFormViewModel = koinInject(),
    onSubmit: () -> Unit = {}
) {
    val ipAddress by viewModel.ipAddressDisplay.collectAsStateWithLifecycle()
    val loading by viewModel.isLoadingState

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        SettingsForm(
            mainApiKeyState = viewModel.mainApiKeyState,
            mainApiSecretState = viewModel.mainApiSecretState,
            autoPaymentsEnabled = viewModel.autoPaymentsEnabledState,
            escrowApiKeyState = viewModel.escrowApiKeyState,
            escrowApiSecretState = viewModel.escrowApiSecretState,
            holderEmailAccount = viewModel.holderEmailAccount,
            ipAddress = ipAddress,
            onSubmit = {
                viewModel.submitForm {
                    onSubmit()
                }
            }
        )

        LoadingOverlay(
            visible = loading,
            backgroundAlpha = 0F
        )
    }
}

@Composable
fun SettingsForm(
    mainApiKeyState: TextFieldState,
    mainApiSecretState: TextFieldState,
    autoPaymentsEnabled: MutableState<Boolean>,
    escrowApiKeyState: TextFieldState,
    escrowApiSecretState: TextFieldState,
    holderEmailAccount: TextFieldState,
    ipAddress: String,
    onSubmit: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Coins.PH Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Text(
            text = "Setup your Coins.PH API Key and Account to send your contract money",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        OwnerAccountInputCard(
            mainApiKeyState = mainApiKeyState,
            mainApiSecretState = mainApiSecretState,
            ipAddress = ipAddress
        )

        EscrowAccountInputCard(
            autoPaymentsEnabled = autoPaymentsEnabled,
            escrowApiKeyState = escrowApiKeyState,
            escrowApiSecretState = escrowApiSecretState,
            holderEmailAccount = holderEmailAccount
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Make sure all the information you have entered are valid Coins.PH data.",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            onClick = onSubmit
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Save Settings"
            )
        }
    }
}

@Composable
fun OwnerAccountInputCard(
    mainApiKeyState: TextFieldState,
    mainApiSecretState: TextFieldState,
    ipAddress: String
) {
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
                text = "Coins.PH Owner Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Make sure you have correctly IP-Whitelisted your API Key",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "API Key",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )

            CustomTextField(
                state = mainApiKeyState,
                isHidden = true
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "API Secret",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )

            CustomTextField(
                state = mainApiSecretState,
                isHidden = true
            )

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Your IP Address is: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Text(
                    text = ipAddress,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@Composable
fun EscrowAccountInputCard(
    autoPaymentsEnabled: MutableState<Boolean>,
    escrowApiKeyState: TextFieldState,
    escrowApiSecretState: TextFieldState,
    holderEmailAccount: TextFieldState
) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1F)) {
                    Text(
                        text = "Coins.PH Escrow Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "The account that holds funds until they are earned back.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Switch(
                    modifier = Modifier.padding(start = 8.dp),
                    checked = autoPaymentsEnabled.value,
                    onCheckedChange = { value ->
                        autoPaymentsEnabled.value = value
                    }
                )
            }

            AnimatedContent(autoPaymentsEnabled.value) { enabled ->
                if (enabled) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "API Key",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )

                        CustomTextField(
                            state = escrowApiKeyState,
                            isHidden = true
                        )

                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "API Secret",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )

                        CustomTextField(
                            state = escrowApiSecretState,
                            isHidden = true
                        )
                    }
                } else {
                    CustomTextField(
                        modifier = Modifier.padding(top = 8.dp),
                        state = holderEmailAccount,
                        keyboardType = KeyboardType.Email
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsForm() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            SettingsForm(
                mainApiKeyState = TextFieldState(),
                mainApiSecretState = TextFieldState(),
                autoPaymentsEnabled = mutableStateOf(false),
                escrowApiKeyState = TextFieldState(),
                escrowApiSecretState = TextFieldState(),
                holderEmailAccount = TextFieldState(),
                ipAddress = "180.2.2.2"
            )
        }
    }
}

@Preview
@Composable
fun PreviewAutoEnabledSettingsForm() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            SettingsForm(
                mainApiKeyState = TextFieldState(),
                mainApiSecretState = TextFieldState(),
                autoPaymentsEnabled = mutableStateOf(true),
                escrowApiKeyState = TextFieldState(),
                escrowApiSecretState = TextFieldState(),
                holderEmailAccount = TextFieldState(),
                ipAddress = "180.2.2.2"
            )
        }
    }
}
