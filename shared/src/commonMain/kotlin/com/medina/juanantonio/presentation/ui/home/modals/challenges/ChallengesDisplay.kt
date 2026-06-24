package com.medina.juanantonio.presentation.ui.home.modals.challenges

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medina.juanantonio.domain.models.network.RunsomChallenge
import com.medina.juanantonio.presentation.ui.home.composables.ChallengeDisplayItem
import com.medina.juanantonio.presentation.ui.theme.RunsomTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.Flag
import org.koin.compose.koinInject

@Composable
fun ChallengesDisplayModal(
    viewModel: ChallengesDisplayViewModel = koinInject(),
    onJoinChallengeClick: (RunsomChallenge) -> Unit = {}
) {
    val challengesList by viewModel.challengesList.collectAsStateWithLifecycle()
    val isLoading by viewModel.challengesListLoadingState.collectAsStateWithLifecycle()

    ChallengesDisplay(
        challengesList = challengesList,
        isChallengesListLoading = isLoading,
        onJoinChallengeClick = onJoinChallengeClick
    )
}

@Composable
fun ChallengesDisplay(
    challengesList: List<RunsomChallenge>,
    isChallengesListLoading: Boolean = false,
    onJoinChallengeClick: (RunsomChallenge) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Global Challenges",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Text(
                text = "Discover challenges you can join and complete through your activities.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        AnimatedContent(
            targetState = isChallengesListLoading
        ) { isLoading ->
            if (isLoading) {
                ChallengesLoadingSpinner()
            } else {
                ChallengesListDisplay(
                    items = challengesList,
                    onJoinClick = onJoinChallengeClick
                )
            }
        }
    }
}


@Composable
fun ChallengesLoadingSpinner() {
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
fun ChallengesListDisplay(
    items: List<RunsomChallenge>,
    onJoinClick: (RunsomChallenge) -> Unit = {}
) {
    if (items.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(48.dp).padding(bottom = 8.dp),
                imageVector = FeatherIcons.Flag,
                tint = Color.Black,
                contentDescription = null
            )

            Text(
                text = "No Challenges Available",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    } else {
        Column(modifier = Modifier) {
            items.forEach { challenge ->
                ChallengeDisplayItem(
                    modifier = Modifier,
                    challenge = challenge,
                    onJoinClick = onJoinClick
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewChallengesDisplay() {
    RunsomTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
            ChallengesDisplay(
                challengesList = emptyList()
            )
        }
    }
}
