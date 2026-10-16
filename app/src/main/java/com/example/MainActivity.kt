package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.dialogs.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AmaAppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmaAppRoot(
    viewModel: AmaAppViewModel = viewModel()
) {
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            AmaTopBar(
                currentUser = currentUser,
                onRoleChange = { newRole -> viewModel.switchUserRole(newRole) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == MainNavigationTab.EXPLORE,
                    onClick = { viewModel.switchTab(MainNavigationTab.EXPLORE) },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_explore")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == MainNavigationTab.DASHBOARD,
                    onClick = { viewModel.switchTab(MainNavigationTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == MainNavigationTab.COMMUNITY,
                    onClick = { viewModel.switchTab(MainNavigationTab.COMMUNITY) },
                    icon = { Icon(Icons.Default.Forum, contentDescription = "Community") },
                    label = { Text("Community", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_community")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == MainNavigationTab.IP_VAULT,
                    onClick = { viewModel.switchTab(MainNavigationTab.IP_VAULT) },
                    icon = { Icon(Icons.Default.Security, contentDescription = "IP Vault") },
                    label = { Text("IP Vault", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_ip_vault")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == MainNavigationTab.ADMIN,
                    onClick = { viewModel.switchTab(MainNavigationTab.ADMIN) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                    label = { Text("Admin", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_admin")
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Tab Content
            when (uiState.currentTab) {
                MainNavigationTab.EXPLORE -> {
                    ExploreAppsScreen(
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
                }

                MainNavigationTab.DASHBOARD -> {
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

                MainNavigationTab.COMMUNITY -> {
                    CommunityForumScreen(
                        currentUser = currentUser,
                        threads = forumThreads,
                        onUpvote = { viewModel.upvoteThread(it) },
                        onPinToggle = { viewModel.togglePinThread(it) },
                        onDeleteThread = { viewModel.deleteForumThread(it) },
                        onNewThreadClick = { viewModel.openNewThreadDialog(true) }
                    )
                }

                MainNavigationTab.IP_VAULT -> {
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

                MainNavigationTab.ADMIN -> {
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

            // Simulated Download Progress Overlay
            AnimatedVisibility(
                visible = uiState.isDownloadingSimulated,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { uiState.downloadProgressPercent / 100f },
                            color = CyanPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                        Column {
                            Text(
                                text = "Downloading APK package...",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${uiState.downloadProgressPercent}% completed • Verifying SHA-256 signature",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanPrimaryLight
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Dialogs ---

    // App Detail Dialog
    uiState.selectedAppForDetail?.let { selectedApp ->
        val proof = blockchainProofs.firstOrNull { it.appId == selectedApp.id || it.proofId == selectedApp.blockchainProofId }
        AppDetailDialog(
            app = selectedApp,
            blockchainProof = proof,
            onDismiss = { viewModel.selectAppForDetail(null) },
            onDownloadClick = { build, isApk ->
                viewModel.triggerSimulatedDownload(selectedApp, build, isApk)
            },
            onFeedbackClick = {
                viewModel.selectAppForDetail(null)
                viewModel.openFeedbackDialog(selectedApp, true)
            },
            onViewProofClick = { p ->
                viewModel.selectProofForDetail(p)
            }
        )
    }

    // Upload App Dialog
    if (uiState.isUploadingAppDialog) {
        UploadAppDialog(
            onDismiss = { viewModel.openUploadAppDialog(false) },
            onSubmit = { title, pkg, pitch, desc, cat, plat, ver, size, notes ->
                viewModel.registerNewApp(title, pkg, pitch, desc, cat, plat, ver, size, notes)
            }
        )
    }

    // Upload Build Dialog
    if (uiState.isUploadingBuildDialog && uiState.selectedAppForNewBuild != null) {
        UploadBuildDialog(
            app = uiState.selectedAppForNewBuild!!,
            onDismiss = { viewModel.openUploadBuildDialog(null, false) },
            onSubmit = { ver, buildNum, plat, size, notes, rollout ->
                viewModel.uploadNewBuild(ver, buildNum, plat, size, notes, rollout)
            }
        )
    }

    // Feedback Dialog
    if (uiState.isFeedbackDialog && uiState.selectedAppForFeedback != null) {
        FeedbackDialog(
            app = uiState.selectedAppForFeedback!!,
            onDismiss = { viewModel.openFeedbackDialog(null, false) },
            onSubmit = { type, sev, rating, title, desc, dev, os ->
                viewModel.submitFeedback(type, sev, rating, title, desc, dev, os)
            }
        )
    }

    // New Thread Dialog
    if (uiState.isNewThreadDialog) {
        NewThreadDialog(
            onDismiss = { viewModel.openNewThreadDialog(false) },
            onSubmit = { title, content, cat ->
                viewModel.createForumThread(title, content, cat)
            }
        )
    }

    // Register IP Dialog
    if (uiState.isRegisterIpDialog) {
        RegisterIpDialog(
            onDismiss = { viewModel.openRegisterIpDialog(false) },
            onSubmit = { title, bundle, code ->
                viewModel.registerManualIp(title, bundle, code)
            }
        )
    }

    // Blockchain Certificate Dialog
    uiState.selectedProofForDetail?.let { proof ->
        BlockchainCertificateDialog(
            proof = proof,
            onDismiss = { viewModel.selectProofForDetail(null) }
        )
    }
}
