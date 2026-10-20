package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmaTopBar(
    currentUser: UserProfile,
    onRoleChange: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyanPrimary, VioletSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "AmaApp Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AmaApp",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "v2.0",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyanPrimary,
                            modifier = Modifier
                                .background(CyanPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "Builds • Feedback • IP Proof",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Role Switcher Chip
            Box {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (currentUser.role) {
                        UserRole.DEVELOPER -> VioletSecondary.copy(alpha = 0.18f)
                        UserRole.TESTER -> CyanPrimary.copy(alpha = 0.18f)
                        UserRole.ADMIN -> StatusBug.copy(alpha = 0.18f)
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (currentUser.role) {
                            UserRole.DEVELOPER -> VioletSecondary
                            UserRole.TESTER -> CyanPrimary
                            UserRole.ADMIN -> StatusBug
                        }
                    ),
                    modifier = Modifier
                        .testTag("role_switcher_chip")
                        .clickable { roleMenuExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (currentUser.role) {
                                        UserRole.DEVELOPER -> VioletSecondary
                                        UserRole.TESTER -> CyanPrimary
                                        UserRole.ADMIN -> StatusBug
                                    }
                                )
                        )
                        Text(
                            text = "${currentUser.role.badge}: ${currentUser.name.split(" ").first()}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Switch Role",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = "Developer (Sarah Jenkins)",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Upload APK/iOS, manage versions & analytics",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Code, contentDescription = null, tint = VioletSecondary)
                        },
                        onClick = {
                            onRoleChange(UserRole.DEVELOPER)
                            roleMenuExpanded = false
                        },
                        modifier = Modifier.testTag("role_item_developer")
                    )

                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = "Tester (Alex Rivera)",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Download APKs, submit bugs & test feedback",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.BugReport, contentDescription = null, tint = CyanPrimary)
                        },
                        onClick = {
                            onRoleChange(UserRole.TESTER)
                            roleMenuExpanded = false
                        },
                        modifier = Modifier.testTag("role_item_tester")
                    )

                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = "SuperAdmin (Marcus Vance)",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Governance, app review & audit controls",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = StatusBug)
                        },
                        onClick = {
                            onRoleChange(UserRole.ADMIN)
                            roleMenuExpanded = false
                        },
                        modifier = Modifier.testTag("role_item_admin")
                    )
                }
            }
        }
    }
}

@Composable
fun RolePermissionBanner(
    user: UserProfile,
    modifier: Modifier = Modifier
) {
    val gradient = when (user.role) {
        UserRole.DEVELOPER -> Brush.horizontalGradient(listOf(PaleSage, CardCream))
        UserRole.TESTER -> Brush.horizontalGradient(listOf(PaleSage, CardCream))
        UserRole.ADMIN -> Brush.horizontalGradient(listOf(PaleSage, CardCream))
    }
    val accentColor = when (user.role) {
        UserRole.DEVELOPER -> VioletSecondaryLight
        UserRole.TESTER -> CyanPrimary
        UserRole.ADMIN -> StatusBug
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
        color = Color.Transparent
    ) {
        Box(modifier = Modifier.background(gradient)) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (user.role) {
                            UserRole.DEVELOPER -> Icons.Default.Terminal
                            UserRole.TESTER -> Icons.Default.Verified
                            UserRole.ADMIN -> Icons.Default.Security
                        },
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${user.role.title} Permissions Active",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = accentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rep: ${user.reputationScore}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = user.role.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformBadge(platform: AppPlatform, modifier: Modifier = Modifier) {
    val (label, bg, fg, icon) = when (platform) {
        AppPlatform.ANDROID_APK -> Quadruple(
            "Android APK / Play",
            Color(0xFFE3EED3),
            Color(0xFF2E6B34),
            Icons.Default.Android
        )
        AppPlatform.IOS_IPA -> Quadruple(
            "iOS IPA / TestFlight",
            Color(0xFFDCE9F5),
            Color(0xFF1F5F8B),
            Icons.Default.PhoneIphone
        )
        AppPlatform.DUAL -> Quadruple(
            "Dual Android & iOS",
            Color(0xFFEAE1F5),
            Color(0xFF5B3A8E),
            Icons.Default.Devices
        )
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = fg
            )
        }
    }
}

@Composable
fun StatusBadge(status: AppStatus, modifier: Modifier = Modifier) {
    val color = Color(status.colorHex)
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun MetricChip(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color = CyanPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BlockchainCertificateCard(
    proof: BlockchainTimestampProof,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardCream,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Olive),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(CardCream, PaleSage)
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Header Stamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified Proof",
                            tint = EmeraldTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "IMMUTABLE IP TIMESTAMP",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = EmeraldTertiary
                        )
                    }

                    Text(
                        text = "Block #${proof.blockHeight}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CyanPrimary
                    )
                }

                HorizontalDivider(color = WarmBorder)

                // App & Author Info
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = proof.appTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Avocado
                    )
                    Text(
                        text = "Bundle ID: ${proof.bundleId} • Dev: ${proof.developerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreen
                    )
                }

                // Cryptographic Digest & Hashes
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Cream,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "SHA-256 APP DIGEST:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MutedGreen
                        )
                        Text(
                            text = proof.sha256AppDigest,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            color = CyanPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "TRANSACTION HASH (ON-CHAIN):",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MutedGreen
                        )
                        Text(
                            text = proof.transactionHash,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            color = VioletSecondaryLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Legal Protection Notice
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = BlockchainGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Legally binding chronological priority proof against copycats.",
                        style = MaterialTheme.typography.labelSmall,
                        color = BlockchainGold
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
