package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppBuild
import com.example.model.AppListing
import com.example.model.AppPlatform
import com.example.ui.components.AmaBanner
import com.example.ui.components.AmaCategoryChip
import com.example.ui.components.AmaEmptyState
import com.example.ui.components.AmaGridCard
import com.example.ui.components.AmaLogo
import com.example.ui.components.AmaSectionHeader
import com.example.ui.components.RatingSummary
import com.example.ui.components.filterApps
import com.example.ui.theme.Avocado
import com.example.ui.theme.CardCream
import com.example.ui.theme.MutedGreen
import com.example.ui.theme.PaleSage
import com.example.ui.theme.WarmBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    apps: List<AppListing>,
    ratingSummaries: Map<String, RatingSummary>,
    downloadedAppIds: Set<String>,
    searchQuery: String,
    selectedCategory: String,
    selectedPlatform: AppPlatform?,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPlatformChange: (AppPlatform?) -> Unit,
    onClearFilters: () -> Unit,
    onAppClick: (AppListing) -> Unit,
    onDownload: (AppListing, AppBuild) -> Unit,
    onSeeAll: () -> Unit,
    onNotifications: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }

    val visibleApps = apps.filter { it.isApprovedByAdmin }
    val categories = listOf("All") + visibleApps.map { it.category }.distinct()
    val filtered = filterApps(visibleApps, searchQuery, selectedCategory, selectedPlatform)
    val filtersActive = searchQuery.isNotBlank() || selectedCategory != "All" || selectedPlatform != null

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.testTag("home_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Compact header: logo + notifications + profile
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AmaLogo(height = 52.dp)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onNotifications) {
                    Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications", tint = Avocado)
                }
                IconButton(onClick = onProfile, modifier = Modifier.testTag("btn_home_profile")) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Avocado)
                }
            }
        }

        // Search + filter
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_apps_input"),
                    placeholder = { Text("Search apps, developers...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardCream,
                        unfocusedContainerColor = CardCream,
                        focusedBorderColor = Avocado,
                        unfocusedBorderColor = WarmBorder,
                        focusedLeadingIconColor = Avocado,
                        unfocusedLeadingIconColor = MutedGreen,
                        focusedTrailingIconColor = Avocado,
                        unfocusedTrailingIconColor = MutedGreen,
                        focusedTextColor = Avocado,
                        unfocusedTextColor = Avocado,
                        cursorColor = Avocado
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    onClick = { showFilters = true },
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedPlatform != null) Avocado else CardCream,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("btn_open_filters")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Filters",
                            tint = if (selectedPlatform != null) PaleSage else Avocado
                        )
                    }
                }
            }
        }

        // Promo banner
        item(span = { GridItemSpan(maxLineSpan) }) {
            AmaBanner(onClick = onSeeAll)
        }

        // Category chips (horizontal scroll)
        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(categories) { cat ->
                    AmaCategoryChip(
                        label = cat,
                        selected = cat == selectedCategory,
                        onClick = { onCategoryChange(cat) },
                        modifier = Modifier.testTag("filter_cat_$cat")
                    )
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            AmaSectionHeader(
                title = "Featured Apps",
                actionLabel = "See All",
                onAction = onSeeAll
            )
        }

        if (filtered.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AmaEmptyState(
                    icon = Icons.Default.SearchOff,
                    title = if (filtersActive) "No apps match your search" else "No apps yet",
                    message = if (filtersActive) {
                        "Try a different search or clear your filters."
                    } else {
                        "Apps published by developers will appear here."
                    },
                    actionLabel = if (filtersActive) "Clear filters" else null,
                    onAction = if (filtersActive) onClearFilters else null
                )
            }
        } else {
            items(filtered, key = { it.id }) { app ->
                val build = app.builds.firstOrNull { it.isLatest } ?: app.builds.firstOrNull()
                AmaGridCard(
                    app = app,
                    summary = ratingSummaries[app.id],
                    downloaded = app.id in downloadedAppIds,
                    onClick = { onAppClick(app) },
                    // With no build to fetch, the download control opens details for more info.
                    onDownload = { if (build != null) onDownload(app, build) else onAppClick(app) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            containerColor = CardCream
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    color = Avocado
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Platform",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MutedGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AmaCategoryChip("All", selectedPlatform == null, { onPlatformChange(null) })
                    AmaCategoryChip(
                        "Android",
                        selectedPlatform == AppPlatform.ANDROID_APK,
                        { onPlatformChange(AppPlatform.ANDROID_APK) }
                    )
                    AmaCategoryChip(
                        "iOS",
                        selectedPlatform == AppPlatform.IOS_IPA,
                        { onPlatformChange(AppPlatform.IOS_IPA) }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        onClearFilters()
                        showFilters = false
                    }) { Text("Clear all", color = MutedGreen) }
                    TextButton(onClick = { showFilters = false }) {
                        Text("Done", color = Avocado, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
