package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppBuild
import com.example.model.AppListing
import com.example.model.AppPlatform
import com.example.model.FeedbackReport
import com.example.ui.components.AmaAppIcon
import com.example.ui.components.AmaPrimaryButton
import com.example.ui.components.AmaSectionHeader
import com.example.ui.components.RatingSummary
import com.example.ui.components.StatusBadge
import com.example.ui.components.shortLabel
import com.example.ui.theme.Avocado
import com.example.ui.theme.CardCream
import com.example.ui.theme.Gold
import com.example.ui.theme.MutedGreen
import com.example.ui.theme.PaleSage
import com.example.ui.theme.Success
import com.example.ui.theme.WarmBorder

@Composable
fun AppDetailsScreen(
    app: AppListing,
    summary: RatingSummary?,
    reviews: List<FeedbackReport>,
    isSaved: Boolean,
    isDownloaded: Boolean,
    onBack: () -> Unit,
    onToggleSaved: () -> Unit,
    onDownload: (AppBuild, Boolean) -> Unit,
    onDebug: () -> Unit,
    onSeeAllReviews: () -> Unit,
    onMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    val build = app.builds.firstOrNull { it.isLatest } ?: app.builds.firstOrNull()
    val isIos = app.platform == AppPlatform.IOS_IPA

    fun share() {
        val text = buildString {
            append("Check out ${app.title} on AmaApp")
            if (app.shortPitch.isNotBlank()) append(" - ${app.shortPitch}")
            append("\nPackage: ${app.packageName}")
        }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, app.title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(send, "Share ${app.title}"))
    }

    Column(modifier = modifier.testTag("app_details_screen")) {
        // Top app bar: Back / Bookmark / More
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_details_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Avocado)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onToggleSaved, modifier = Modifier.testTag("btn_bookmark")) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isSaved) "Remove from saved" else "Save app",
                    tint = Avocado
                )
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = Avocado)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Share") },
                        onClick = { menuOpen = false; share() }
                    )
                    DropdownMenuItem(
                        text = { Text("Report a bug") },
                        onClick = { menuOpen = false; onDebug() }
                    )
                    DropdownMenuItem(
                        text = { Text("All reviews") },
                        onClick = { menuOpen = false; onSeeAllReviews() }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            // Identity group
            Row(verticalAlignment = Alignment.CenterVertically) {
                AmaAppIcon(category = app.category, size = 72.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = app.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Avocado
                    )
                    Text(text = "By ${app.developerName}", fontSize = 13.sp, color = MutedGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (summary != null) {
                                "%.1f (%d reviews)".format(summary.average, summary.count)
                            } else {
                                "Not rated yet"
                            },
                            fontSize = 12.sp,
                            color = MutedGreen
                        )
                    }
                    // Only shown when the listing really is verified and approved
                    if (app.developerVerified && app.isApprovedByAdmin) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = Success,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Verified Developer",
                                fontSize = 12.sp,
                                color = Success,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Build information (integrity + compatibility)
            if (build != null) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CardCream,
                    border = BorderStroke(1.dp, WarmBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        InfoRow("Latest build", "v${build.versionName} (#${build.buildNumber})")
                        InfoRow("Size", "${build.binarySizeMb} MB")
                        InfoRow("Platform", app.platform.shortLabel())
                        InfoRow("Rollout", "${build.rolloutPercentage}%")
                        InfoRow("SHA-256", build.sha256Hash.take(16) + "...", mono = true)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // About
            Text(
                text = "About This App",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Avocado
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = app.description,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = MutedGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TagChip(app.category)
                TagChip(app.platform.shortLabel())
                StatusBadge(status = app.status)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary action: Download
            AmaPrimaryButton(
                label = when {
                    build == null -> "No build available"
                    isDownloaded -> "Download again"
                    isIos -> "Get on TestFlight"
                    else -> "Download"
                },
                icon = Icons.Default.Download,
                enabled = build != null,
                modifier = Modifier.testTag("btn_details_download"),
                onClick = { if (build != null) onDownload(build, !isIos) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary actions (intentionally quieter than Download)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardCream,
                border = BorderStroke(1.dp, WarmBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SecondaryAction(
                        icon = Icons.Default.BugReport,
                        label = "Debug",
                        modifier = Modifier.weight(1f).testTag("btn_debug"),
                        onClick = onDebug
                    )
                    Box(modifier = Modifier.width(1.dp).height(56.dp).background(WarmBorder))
                    SecondaryAction(
                        icon = Icons.Default.Code,
                        label = "View Code",
                        modifier = Modifier.weight(1f).testTag("btn_view_code"),
                        onClick = { onMessage("The developer hasn't linked a source repository yet.") }
                    )
                    Box(modifier = Modifier.width(1.dp).height(56.dp).background(WarmBorder))
                    SecondaryAction(
                        icon = Icons.Default.Share,
                        label = "Share",
                        modifier = Modifier.weight(1f).testTag("btn_share"),
                        onClick = { share() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reviews preview
            AmaSectionHeader(
                title = "Reviews",
                actionLabel = "See All",
                onAction = onSeeAllReviews
            )
            val latest = reviews.sortedByDescending { it.timestamp }.take(2)
            if (latest.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No reviews yet. Be the first to review this app.",
                    fontSize = 13.sp,
                    color = MutedGreen
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onSeeAllReviews,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Avocado),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Avocado)
                ) { Text("Write a review") }
            } else {
                latest.forEachIndexed { index, report ->
                    AmaReviewRow(report)
                    if (index != latest.lastIndex) HorizontalDivider(color = WarmBorder)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, mono: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 13.sp, color = MutedGreen)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Avocado,
            fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
        )
    }
}

@Composable
private fun TagChip(label: String) {
    Surface(shape = CircleShape, color = PaleSage) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Avocado,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SecondaryAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Avocado)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 12.sp, color = MutedGreen)
    }
}
