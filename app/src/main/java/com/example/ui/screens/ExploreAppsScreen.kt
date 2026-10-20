package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreAppsScreen(
    apps: List<AppListing>,
    currentUser: UserProfile,
    searchQuery: String,
    selectedCategory: String,
    selectedPlatform: AppPlatform?,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPlatformChange: (AppPlatform?) -> Unit,
    onAppDetailClick: (AppListing) -> Unit,
    onDownloadClick: (AppListing, AppBuild, Boolean) -> Unit,
    onFeedbackClick: (AppListing) -> Unit,
    onUploadAppClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All") + apps.map { it.category }.distinct()

    val filteredApps = apps.filter { app ->
        val matchesSearch = app.title.contains(searchQuery, ignoreCase = true) ||
                app.packageName.contains(searchQuery, ignoreCase = true) ||
                app.shortPitch.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || app.category.equals(selectedCategory, ignoreCase = true)
        val matchesPlatform = selectedPlatform == null || app.platform == selectedPlatform || app.platform == AppPlatform.DUAL
        matchesSearch && matchesCategory && matchesPlatform
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_apps_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Title
        item {
            Text(
                text = "Explore",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_apps_input"),
                placeholder = { Text("Search apps, bundle IDs, developers...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = CyanPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )
        }

        // Platform Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedPlatform == null,
                    onClick = { onPlatformChange(null) },
                    label = { Text("All OS") },
                    modifier = Modifier.testTag("filter_os_all")
                )
                FilterChip(
                    selected = selectedPlatform == AppPlatform.ANDROID_APK,
                    onClick = { onPlatformChange(AppPlatform.ANDROID_APK) },
                    label = { Text("Android APK") },
                    leadingIcon = {
                        Icon(Icons.Default.Android, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("filter_os_android")
                )
                FilterChip(
                    selected = selectedPlatform == AppPlatform.IOS_IPA,
                    onClick = { onPlatformChange(AppPlatform.IOS_IPA) },
                    label = { Text("iOS TestFlight") },
                    leadingIcon = {
                        Icon(Icons.Default.PhoneIphone, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("filter_os_ios")
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .testTag("filter_cat_$cat")
                            .clickable { onCategoryChange(cat) }
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) Cream else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Section Title & Upload CTA
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Beta Releases (${filteredApps.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (currentUser.role.canUploadApps) {
                    FilledTonalButton(
                        onClick = onUploadAppClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_post_app_top")
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Post App", fontSize = 12.sp)
                    }
                }
            }
        }

        // App Cards List
        if (filteredApps.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No apps found matching your query",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredApps, key = { it.id }) { app ->
                AppCardItem(
                    app = app,
                    currentUser = currentUser,
                    onAppDetailClick = { onAppDetailClick(app) },
                    onDownloadClick = { build, isDirectApk -> onDownloadClick(app, build, isDirectApk) },
                    onFeedbackClick = { onFeedbackClick(app) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AppCardItem(
    app: AppListing,
    currentUser: UserProfile,
    onAppDetailClick: () -> Unit,
    onDownloadClick: (AppBuild, Boolean) -> Unit,
    onFeedbackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latestBuild = app.builds.firstOrNull { it.isLatest } ?: app.builds.firstOrNull()

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_card_${app.id}")
            .clickable { onAppDetailClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Row: App Icon & Title & Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyanPrimary.copy(alpha = 0.8f), VioletSecondary.copy(alpha = 0.8f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (app.category) {
                            "Media & Video" -> Icons.Default.Movie
                            "Security & Tools" -> Icons.Default.Shield
                            "Productivity" -> Icons.Default.TaskAlt
                            else -> Icons.Default.Apps
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = app.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (app.blockchainProofId != null) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Blockchain Timestamp Verified",
                                tint = EmeraldTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "by ${app.developerName} • ${app.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = app.status)
            }

            // Short Description
            Text(
                text = app.shortPitch,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Platform Badge & Version
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PlatformBadge(platform = app.platform)

                if (latestBuild != null) {
                    Text(
                        text = "Build v${latestBuild.versionName} (${latestBuild.binarySizeMb} MB)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Telemetry Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricChip(
                    icon = Icons.Default.Star,
                    label = "Rating",
                    value = String.format("%.1f", app.rating),
                    color = BlockchainGold
                )
                MetricChip(
                    icon = Icons.Default.Download,
                    label = "Downloads",
                    value = "${app.totalDownloads}"
                )
                MetricChip(
                    icon = Icons.Default.Group,
                    label = "Testers",
                    value = "${app.activeTesters}"
                )
                MetricChip(
                    icon = Icons.Default.CheckCircle,
                    label = "Crash-Free",
                    value = "${app.crashFreeRate}%",
                    color = EmeraldTertiary
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Direct APK Download
                Button(
                    onClick = {
                        if (latestBuild != null) onDownloadClick(latestBuild, true)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_download_apk_${app.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Cream),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (app.platform == AppPlatform.IOS_IPA) "Get IPA" else "APK (Direct)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Play Store / TestFlight Beta link
                OutlinedButton(
                    onClick = {
                        if (latestBuild != null) onDownloadClick(latestBuild, false)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_store_test_${app.id}"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = if (app.platform == AppPlatform.IOS_IPA) Icons.Default.PhoneIphone else Icons.Default.Shop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (app.platform == AppPlatform.IOS_IPA) "TestFlight" else "Play Track",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Submit Feedback Button
                FilledTonalIconButton(
                    onClick = onFeedbackClick,
                    modifier = Modifier.testTag("btn_feedback_${app.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = "Submit Feedback", tint = CyanPrimary)
                }
            }
        }
    }
}
