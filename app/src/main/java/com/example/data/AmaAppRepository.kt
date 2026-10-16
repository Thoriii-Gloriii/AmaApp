package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

sealed class VerificationResult {
    data class VerifiedOriginal(
        val proof: BlockchainTimestampProof,
        val registrationDaysAgo: Long,
        val message: String
    ) : VerificationResult()

    data class SuspectedCloneDetected(
        val originalProof: BlockchainTimestampProof,
        val warningMessage: String
    ) : VerificationResult()

    data class NotRegistered(
        val searchedQuery: String,
        val message: String
    ) : VerificationResult()
}

class AmaAppRepository {

    // Pre-configured User Profiles for role demonstration
    val developerUser = UserProfile(
        id = "usr_dev_sarah",
        name = "Sarah Jenkins",
        email = "sarah.jenkins@novalabs.dev",
        handle = "@sarah_nova",
        role = UserRole.DEVELOPER,
        reputationScore = 2450,
        appsPublishedCount = 3,
        bugsReportedCount = 8,
        isVerified = true
    )

    val testerUser = UserProfile(
        id = "usr_tester_alex",
        name = "Alex Rivera",
        email = "alex.tester@betahunter.io",
        handle = "@alex_qa",
        role = UserRole.TESTER,
        reputationScore = 1890,
        appsPublishedCount = 0,
        bugsReportedCount = 47,
        isVerified = true
    )

    val adminUser = UserProfile(
        id = "usr_admin_marcus",
        name = "Marcus Vance",
        email = "marcus.vance@amaapp.io",
        handle = "@marcus_admin",
        role = UserRole.ADMIN,
        reputationScore = 9999,
        appsPublishedCount = 12,
        bugsReportedCount = 120,
        isVerified = true
    )

    private val _currentUser = MutableStateFlow<UserProfile>(developerUser)
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _apps = MutableStateFlow<List<AppListing>>(emptyList())
    val apps: StateFlow<List<AppListing>> = _apps.asStateFlow()

    private val _feedbackReports = MutableStateFlow<List<FeedbackReport>>(emptyList())
    val feedbackReports: StateFlow<List<FeedbackReport>> = _feedbackReports.asStateFlow()

    private val _forumThreads = MutableStateFlow<List<ForumThread>>(emptyList())
    val forumThreads: StateFlow<List<ForumThread>> = _forumThreads.asStateFlow()

    private val _blockchainProofs = MutableStateFlow<List<BlockchainTimestampProof>>(emptyList())
    val blockchainProofs: StateFlow<List<BlockchainTimestampProof>> = _blockchainProofs.asStateFlow()

    private val _recentDownloadLogs = MutableStateFlow<List<String>>(emptyList())
    val recentDownloadLogs: StateFlow<List<String>> = _recentDownloadLogs.asStateFlow()

    init {
        seedInitialData()
    }

    fun switchRole(role: UserRole) {
        _currentUser.value = when (role) {
            UserRole.DEVELOPER -> developerUser
            UserRole.TESTER -> testerUser
            UserRole.ADMIN -> adminUser
        }
    }

    // --- Cryptographic Blockchain Helpers ---
    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateBlockchainProof(
        appId: String,
        appTitle: String,
        bundleId: String,
        devName: String,
        version: String,
        customSalt: String = ""
    ): BlockchainTimestampProof {
        val rawData = "$appId::$bundleId::$appTitle::$devName::$version::$customSalt::${System.currentTimeMillis()}"
        val appDigest = sha256(rawData)
        val merkleRoot = sha256("$appDigest::0xPolygonMerkeLeaf::$bundleId")
        val blockHeight = 62_480_000L + (Math.random() * 25000).toLong()
        val blockHash = "0x" + sha256("block::$blockHeight::$merkleRoot").take(40)
        val txHash = "0x" + sha256("tx::$rawData::$blockHash").take(64)
        val signature = "0x" + sha256("secp256k1::sig::$txHash").take(64) + "1b"

        return BlockchainTimestampProof(
            proofId = "proof_" + UUID.randomUUID().toString().take(8),
            appId = appId,
            appTitle = appTitle,
            bundleId = bundleId,
            developerName = devName,
            sha256AppDigest = "sha256:" + appDigest.take(48),
            merkleRoot = "0x" + merkleRoot.take(40),
            blockHeight = blockHeight,
            blockHash = blockHash,
            transactionHash = txHash,
            timestamp = System.currentTimeMillis() - (86400000L * (3 + (Math.random() * 15).toInt())),
            network = "Polygon PoS / Ethereum State Anchor (L2)",
            status = "VERIFIED_IMMUTABLE",
            digitalSignature = signature
        )
    }

    // --- Seed Data ---
    private fun seedInitialData() {
        val app1Id = "app_lumina"
        val app1Bundle = "com.novalabs.lumina.editor"
        val proof1 = generateBlockchainProof(
            appId = app1Id,
            appTitle = "Lumina: AI Video Creator",
            bundleId = app1Bundle,
            devName = "Sarah Jenkins (NovaLabs)",
            version = "2.4.0",
            customSalt = "lumina_genesis_v1"
        )

        val app1Builds = listOf(
            AppBuild(
                id = "bld_lumina_240",
                appId = app1Id,
                versionName = "2.4.0-rc",
                buildNumber = 240,
                platform = AppPlatform.ANDROID_APK,
                binarySizeMb = 48.6,
                downloadUrl = "https://amaapp.dev/binaries/lumina-v2.4.0-release.apk",
                storeTestUrl = "https://play.google.com/apps/testing/com.novalabs.lumina.editor",
                releaseNotes = "Added HDR export pipeline, fixed latency on 4K preview scrubbing, improved battery efficiency.",
                sha256Hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                rolloutPercentage = 80,
                uploadedAt = System.currentTimeMillis() - 86400000L * 2,
                isLatest = true
            ),
            AppBuild(
                id = "bld_lumina_235",
                appId = app1Id,
                versionName = "2.3.5",
                buildNumber = 235,
                platform = AppPlatform.DUAL,
                binarySizeMb = 46.2,
                downloadUrl = "https://amaapp.dev/binaries/lumina-v2.3.5.apk",
                storeTestUrl = "https://testflight.apple.com/join/lumina-beta-235",
                releaseNotes = "Initial neural color grading beta and multi-track audio.",
                sha256Hash = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92",
                rolloutPercentage = 100,
                uploadedAt = System.currentTimeMillis() - 86400000L * 15,
                isLatest = false
            )
        )

        val app2Id = "app_aegis"
        val app2Bundle = "org.aegis.securevault"
        val proof2 = generateBlockchainProof(
            appId = app2Id,
            appTitle = "Aegis Zero-Knowledge Vault",
            bundleId = app2Bundle,
            devName = "CipherGuard Team",
            version = "1.8.2",
            customSalt = "aegis_zk_salt"
        )

        val app2Builds = listOf(
            AppBuild(
                id = "bld_aegis_182",
                appId = app2Id,
                versionName = "1.8.2-beta",
                buildNumber = 182,
                platform = AppPlatform.DUAL,
                binarySizeMb = 24.1,
                downloadUrl = "https://amaapp.dev/binaries/aegis-1.8.2-signed.apk",
                storeTestUrl = "https://play.google.com/apps/testing/org.aegis.securevault",
                releaseNotes = "Post-quantum lattice encryption upgrade and biometric backup.",
                sha256Hash = "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb",
                rolloutPercentage = 50,
                uploadedAt = System.currentTimeMillis() - 86400000L * 3,
                isLatest = true
            )
        )

        val app3Id = "app_orbit"
        val app3Bundle = "io.orbitflow.productivity"
        val proof3 = generateBlockchainProof(
            appId = app3Id,
            appTitle = "OrbitFlow: Spatial Task Planner",
            bundleId = app3Bundle,
            devName = "AeroLabs",
            version = "3.1.0",
            customSalt = "orbit_spatial_proof"
        )

        val app3Builds = listOf(
            AppBuild(
                id = "bld_orbit_310",
                appId = app3Id,
                versionName = "3.1.0-alpha",
                buildNumber = 310,
                platform = AppPlatform.IOS_IPA,
                binarySizeMb = 38.4,
                downloadUrl = "https://amaapp.dev/binaries/orbitflow-3.1.0.ipa",
                storeTestUrl = "https://testflight.apple.com/join/orbitflow-alpha",
                releaseNotes = "Gesture control integrations and calendar sync.",
                sha256Hash = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
                rolloutPercentage = 100,
                uploadedAt = System.currentTimeMillis() - 86400000L * 1,
                isLatest = true
            )
        )

        val appList = listOf(
            AppListing(
                id = app1Id,
                title = "Lumina: AI Video Creator",
                packageName = app1Bundle,
                shortPitch = "Real-time AI style transfer & 60fps mobile timeline video editor",
                description = "Lumina is a next-gen mobile video suite engineered for creators. Features neural keyframing, AI voice enhancement, real-time LUT previews, and direct 4K YouTube/TikTok export. We need testers on Snapdragon 8 Gen 2/3 and Tensor devices to benchmark thermal limits during 10-minute 4K renders.",
                category = "Media & Video",
                developerId = "usr_dev_sarah",
                developerName = "Sarah Jenkins (NovaLabs)",
                developerVerified = true,
                platform = AppPlatform.ANDROID_APK,
                rating = 4.8,
                totalDownloads = 4820,
                activeTesters = 340,
                crashFreeRate = 99.4,
                status = AppStatus.ACTIVE_BETA,
                isApprovedByAdmin = true,
                blockchainProofId = proof1.proofId,
                createdAt = System.currentTimeMillis() - 86400000L * 22,
                builds = app1Builds
            ),
            AppListing(
                id = app2Id,
                title = "Aegis Zero-Knowledge Vault",
                packageName = app2Bundle,
                shortPitch = "End-to-end encrypted password and document vault with decentralized cloud sync",
                description = "Aegis protects credentials, seed phrases, and identity records using client-side Argon2id key derivation and AES-256-GCM. No metadata is visible to servers. Looking for penetration testers and usability feedback on autofill services across Android 14 and 15.",
                category = "Security & Tools",
                developerId = "usr_dev_cipher",
                developerName = "CipherGuard Team",
                developerVerified = true,
                platform = AppPlatform.DUAL,
                rating = 4.9,
                totalDownloads = 8900,
                activeTesters = 720,
                crashFreeRate = 99.8,
                status = AppStatus.OPEN_TESTING,
                isApprovedByAdmin = true,
                blockchainProofId = proof2.proofId,
                createdAt = System.currentTimeMillis() - 86400000L * 35,
                builds = app2Builds
            ),
            AppListing(
                id = app3Id,
                title = "OrbitFlow: Spatial Task Planner",
                packageName = app3Bundle,
                shortPitch = "Visual 3D node-based task orchestration and calendar companion",
                description = "Break free from boring lists! OrbitFlow lets you arrange projects like planetary systems with gravitational priority and focus timer rings. Seeking iOS TestFlight feedback and direct APK testers for touch gesture fidelity.",
                category = "Productivity",
                developerId = "usr_dev_aero",
                developerName = "AeroLabs",
                developerVerified = true,
                platform = AppPlatform.IOS_IPA,
                rating = 4.6,
                totalDownloads = 2150,
                activeTesters = 190,
                crashFreeRate = 98.9,
                status = AppStatus.STAGED_ROLLOUT,
                isApprovedByAdmin = true,
                blockchainProofId = proof3.proofId,
                createdAt = System.currentTimeMillis() - 86400000L * 10,
                builds = app3Builds
            )
        )

        val sampleFeedback = listOf(
            FeedbackReport(
                id = "fb_01",
                appId = app1Id,
                appTitle = "Lumina: AI Video Creator",
                testerId = "usr_tester_alex",
                testerName = "Alex Rivera",
                type = FeedbackType.BUG_REPORT,
                severity = FeedbackSeverity.CRITICAL_CRASH,
                rating = 4,
                title = "Crash when applying Neural Glow to 120fps video clips",
                description = "Steps to reproduce: 1. Import a 120fps slow-mo clip. 2. Tap Filters -> Neural Glow. 3. Scrub playhead past 3 seconds. App exits unexpectedly with SIGSEGV in liblumina_gpu.so.",
                deviceModel = "Pixel 8 Pro",
                osVersion = "Android 15 (AP2A.240905.003)",
                status = "Fixed in v2.4.1",
                developerResponse = "Thanks Alex! We tracked down an out-of-bounds GPU texture memory clamp on high-refresh clips. Patch is compiled and rolling out in build #241."
            ),
            FeedbackReport(
                id = "fb_02",
                appId = app1Id,
                appTitle = "Lumina: AI Video Creator",
                testerId = "usr_tester_dave",
                testerName = "Dave K.",
                type = FeedbackType.FEATURE_REQUEST,
                severity = FeedbackSeverity.ENHANCEMENT,
                rating = 5,
                title = "Add support for custom .cube LUT file imports from device storage",
                description = "Professional video editors rely heavily on custom LUTs graded in DaVinci Resolve. Adding a local file picker to import .cube files would make Lumina an instant staple in my workflow.",
                deviceModel = "Samsung Galaxy S24 Ultra",
                osVersion = "Android 14 (OneUI 6.1)",
                status = "Planned for v2.5.0",
                developerResponse = "Great suggestion! The SAF file picker for .cube files is currently in development branch."
            ),
            FeedbackReport(
                id = "fb_03",
                appId = app2Id,
                appTitle = "Aegis Zero-Knowledge Vault",
                testerId = "usr_tester_alex",
                testerName = "Alex Rivera",
                type = FeedbackType.USABILITY,
                severity = FeedbackSeverity.MINOR_GLITCH,
                rating = 5,
                title = "Biometric prompt dismisses too quickly on retry",
                description = "When fingerprint verification fails on the first attempt, the bottom sheet collapses instead of giving a second chance.",
                deviceModel = "OnePlus 12",
                osVersion = "Android 14",
                status = "Under Review",
                developerResponse = "Investigating BiometricPrompt callback error codes on OxygenOS."
            )
        )

        val sampleThreads = listOf(
            ForumThread(
                id = "th_01",
                appId = app1Id,
                appTitle = "Lumina: AI Video Creator",
                title = "Lumina v2.4.0 Release Candidate: Testing Focus & Known Issues",
                content = "Hey testers! Build #240 is now live on AmaApp for direct APK download and Play Store Closed Testing. Please stress-test the new HDR rendering pipeline and report any frame drops or battery spikes.",
                authorName = "Sarah Jenkins",
                authorRole = UserRole.DEVELOPER,
                category = "#announcements",
                upvotes = 34,
                repliesCount = 12,
                isPinned = true
            ),
            ForumThread(
                id = "th_02",
                appId = app1Id,
                appTitle = "Lumina: AI Video Creator",
                title = "Best rendering performance achieved on Tensor G3 vs Snapdragon 8 Gen 3",
                content = "Ran benchmarks across both chipsets with a 60-second 4K timeline containing 3 video layers and 2 audio filters. Here are the average render times and temperatures...",
                authorName = "Alex Rivera",
                authorRole = UserRole.TESTER,
                category = "#general-testing",
                upvotes = 28,
                repliesCount = 7
            ),
            ForumThread(
                id = "th_03",
                appId = app2Id,
                appTitle = "Aegis Zero-Knowledge Vault",
                title = "Feature Request: Hardware Security Key (YubiKey NFC) as 2FA backup",
                content = "Would love to see native FIDO2 / WebAuthn NFC key support in addition to the standard TOTP authenticator.",
                authorName = "CyberSam",
                authorRole = UserRole.TESTER,
                category = "#feature-requests",
                upvotes = 42,
                repliesCount = 5
            ),
            ForumThread(
                id = "th_04",
                title = "AmaApp Blockchain IP Protection: How to verify your timestamp certificate",
                content = "Welcome all developers! Remember that every app build uploaded to AmaApp calculates a cryptographic SHA-256 digest and records an immutable block proof on Polygon/Ethereum. If someone copies your app, your timestamp certificate is legally admissible chronological evidence.",
                authorName = "Marcus Vance",
                authorRole = UserRole.ADMIN,
                category = "#announcements",
                upvotes = 91,
                repliesCount = 19,
                isPinned = true
            )
        )

        _apps.value = appList
        _feedbackReports.value = sampleFeedback
        _forumThreads.value = sampleThreads
        _blockchainProofs.value = listOf(proof1, proof2, proof3)
    }

    // --- Developer Actions ---
    fun registerNewApp(
        title: String,
        packageName: String,
        shortPitch: String,
        description: String,
        category: String,
        platform: AppPlatform,
        initialVersionName: String,
        binarySizeMb: Double,
        releaseNotes: String,
        downloadUrl: String = "https://amaapp.dev/binaries/download.apk",
        storeUrl: String = "https://play.google.com/store/apps/details?id=$packageName"
    ): AppListing {
        val appId = "app_" + UUID.randomUUID().toString().take(8)
        val dev = _currentUser.value

        // Automatically create Blockchain IP Timestamp Proof
        val proof = generateBlockchainProof(
            appId = appId,
            appTitle = title,
            bundleId = packageName,
            devName = dev.name,
            version = initialVersionName,
            customSalt = "amaapp_registered_${System.currentTimeMillis()}"
        )

        val build = AppBuild(
            id = "bld_" + UUID.randomUUID().toString().take(8),
            appId = appId,
            versionName = initialVersionName,
            buildNumber = 1,
            platform = platform,
            binarySizeMb = binarySizeMb,
            downloadUrl = downloadUrl,
            storeTestUrl = storeUrl,
            releaseNotes = releaseNotes,
            sha256Hash = sha256("$appId::$packageName::$initialVersionName"),
            rolloutPercentage = 100,
            uploadedAt = System.currentTimeMillis(),
            isLatest = true
        )

        val newApp = AppListing(
            id = appId,
            title = title,
            packageName = packageName,
            shortPitch = shortPitch,
            description = description,
            category = category,
            developerId = dev.id,
            developerName = dev.name,
            developerVerified = true,
            platform = platform,
            rating = 5.0,
            totalDownloads = 1,
            activeTesters = 1,
            crashFreeRate = 100.0,
            status = AppStatus.ACTIVE_BETA,
            isApprovedByAdmin = true,
            blockchainProofId = proof.proofId,
            createdAt = System.currentTimeMillis(),
            builds = listOf(build)
        )

        _blockchainProofs.value = listOf(proof) + _blockchainProofs.value
        _apps.value = listOf(newApp) + _apps.value
        return newApp
    }

    fun uploadNewBuild(
        appId: String,
        versionName: String,
        buildNumber: Int,
        platform: AppPlatform,
        binarySizeMb: Double,
        releaseNotes: String,
        rolloutPercent: Int,
        downloadUrl: String = "https://amaapp.dev/binaries/new_build.apk",
        storeUrl: String = "https://play.google.com/apps/testing/"
    ) {
        val currentApps = _apps.value.toMutableList()
        val appIndex = currentApps.indexOfFirst { it.id == appId }
        if (appIndex >= 0) {
            val app = currentApps[appIndex]
            val updatedOldBuilds = app.builds.map { it.copy(isLatest = false) }

            val newBuild = AppBuild(
                id = "bld_" + UUID.randomUUID().toString().take(8),
                appId = appId,
                versionName = versionName,
                buildNumber = buildNumber,
                platform = platform,
                binarySizeMb = binarySizeMb,
                downloadUrl = downloadUrl,
                storeTestUrl = storeUrl,
                releaseNotes = releaseNotes,
                sha256Hash = sha256("$appId::$versionName::$buildNumber"),
                rolloutPercentage = rolloutPercent,
                uploadedAt = System.currentTimeMillis(),
                isLatest = true
            )

            val updatedApp = app.copy(
                builds = listOf(newBuild) + updatedOldBuilds,
                status = if (rolloutPercent < 100) AppStatus.STAGED_ROLLOUT else app.status
            )
            currentApps[appIndex] = updatedApp
            _apps.value = currentApps
        }
    }

    fun updateRolloutPercentage(appId: String, buildId: String, percentage: Int) {
        val currentApps = _apps.value.toMutableList()
        val appIndex = currentApps.indexOfFirst { it.id == appId }
        if (appIndex >= 0) {
            val app = currentApps[appIndex]
            val updatedBuilds = app.builds.map {
                if (it.id == buildId) it.copy(rolloutPercentage = percentage) else it
            }
            currentApps[appIndex] = app.copy(builds = updatedBuilds)
            _apps.value = currentApps
        }
    }

    // --- Tester Actions ---
    fun recordDownload(appId: String, buildVersion: String, targetPlatform: String) {
        val currentApps = _apps.value.toMutableList()
        val appIndex = currentApps.indexOfFirst { it.id == appId }
        if (appIndex >= 0) {
            val app = currentApps[appIndex]
            currentApps[appIndex] = app.copy(
                totalDownloads = app.totalDownloads + 1,
                activeTesters = app.activeTesters + 1
            )
            _apps.value = currentApps
        }
        val log = "Downloaded ${currentApps.firstOrNull { it.id == appId }?.title ?: "App"} ($buildVersion) via $targetPlatform"
        _recentDownloadLogs.value = listOf(log) + _recentDownloadLogs.value.take(9)
    }

    fun submitFeedback(
        appId: String,
        type: FeedbackType,
        severity: FeedbackSeverity,
        rating: Int,
        title: String,
        description: String,
        deviceModel: String,
        osVersion: String
    ) {
        val app = _apps.value.firstOrNull { it.id == appId } ?: return
        val user = _currentUser.value
        val report = FeedbackReport(
            id = "fb_" + UUID.randomUUID().toString().take(8),
            appId = appId,
            appTitle = app.title,
            testerId = user.id,
            testerName = user.name,
            type = type,
            severity = severity,
            rating = rating,
            title = title,
            description = description,
            deviceModel = deviceModel,
            osVersion = osVersion,
            status = "New / Awaiting Review",
            developerResponse = null,
            timestamp = System.currentTimeMillis()
        )
        _feedbackReports.value = listOf(report) + _feedbackReports.value
    }

    // --- Developer Responds to Feedback ---
    fun respondToFeedback(feedbackId: String, response: String, newStatus: String) {
        _feedbackReports.value = _feedbackReports.value.map {
            if (it.id == feedbackId) {
                it.copy(developerResponse = response, status = newStatus)
            } else it
        }
    }

    // --- Forum Actions ---
    fun createForumThread(title: String, content: String, category: String, appId: String? = null) {
        val user = _currentUser.value
        val app = _apps.value.firstOrNull { it.id == appId }
        val thread = ForumThread(
            id = "th_" + UUID.randomUUID().toString().take(8),
            appId = appId,
            appTitle = app?.title,
            title = title,
            content = content,
            authorName = user.name,
            authorRole = user.role,
            category = category,
            upvotes = 1,
            repliesCount = 0,
            isPinned = false,
            isLocked = false,
            timestamp = System.currentTimeMillis()
        )
        _forumThreads.value = listOf(thread) + _forumThreads.value
    }

    fun upvoteThread(threadId: String) {
        _forumThreads.value = _forumThreads.value.map {
            if (it.id == threadId) it.copy(upvotes = it.upvotes + 1) else it
        }
    }

    // --- Admin Actions ---
    fun togglePinThread(threadId: String) {
        _forumThreads.value = _forumThreads.value.map {
            if (it.id == threadId) it.copy(isPinned = !it.isPinned) else it
        }
    }

    fun deleteForumThread(threadId: String) {
        _forumThreads.value = _forumThreads.value.filter { it.id != threadId }
    }

    fun setAppStatus(appId: String, newStatus: AppStatus) {
        _apps.value = _apps.value.map {
            if (it.id == appId) it.copy(status = newStatus) else it
        }
    }

    // --- Blockchain IP Verification & Anti-Clone Proof Engine ---
    fun verifyAppIntellectualProperty(query: String): VerificationResult {
        val cleanQuery = query.trim().lowercase()
        if (cleanQuery.isEmpty()) {
            return VerificationResult.NotRegistered("", "Please enter a package name, app title, or SHA-256 hash.")
        }

        // Check if matches an existing verified proof
        val matchedProof = _blockchainProofs.value.firstOrNull { proof ->
            proof.bundleId.lowercase().contains(cleanQuery) ||
                    proof.sha256AppDigest.lowercase().contains(cleanQuery) ||
                    proof.appTitle.lowercase().contains(cleanQuery) ||
                    proof.transactionHash.lowercase().contains(cleanQuery)
        }

        if (matchedProof != null) {
            val daysAgo = (System.currentTimeMillis() - matchedProof.timestamp) / 86400000L
            return VerificationResult.VerifiedOriginal(
                proof = matchedProof,
                registrationDaysAgo = daysAgo.coerceAtLeast(1),
                message = "AUTHENTIC ORIGINAL VERIFIED: Timestamped $daysAgo days ago on Polygon/Ethereum block #${matchedProof.blockHeight}. Author: ${matchedProof.developerName}."
            )
        }

        // Check if query resembles a known package name with suspicious variation (clone detection)
        val suspiciousMatch = _apps.value.firstOrNull { app ->
            val cleanTitle = app.title.lowercase()
            cleanTitle.contains(cleanQuery) || cleanQuery.contains(cleanTitle.take(5))
        }

        if (suspiciousMatch != null) {
            val origProof = _blockchainProofs.value.firstOrNull { it.appId == suspiciousMatch.id }
            if (origProof != null) {
                return VerificationResult.SuspectedCloneDetected(
                    originalProof = origProof,
                    warningMessage = "SUSPECTED CLONE DETECTED: This candidate claims title similarity to '${suspiciousMatch.title}', but has no prior blockchain signature. The genuine original was registered by ${origProof.developerName} at Block #${origProof.blockHeight}."
                )
            }
        }

        return VerificationResult.NotRegistered(
            searchedQuery = query,
            message = "No immutable blockchain proof found for '$query'. If this is your proprietary software, register it immediately in the IP Vault to protect your ownership."
        )
    }

    fun createManualBlockchainTimestamp(
        appTitle: String,
        bundleId: String,
        rawCodeOrBinary: String
    ): BlockchainTimestampProof {
        val dev = _currentUser.value
        val proof = generateBlockchainProof(
            appId = "app_custom_" + UUID.randomUUID().toString().take(6),
            appTitle = appTitle,
            bundleId = bundleId,
            devName = dev.name,
            version = "1.0.0",
            customSalt = rawCodeOrBinary
        )
        _blockchainProofs.value = listOf(proof) + _blockchainProofs.value
        return proof
    }
}
