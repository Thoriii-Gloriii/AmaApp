package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminControlScreen(
    currentUser: UserProfile,
    apps: List<AppListing>,
    feedbackReports: List<FeedbackReport>,
    proofs: List<BlockchainTimestampProof>,
    onSetAppStatus: (String, AppStatus) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!currentUser.role.canApproveApps) {
        // Restrict non-admins
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(StatusBug.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = StatusBug,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "SuperAdmin Console Restricted",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "You are currently signed in as '${currentUser.role.title}'. Overarching governance, security auditing, and app status enforcement require Admin clearance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Button(
                        onClick = { onSwitchRole(UserRole.ADMIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusBug),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_switch_to_admin")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch to SuperAdmin")
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_control_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "AmaApp Governance Console",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Platform security, app verification & permissions matrix",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Platform Health Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("APPS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${apps.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = CyanPrimary)
                        Text("All verified", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("REPORTS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${feedbackReports.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = StatusFeature)
                        Text("Active bug triage", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("CHAIN PROOFS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${proofs.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = EmeraldTertiary)
                        Text("Syncing L2", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Section: App Moderation & Status Management
        item {
            Text(
                text = "App Distribution Moderation Queue",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(apps, key = { it.id }) { app ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = app.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${app.packageName} • Dev: ${app.developerName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(status = app.status)
                    }

                    // Admin Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (app.status != AppStatus.ACTIVE_BETA) {
                            FilledTonalButton(
                                onClick = { onSetAppStatus(app.id, AppStatus.ACTIVE_BETA) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve Beta", fontSize = 11.sp)
                            }
                        }

                        if (app.status != AppStatus.UNDER_REVIEW) {
                            OutlinedButton(
                                onClick = { onSetAppStatus(app.id, AppStatus.UNDER_REVIEW) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Hold Review", fontSize = 11.sp)
                            }
                        }

                        if (app.status != AppStatus.FLAGGED) {
                            Button(
                                onClick = { onSetAppStatus(app.id, AppStatus.FLAGGED) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusBug),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Suspend", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section: Role & Permission Matrix
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Security & Permission Governance Matrix",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PermissionRow("Upload APK/iOS Builds", dev = true, tester = false, admin = true)
                    PermissionRow("Manage Versions & Rollout", dev = true, tester = false, admin = true)
                    PermissionRow("Download & Test Builds", dev = true, tester = true, admin = true)
                    PermissionRow("Submit Bugs & Telemetry", dev = true, tester = true, admin = true)
                    PermissionRow("Respond to Tester Bugs", dev = true, tester = false, admin = true)
                    PermissionRow("Generate Blockchain IP Proofs", dev = true, tester = false, admin = true)
                    PermissionRow("Moderate Forum Threads", dev = false, tester = false, admin = true)
                    PermissionRow("Override App Distribution", dev = false, tester = false, admin = true)
                }
            }
        }
    }
}

@Composable
fun PermissionRow(
    feature: String,
    dev: Boolean,
    tester: Boolean,
    admin: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.4f)
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BadgeIcon(dev, VioletSecondary)
            BadgeIcon(tester, CyanPrimary)
            BadgeIcon(admin, StatusBug)
        }
    }
}

@Composable
fun BadgeIcon(allowed: Boolean, color: Color) {
    Icon(
        imageVector = if (allowed) Icons.Default.CheckCircle else Icons.Default.Cancel,
        contentDescription = null,
        tint = if (allowed) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        modifier = Modifier.size(16.dp)
    )
}
