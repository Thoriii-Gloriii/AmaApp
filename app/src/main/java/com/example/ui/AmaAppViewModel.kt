package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AmaAppRepository
import com.example.data.VerificationResult
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainNavigationTab(val label: String, val testTag: String) {
    EXPLORE("Explore", "nav_explore"),
    DASHBOARD("Dashboard", "nav_dashboard"),
    COMMUNITY("Community", "nav_community"),
    IP_VAULT("IP Vault", "nav_ip_vault"),
    ADMIN("Admin Panel", "nav_admin")
}

data class AmaUiState(
    val currentTab: MainNavigationTab = MainNavigationTab.EXPLORE,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedPlatform: AppPlatform? = null,
    val selectedAppForDetail: AppListing? = null,
    val selectedAppForNewBuild: AppListing? = null,
    val selectedAppForFeedback: AppListing? = null,
    val isUploadingAppDialog: Boolean = false,
    val isUploadingBuildDialog: Boolean = false,
    val isFeedbackDialog: Boolean = false,
    val isNewThreadDialog: Boolean = false,
    val isRegisterIpDialog: Boolean = false,
    val selectedProofForDetail: BlockchainTimestampProof? = null,
    val verificationQuery: String = "",
    val verificationResult: VerificationResult? = null,
    val isVerifying: Boolean = false,
    val snackbarMessage: String? = null,
    val isDownloadingSimulated: Boolean = false,
    val downloadProgressPercent: Int = 0
)

class AmaAppViewModel(
    private val repository: AmaAppRepository = AmaAppRepository()
) : ViewModel() {

    val currentUser: StateFlow<UserProfile> = repository.currentUser
    val apps: StateFlow<List<AppListing>> = repository.apps
    val feedbackReports: StateFlow<List<FeedbackReport>> = repository.feedbackReports
    val forumThreads: StateFlow<List<ForumThread>> = repository.forumThreads
    val blockchainProofs: StateFlow<List<BlockchainTimestampProof>> = repository.blockchainProofs
    val recentDownloadLogs: StateFlow<List<String>> = repository.recentDownloadLogs

    private val _uiState = MutableStateFlow(AmaUiState())
    val uiState: StateFlow<AmaUiState> = _uiState.asStateFlow()

    fun switchTab(tab: MainNavigationTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun switchUserRole(role: UserRole) {
        repository.switchRole(role)
        showSnackbar("Switched profile to ${role.title} (${role.badge})")
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSelectedPlatform(platform: AppPlatform?) {
        _uiState.update { it.copy(selectedPlatform = platform) }
    }

    fun selectAppForDetail(app: AppListing?) {
        _uiState.update { it.copy(selectedAppForDetail = app) }
    }

    fun openUploadAppDialog(open: Boolean) {
        if (open && !currentUser.value.role.canUploadApps) {
            showSnackbar("Permission Denied: Only Developers or Admins can upload apps.")
            return
        }
        _uiState.update { it.copy(isUploadingAppDialog = open) }
    }

    fun openUploadBuildDialog(app: AppListing?, open: Boolean) {
        if (open && !currentUser.value.role.canManageVersions) {
            showSnackbar("Permission Denied: Only Developers or Admins can upload new builds.")
            return
        }
        _uiState.update {
            it.copy(
                selectedAppForNewBuild = app,
                isUploadingBuildDialog = open
            )
        }
    }

    fun openFeedbackDialog(app: AppListing?, open: Boolean) {
        _uiState.update {
            it.copy(
                selectedAppForFeedback = app,
                isFeedbackDialog = open
            )
        }
    }

    fun openNewThreadDialog(open: Boolean) {
        _uiState.update { it.copy(isNewThreadDialog = open) }
    }

    fun openRegisterIpDialog(open: Boolean) {
        if (open && !currentUser.value.role.canGenerateBlockchainProofs) {
            showSnackbar("Permission Denied: Only Developers or Admins can register IP proofs.")
            return
        }
        _uiState.update { it.copy(isRegisterIpDialog = open) }
    }

    fun selectProofForDetail(proof: BlockchainTimestampProof?) {
        _uiState.update { it.copy(selectedProofForDetail = proof) }
    }

    fun setVerificationQuery(query: String) {
        _uiState.update { it.copy(verificationQuery = query) }
    }

    fun verifyBlockchainProof(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, verificationResult = null) }
            delay(400) // Realistic cryptographic verification delay
            val result = repository.verifyAppIntellectualProperty(query)
            _uiState.update { it.copy(isVerifying = false, verificationResult = result) }
        }
    }

    fun registerNewApp(
        title: String,
        packageName: String,
        shortPitch: String,
        description: String,
        category: String,
        platform: AppPlatform,
        initialVersionName: String,
        binarySizeMb: Double,
        releaseNotes: String
    ) {
        if (!currentUser.value.role.canUploadApps) {
            showSnackbar("Permission Denied: Developer role required to register apps.")
            return
        }
        val newApp = repository.registerNewApp(
            title = title,
            packageName = packageName,
            shortPitch = shortPitch,
            description = description,
            category = category,
            platform = platform,
            initialVersionName = initialVersionName,
            binarySizeMb = binarySizeMb,
            releaseNotes = releaseNotes
        )
        _uiState.update { it.copy(isUploadingAppDialog = false) }
        showSnackbar("App registered & IP timestamped on blockchain for '${newApp.title}'!")
    }

    fun uploadNewBuild(
        versionName: String,
        buildNumber: Int,
        platform: AppPlatform,
        binarySizeMb: Double,
        releaseNotes: String,
        rolloutPercent: Int
    ) {
        val app = _uiState.value.selectedAppForNewBuild ?: return
        if (!currentUser.value.role.canManageVersions) {
            showSnackbar("Permission Denied: Developer role required to upload builds.")
            return
        }
        repository.uploadNewBuild(
            appId = app.id,
            versionName = versionName,
            buildNumber = buildNumber,
            platform = platform,
            binarySizeMb = binarySizeMb,
            releaseNotes = releaseNotes,
            rolloutPercent = rolloutPercent
        )
        _uiState.update { it.copy(isUploadingBuildDialog = false, selectedAppForNewBuild = null) }
        showSnackbar("Build $versionName (#$buildNumber) successfully deployed to AmaApp!")
    }

    fun updateRollout(appId: String, buildId: String, percent: Int) {
        if (!currentUser.value.role.canManageVersions) {
            showSnackbar("Permission Denied: Developer role required to update rollouts.")
            return
        }
        repository.updateRolloutPercentage(appId, buildId, percent)
        showSnackbar("Staged rollout updated to $percent%")
    }

    fun triggerSimulatedDownload(app: AppListing, build: AppBuild, isDirectApk: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloadingSimulated = true, downloadProgressPercent = 10) }
            delay(250)
            _uiState.update { it.copy(downloadProgressPercent = 55) }
            delay(250)
            _uiState.update { it.copy(downloadProgressPercent = 100) }
            delay(200)
            _uiState.update { it.copy(isDownloadingSimulated = false, downloadProgressPercent = 0) }

            val target = if (isDirectApk) "Direct APK (${build.binarySizeMb} MB)" else "Google Play Beta Track"
            repository.recordDownload(app.id, build.versionName, target)
            showSnackbar("Downloaded ${app.title} ${build.versionName} via $target. Ready to test!")
        }
    }

    fun submitFeedback(
        type: FeedbackType,
        severity: FeedbackSeverity,
        rating: Int,
        title: String,
        description: String,
        deviceModel: String,
        osVersion: String
    ) {
        val app = _uiState.value.selectedAppForFeedback ?: return
        repository.submitFeedback(
            appId = app.id,
            type = type,
            severity = severity,
            rating = rating,
            title = title,
            description = description,
            deviceModel = deviceModel,
            osVersion = osVersion
        )
        _uiState.update { it.copy(isFeedbackDialog = false, selectedAppForFeedback = null) }
        showSnackbar("Feedback submitted directly to ${app.developerName}!")
    }

    fun respondToFeedback(feedbackId: String, response: String, newStatus: String) {
        if (!currentUser.value.role.canManageVersions) {
            showSnackbar("Permission Denied: Only developers can respond to feedback.")
            return
        }
        repository.respondToFeedback(feedbackId, response, newStatus)
        showSnackbar("Response sent to beta tester!")
    }

    fun createForumThread(title: String, content: String, category: String, appId: String? = null) {
        repository.createForumThread(title, content, category, appId)
        _uiState.update { it.copy(isNewThreadDialog = false) }
        showSnackbar("Discussion thread posted to Community!")
    }

    fun upvoteThread(threadId: String) {
        repository.upvoteThread(threadId)
    }

    fun togglePinThread(threadId: String) {
        if (!currentUser.value.role.canModerateForum) {
            showSnackbar("Permission Denied: Only Admins can pin threads.")
            return
        }
        repository.togglePinThread(threadId)
        showSnackbar("Thread pin status updated.")
    }

    fun deleteForumThread(threadId: String) {
        if (!currentUser.value.role.canModerateForum) {
            showSnackbar("Permission Denied: Only Admins can delete threads.")
            return
        }
        repository.deleteForumThread(threadId)
        showSnackbar("Thread removed by Admin.")
    }

    fun setAppStatus(appId: String, newStatus: AppStatus) {
        if (!currentUser.value.role.canApproveApps) {
            showSnackbar("Permission Denied: Only Admins can change app approval status.")
            return
        }
        repository.setAppStatus(appId, newStatus)
        showSnackbar("App status changed to ${newStatus.label}")
    }

    fun registerManualIp(appTitle: String, bundleId: String, rawCode: String) {
        if (!currentUser.value.role.canGenerateBlockchainProofs) {
            showSnackbar("Permission Denied: Developer role required to register IP.")
            return
        }
        val proof = repository.createManualBlockchainTimestamp(appTitle, bundleId, rawCode)
        _uiState.update { it.copy(isRegisterIpDialog = false, selectedProofForDetail = proof) }
        showSnackbar("Blockchain Proof registered at Block #${proof.blockHeight}!")
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
