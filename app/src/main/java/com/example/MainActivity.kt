package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AmaAppViewModel
import com.example.ui.MainNavigationTab
import com.example.ui.components.AmaBottomBar
import com.example.ui.components.AmaBottomTab
import com.example.ui.components.buildRatingSummaries
import com.example.ui.dialogs.BlockchainCertificateDialog
import com.example.ui.dialogs.FeedbackDialog
import com.example.ui.dialogs.NewThreadDialog
import com.example.ui.dialogs.RegisterIpDialog
import com.example.ui.dialogs.UploadAppDialog
import com.example.ui.dialogs.UploadBuildDialog
import com.example.ui.screens.AdminControlScreen
import com.example.ui.screens.AppDetailsScreen
import com.example.ui.screens.CommunityForumScreen
import com.example.ui.screens.DeveloperDashboardScreen
import com.example.ui.screens.ExploreAppsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IpTimestampVaultScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReviewsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.splash.SplashScreen
import com.example.ui.theme.Avocado
import com.example.ui.theme.Cream
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Olive

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transparent = android.graphics.Color.TRANSPARENT
        // The brand is light (cream), so system bar icons are dark.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(transparent, transparent),
            navigationBarStyle = SystemBarStyle.light(transparent, transparent)
        )
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val onboarded = prefs.getBoolean(KEY_ONBOARDED, false)
        setContent {
            MyApplicationTheme {
                AmaAppRoot(
                    onboarded = onboarded,
                    onOnboardingFinished = {
                        prefs.edit().putBoolean(KEY_ONBOARDED, true).apply()
                    }
                )
            }
        }
    }

    private companion object {
        const val PREFS_NAME = "amaapp_prefs"
        const val KEY_ONBOARDED = "onboarded"
    }
}

private enum class AppStage { SPLASH, WELCOME, MAIN }

/** Splash video -> Welcome (first launch only) -> main app. */
@Composable
fun AmaAppRoot(
    onboarded: Boolean,
    onOnboardingFinished: () -> Unit,
    viewModel: AmaAppViewModel = viewModel()
) {
    var stage by rememberSaveable { mutableStateOf(AppStage.SPLASH) }

    Box(modifier = Modifier.fillMaxSize().background(Cream)) {
        Crossfade(targetState = stage, label = "app_stage") { current ->
            when (current) {
                AppStage.SPLASH -> SplashScreen(
                    onFinished = { stage = if (onboarded) AppStage.MAIN else AppStage.WELCOME }
                )
                AppStage.WELCOME -> WelcomeScreen(
                    onGetStarted = {
                        onOnboardingFinished()
                        stage = AppStage.MAIN
                    }
                )
                AppStage.MAIN -> AmaMainContent(viewModel)
            }
        }
    }
}

private fun MainNavigationTab.bottomTab(): AmaBottomTab = when (this) {
    MainNavigationTab.HOME -> AmaBottomTab.HOME
    MainNavigationTab.EXPLORE -> AmaBottomTab.EXPLORE
    MainNavigationTab.LIBRARY -> AmaBottomTab.LIBRARY
    else -> AmaBottomTab.PROFILE
}

private fun AmaBottomTab.destination(): MainNavigationTab = when (this) {
    AmaBottomTab.HOME -> MainNavigationTab.HOME
    AmaBottomTab.EXPLORE -> MainNavigationTab.EXPLORE
    AmaBottomTab.LIBRARY -> MainNavigationTab.LIBRARY
    AmaBottomTab.PROFILE -> MainNavigationTab.PROFILE
}

@Composable
private fun AmaMainContent(viewModel: AmaAppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val apps by viewModel.apps.collectAsStateWithLifecycle()
    val feedbackReports by viewModel.feedbackReports.collectAsStateWithLifecycle()
    val forumThreads by viewModel.forumThreads.collectAsStateWithLifecycle()
    val blockchainProofs by viewModel.blockchainProofs.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    // Ratings are always derived from real review records, never hard-coded.
    val ratingSummaries = remember(feedbackReports) { buildRatingSummaries(feedbackReports) }

    // Always render the freshest copy of the selected app
    val detailApp = uiState.selectedAppForDetail?.let { sel -> apps.firstOrNull { it.id == sel.id } ?: sel }
    val reviewsApp = uiState.reviewsApp?.let { sel -> apps.firstOrNull { it.id == sel.id } ?: sel }
    val inSecondaryPage = detailApp != null || reviewsApp != null
    val tab = uiState.currentTab

    BackHandler(enabled = !inSecondaryPage && tab != MainNavigationTab.HOME) {
        viewModel.switchTab(
            when (tab) {
                MainNavigationTab.DASHBOARD,
                MainNavigationTab.COMMUNITY,
                MainNavigationTab.IP_VAULT,
                MainNavigationTab.ADMIN -> MainNavigationTab.PROFILE
                else -> MainNavigationTab.HOME
            }
        )
    }
    BackHandler(enabled = detailApp != null && reviewsApp == null) {
        viewModel.selectAppForDetail(null)
    }
    BackHandler(enabled = reviewsApp != null) {
        viewModel.openReviews(null)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // Details and Reviews are secondary pages: no bottom navigation there.
            if (!inSecondaryPage) {
                AmaBottomBar(
                    selected = tab.bottomTab(),
                    onSelect = { viewModel.switchTab(it.destination()) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Avocado,
                    contentColor = Cream,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                reviewsApp != null -> ReviewsScreen(
                    app = reviewsApp,
                    reviews = feedbackReports.filter { it.appId == reviewsApp.id && it.rating in 1..5 },
                    summary = ratingSummaries[reviewsApp.id],
                    onBack = { viewModel.openReviews(null) },
                    onSubmit = { rating, comment -> viewModel.submitReview(reviewsApp, rating, comment) },
                    modifier = Modifier.fillMaxSize()
                )

                detailApp != null -> AppDetailsScreen(
                    app = detailApp,
                    summary = ratingSummaries[detailApp.id],
                    reviews = feedbackReports.filter { it.appId == detailApp.id && it.rating in 1..5 },
                    isSaved = detailApp.id in uiState.savedAppIds,
                    isDownloaded = detailApp.id in uiState.downloadedAppIds,
                    onBack = { viewModel.selectAppForDetail(null) },
                    onToggleSaved = { viewModel.toggleSaved(detailApp) },
                    onDownload = { build, isApk -> viewModel.triggerSimulatedDownload(detailApp, build, isApk) },
                    onDebug = { viewModel.openFeedbackDialog(detailApp, true) },
                    onSeeAllReviews = { viewModel.openReviews(detailApp) },
                    onMessage = { viewModel.showSnackbar(it) },
                    modifier = Modifier.fillMaxSize()
                )

                else -> when (tab) {
                    MainNavigationTab.HOME -> HomeScreen(
                        apps = apps,
                        ratingSummaries = ratingSummaries,
                        downloadedAppIds = uiState.downloadedAppIds,
                        searchQuery = uiState.searchQuery,
                        selectedCategory = uiState.selectedCategory,
                        selectedPlatform = uiState.selectedPlatform,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategoryChange = { viewModel.setSelectedCategory(it) },
                        onPlatformChange = { viewModel.setSelectedPlatform(it) },
                        onClearFilters = { viewModel.clearFilters() },
                        onAppClick = { viewModel.selectAppForDetail(it) },
                        onDownload = { app, build -> viewModel.triggerSimulatedDownload(app, build, true) },
                        onSeeAll = { viewModel.switchTab(MainNavigationTab.EXPLORE) },
                        onNotifications = { viewModel.showSnackbar("You're all caught up.") },
                        onProfile = { viewModel.switchTab(MainNavigationTab.PROFILE) },
                        modifier = Modifier.fillMaxSize()
                    )

                    MainNavigationTab.EXPLORE -> ExploreAppsScreen(
                        apps = apps,
                        currentUser = currentUser,
                        searchQuery = uiState.searchQuery,
                        selectedCategory = uiState.selectedCategory,
                        selectedPlatform = uiState.selectedPlatform,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategoryChange = { viewModel.setSelectedCategory(it) },
                        onPlatformChange = { viewModel.setSelectedPlatform(it) },
                        onAppDetailClick = { viewModel.selectAppForDetail(it) },
                        onDownloadClick = { app, build, isApk ->
                            viewModel.triggerSimulatedDownload(app, build, isApk)
                        },
                        onFeedbackClick = { viewModel.openFeedbackDialog(it, true) },
                        onUploadAppClick = { viewModel.openUploadAppDialog(true) }
                    )

                    MainNavigationTab.LIBRARY -> LibraryScreen(
                        apps = apps,
                        savedAppIds = uiState.savedAppIds,
                        downloadedAppIds = uiState.downloadedAppIds,
                        onAppClick = { viewModel.selectAppForDetail(it) },
                        onRemoveSaved = { viewModel.toggleSaved(it) },
                        onDiscover = { viewModel.switchTab(MainNavigationTab.HOME) },
                        onMessage = { viewModel.showSnackbar(it) },
                        modifier = Modifier.fillMaxSize()
                    )

                    MainNavigationTab.PROFILE -> ProfileScreen(
                        user = currentUser,
                        onSwitchRole = { viewModel.switchUserRole(it) },
                        onOpen = { viewModel.switchTab(it) },
                        modifier = Modifier.fillMaxSize()
                    )

                    MainNavigationTab.DASHBOARD -> SecondaryPage(
                        title = "Developer Dashboard",
                        onBack = { viewModel.switchTab(MainNavigationTab.PROFILE) }
                    ) {
                        DeveloperDashboardScreen(
                            currentUser = currentUser,
                            apps = apps,
                            feedbackReports = feedbackReports,
                            onUploadNewApp = { viewModel.openUploadAppDialog(true) },
                            onUploadNewBuild = { viewModel.openUploadBuildDialog(it, true) },
                            onUpdateRollout = { appId, buildId, pct ->
                                viewModel.updateRollout(appId, buildId, pct)
                            },
                            onRespondToFeedback = { fbId, reply, st ->
                                viewModel.respondToFeedback(fbId, reply, st)
                            },
                            onSwitchRole = { viewModel.switchUserRole(it) }
                        )
                    }

                    MainNavigationTab.COMMUNITY -> SecondaryPage(
                        title = "Community",
                        onBack = { viewModel.switchTab(MainNavigationTab.PROFILE) }
                    ) {
                        CommunityForumScreen(
                            currentUser = currentUser,
                            threads = forumThreads,
                            onUpvote = { viewModel.upvoteThread(it) },
                            onPinToggle = { viewModel.togglePinThread(it) },
                            onDeleteThread = { viewModel.deleteForumThread(it) },
                            onNewThreadClick = { viewModel.openNewThreadDialog(true) }
                        )
                    }

                    MainNavigationTab.IP_VAULT -> SecondaryPage(
                        title = "IP Vault",
                        onBack = { viewModel.switchTab(MainNavigationTab.PROFILE) }
                    ) {
                        IpTimestampVaultScreen(
                            currentUser = currentUser,
                            proofs = blockchainProofs,
                            verificationQuery = uiState.verificationQuery,
                            verificationResult = uiState.verificationResult,
                            isVerifying = uiState.isVerifying,
                            onQueryChange = { viewModel.setVerificationQuery(it) },
                            onVerifyClick = { viewModel.verifyBlockchainProof(it) },
                            onRegisterIpClick = { viewModel.openRegisterIpDialog(true) },
                            onSelectProof = { viewModel.selectProofForDetail(it) }
                        )
                    }

                    MainNavigationTab.ADMIN -> SecondaryPage(
                        title = "Admin Console",
                        onBack = { viewModel.switchTab(MainNavigationTab.PROFILE) }
                    ) {
                        AdminControlScreen(
                            currentUser = currentUser,
                            apps = apps,
                            feedbackReports = feedbackReports,
                            proofs = blockchainProofs,
                            onSetAppStatus = { appId, st -> viewModel.setAppStatus(appId, st) },
                            onSwitchRole = { viewModel.switchUserRole(it) }
                        )
                    }
                }
            }

            // Simulated download progress overlay
            AnimatedVisibility(
                visible = uiState.isDownloadingSimulated,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Avocado,
                    border = BorderStroke(1.dp, Olive),
                    tonalElevation = 0.dp,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { uiState.downloadProgressPercent / 100f },
                            color = Cream,
                            trackColor = Olive,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                        Column {
                            Text(
                                text = "Downloading package...",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Cream
                            )
                            Text(
                                text = "${uiState.downloadProgressPercent}% completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = Cream.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Dialogs ---

    if (uiState.isUploadingAppDialog) {
        UploadAppDialog(
            onDismiss = { viewModel.openUploadAppDialog(false) },
            onSubmit = { title, pkg, pitch, desc, cat, plat, ver, size, notes ->
                viewModel.registerNewApp(title, pkg, pitch, desc, cat, plat, ver, size, notes)
            }
        )
    }

    if (uiState.isUploadingBuildDialog && uiState.selectedAppForNewBuild != null) {
        UploadBuildDialog(
            app = uiState.selectedAppForNewBuild!!,
            onDismiss = { viewModel.openUploadBuildDialog(null, false) },
            onSubmit = { ver, buildNum, plat, size, notes, rollout ->
                viewModel.uploadNewBuild(ver, buildNum, plat, size, notes, rollout)
            }
        )
    }

    if (uiState.isFeedbackDialog && uiState.selectedAppForFeedback != null) {
        FeedbackDialog(
            app = uiState.selectedAppForFeedback!!,
            onDismiss = { viewModel.openFeedbackDialog(null, false) },
            onSubmit = { type, sev, rating, title, desc, dev, os ->
                viewModel.submitFeedback(type, sev, rating, title, desc, dev, os)
            }
        )
    }

    if (uiState.isNewThreadDialog) {
        NewThreadDialog(
            onDismiss = { viewModel.openNewThreadDialog(false) },
            onSubmit = { title, content, cat ->
                viewModel.createForumThread(title, content, cat)
            }
        )
    }

    if (uiState.isRegisterIpDialog) {
        RegisterIpDialog(
            onDismiss = { viewModel.openRegisterIpDialog(false) },
            onSubmit = { title, bundle, code ->
                viewModel.registerManualIp(title, bundle, code)
            }
        )
    }

    uiState.selectedProofForDetail?.let { proof ->
        BlockchainCertificateDialog(
            proof = proof,
            onDismiss = { viewModel.selectProofForDetail(null) }
        )
    }
}

/** Wraps the older developer/admin screens with a back arrow to return to Profile. */
@Composable
private fun SecondaryPage(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Avocado)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Avocado
            )
        }
        Box(modifier = Modifier.weight(1f)) { content() }
    }
}
