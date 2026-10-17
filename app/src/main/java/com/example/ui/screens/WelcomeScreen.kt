package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmaLogo
import com.example.ui.components.AmaPrimaryButton
import com.example.ui.components.AmaWaveBackground
import com.example.ui.theme.Avocado
import com.example.ui.theme.CardCream
import com.example.ui.theme.Cream
import com.example.ui.theme.MutedGreen

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSignInInfo by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Cream)
            .testTag("welcome_screen")
    ) {
        AmaWaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AmaLogo(height = 220.dp)
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Real Apps. Real People.",
                color = Avocado,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Discover, download, debug and review apps built by vibe coders and developers worldwide.",
                color = MutedGreen,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            AmaPrimaryButton(
                label = "Get Started",
                onClick = onGetStarted,
                modifier = Modifier.testTag("btn_get_started")
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = { showSignInInfo = true },
                modifier = Modifier.testTag("btn_have_account")
            ) {
                Text(text = "I already have an account", color = Avocado, fontSize = 13.sp)
            }
        }
    }

    if (showSignInInfo) {
        AlertDialog(
            onDismissRequest = { showSignInInfo = false },
            containerColor = CardCream,
            title = { Text("Sign-in isn't available yet", color = Avocado) },
            text = {
                Text(
                    "AmaApp accounts are still being built, so there is nothing to sign in to yet. " +
                        "You can explore the marketplace as a guest in the meantime.",
                    color = MutedGreen
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSignInInfo = false
                    onGetStarted()
                }) { Text("Continue as guest", color = Avocado) }
            },
            dismissButton = {
                TextButton(onClick = { showSignInInfo = false }) {
                    Text("Not now", color = MutedGreen)
                }
            }
        )
    }
}
