package com.example.model

enum class AppPlatform(val label: String, val badgeColor: Long) {
    ANDROID_APK("Android (APK / Play)", 0xFF3F7A3A),
    IOS_IPA("iOS (IPA / TestFlight)", 0xFF007AFF),
    DUAL("Android & iOS Dual", 0xFF6B7F3A)
}

enum class AppStatus(val label: String, val colorHex: Long) {
    ACTIVE_BETA("Active Beta", 0xFF3F7A3A),
    OPEN_TESTING("Open Testing", 0xFF2F6F7C),
    CLOSED_TESTING("Closed Testing", 0xFFB07A10),
    STAGED_ROLLOUT("Staged Rollout", 0xFF6B7F3A),
    UNDER_REVIEW("Under Review", 0xFF9E9E9E),
    FLAGGED("Flagged / Suspended", 0xFFC0443A)
}

data class AppBuild(
    val id: String,
    val appId: String,
    val versionName: String,
    val buildNumber: Int,
    val platform: AppPlatform,
    val binarySizeMb: Double,
    val downloadUrl: String,
    val storeTestUrl: String,
    val releaseNotes: String,
    val sha256Hash: String,
    val rolloutPercentage: Int = 100,
    val uploadedAt: Long = System.currentTimeMillis(),
    val isLatest: Boolean = true
)

data class AppListing(
    val id: String,
    val title: String,
    val packageName: String,
    val shortPitch: String,
    val description: String,
    val category: String,
    val developerId: String,
    val developerName: String,
    val developerVerified: Boolean = true,
    val platform: AppPlatform,
    val rating: Double,
    val totalDownloads: Int,
    val activeTesters: Int,
    val crashFreeRate: Double,
    val status: AppStatus,
    val isApprovedByAdmin: Boolean = true,
    val blockchainProofId: String? = null,
    val createdAt: Long = System.currentTimeMillis() - 86400000L * 14,
    val builds: List<AppBuild> = emptyList()
)

enum class FeedbackType(val label: String) {
    BUG_REPORT("Bug Report"),
    FEATURE_REQUEST("Feature Request"),
    USABILITY("UX & Design"),
    PERFORMANCE("Performance")
}

enum class FeedbackSeverity(val label: String, val colorHex: Long) {
    CRITICAL_CRASH("Critical Crash", 0xFFC0443A),
    MAJOR_ISSUE("Major Issue", 0xFFB8592E),
    MINOR_GLITCH("Minor Glitch", 0xFFB07A10),
    COSMETIC_POLISH("Cosmetic / Polish", 0xFF2F6F7C),
    ENHANCEMENT("Enhancement / Feature", 0xFF3F7A3A)
}

data class FeedbackReport(
    val id: String,
    val appId: String,
    val appTitle: String,
    val testerId: String,
    val testerName: String,
    val type: FeedbackType,
    val severity: FeedbackSeverity,
    val rating: Int,
    val title: String,
    val description: String,
    val deviceModel: String,
    val osVersion: String,
    val status: String,
    val developerResponse: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class ForumThread(
    val id: String,
    val appId: String? = null,
    val appTitle: String? = null,
    val title: String,
    val content: String,
    val authorName: String,
    val authorRole: UserRole,
    val category: String,
    val upvotes: Int = 0,
    val repliesCount: Int = 0,
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class ForumReply(
    val id: String,
    val threadId: String,
    val authorName: String,
    val authorRole: UserRole,
    val content: String,
    val isDeveloperSolution: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class BlockchainTimestampProof(
    val proofId: String,
    val appId: String,
    val appTitle: String,
    val bundleId: String,
    val developerName: String,
    val sha256AppDigest: String,
    val merkleRoot: String,
    val blockHeight: Long,
    val blockHash: String,
    val transactionHash: String,
    val timestamp: Long,
    val network: String = "Polygon Mainnet (Ethereum L2 Proof-of-Origin)",
    val status: String = "IMMUTABLE_CONFIRMED",
    val digitalSignature: String
)
