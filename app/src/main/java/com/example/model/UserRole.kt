package com.example.model

enum class UserRole(
    val title: String,
    val badge: String,
    val description: String
) {
    DEVELOPER(
        title = "Developer",
        badge = "DEV",
        description = "Post APK/iOS builds, monitor crash analytics & protect IP"
    ),
    TESTER(
        title = "Tester",
        badge = "BETA",
        description = "Test cutting-edge apps, report bugs & earn tester rank"
    ),
    ADMIN(
        title = "Admin",
        badge = "ROOT",
        description = "Overarching platform governance, app approvals & moderation"
    );

    val canUploadApps: Boolean get() = this == DEVELOPER || this == ADMIN
    val canViewAnalytics: Boolean get() = this == DEVELOPER || this == ADMIN
    val canManageVersions: Boolean get() = this == DEVELOPER || this == ADMIN
    val canSubmitFeedback: Boolean get() = this == TESTER || this == DEVELOPER || this == ADMIN
    val canModerateForum: Boolean get() = this == ADMIN
    val canApproveApps: Boolean get() = this == ADMIN
    val canGenerateBlockchainProofs: Boolean get() = this == DEVELOPER || this == ADMIN
    val canManageUsers: Boolean get() = this == ADMIN
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val handle: String,
    val role: UserRole,
    val reputationScore: Int = 1250,
    val appsPublishedCount: Int = 3,
    val bugsReportedCount: Int = 18,
    val isVerified: Boolean = true
)
