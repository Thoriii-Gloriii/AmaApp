package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppListing
import com.example.ui.components.AmaAppIcon
import com.example.ui.components.AmaEmptyState
import com.example.ui.theme.Avocado
import com.example.ui.theme.Cream
import com.example.ui.theme.MutedGreen
import com.example.ui.theme.PaleSage
import com.example.ui.theme.WarmBorder

enum class LibraryTab(val label: String) {
    INSTALLED("Installed"),
    SAVED("Saved"),
    DOWNLOADS("Downloads")
}

/** True only if the package is genuinely installed on this device. */
fun isPackageInstalled(context: Context, packageName: String): Boolean = try {
    if (Build.VERSION.SDK_INT >= 33) {
        context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(packageName, 0)
    }
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}

/**
 * My Apps. Installed, Saved and Downloads are deliberately separate states:
 * Installed = verified on-device, Saved = bookmarked, Downloads = package fetched via AmaApp.
 */
@Composable
fun LibraryScreen(
    apps: List<AppListing>,
    savedAppIds: Set<String>,
    downloadedAppIds: Set<String>,
    onAppClick: (AppListing) -> Unit,
    onRemoveSaved: (AppListing) -> Unit,
    onDiscover: () -> Unit,
    onMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var tabName by rememberSaveable { mutableStateOf(LibraryTab.INSTALLED.name) }
    val tab = LibraryTab.valueOf(tabName)

    val installed = remember(apps) { apps.filter { isPackageInstalled(context, it.packageName) } }
    val saved = apps.filter { it.id in savedAppIds }
    val downloads = apps.filter { it.id in downloadedAppIds }
    val shown = when (tab) {
        LibraryTab.INSTALLED -> installed
        LibraryTab.SAVED -> saved
        LibraryTab.DOWNLOADS -> downloads
    }

    Column(modifier = modifier.testTag("library_screen")) {
        Text(
            text = "My Apps",
            style = MaterialTheme.typography.headlineSmall,
            color = Avocado,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)
        )

        // Segmented tabs
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(CircleShape)
                .background(PaleSage)
                .padding(4.dp)
        ) {
            LibraryTab.entries.forEach { t ->
                val selected = t == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (selected) Avocado else Color.Transparent)
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { tabName = t.name }
                        )
                        .testTag("library_tab_${t.name.lowercase()}")
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = t.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) Cream else Avocado
                    )
                }
            }
        }

        if (shown.isEmpty()) {
            val (icon, title, message) = when (tab) {
                LibraryTab.INSTALLED -> Triple(
                    Icons.Default.Inbox,
                    "No installed apps yet",
                    "Apps from AmaApp that are installed on this device will show up here."
                )
                LibraryTab.SAVED -> Triple(
                    Icons.Default.BookmarkBorder,
                    "No saved apps yet",
                    "Tap the bookmark on any app to keep it here for later."
                )
                LibraryTab.DOWNLOADS -> Triple(
                    Icons.Default.CloudDownload,
                    "No downloads yet",
                    "Apps you download from AmaApp will be listed here."
                )
            }
            AmaEmptyState(
                icon = icon,
                title = title,
                message = message,
                actionLabel = "Discover apps",
                onAction = onDiscover
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(shown, key = { it.id }) { app ->
                    LibraryRow(
                        app = app,
                        tab = tab,
                        onOpenDetails = { onAppClick(app) },
                        onOpenApp = {
                            val launch = context.packageManager.getLaunchIntentForPackage(app.packageName)
                            if (launch != null) {
                                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(launch)
                            } else {
                                onMessage("${app.title} can't be opened from here.")
                            }
                        },
                        onRemoveSaved = { onRemoveSaved(app) }
                    )
                    HorizontalDivider(color = WarmBorder)
                }
            }
        }
    }
}

@Composable
private fun LibraryRow(
    app: AppListing,
    tab: LibraryTab,
    onOpenDetails: () -> Unit,
    onOpenApp: () -> Unit,
    onRemoveSaved: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val build = app.builds.firstOrNull { it.isLatest } ?: app.builds.firstOrNull()
    val status = when (tab) {
        LibraryTab.INSTALLED -> build?.let { "Latest build v${it.versionName}" } ?: "Installed"
        LibraryTab.SAVED -> "Saved for later"
        LibraryTab.DOWNLOADS -> build?.let { "Downloaded v${it.versionName}" } ?: "Downloaded"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetails)
            .padding(vertical = 12.dp)
            .testTag("library_row_${app.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AmaAppIcon(category = app.category, size = 48.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Avocado,
                maxLines = 1
            )
            Text(text = app.category, fontSize = 12.sp, color = MutedGreen, maxLines = 1)
            Text(text = status, fontSize = 11.sp, color = MutedGreen, maxLines = 1)
        }
        Surface(
            onClick = if (tab == LibraryTab.INSTALLED) onOpenApp else onOpenDetails,
            shape = CircleShape,
            color = PaleSage
        ) {
            Text(
                text = if (tab == LibraryTab.INSTALLED) "Open" else "View",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Avocado,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
        }
        Box {
            IconButton(onClick = { menuOpen = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options for ${app.title}", tint = MutedGreen)
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("View details") },
                    onClick = { menuOpen = false; onOpenDetails() }
                )
                if (tab == LibraryTab.SAVED) {
                    DropdownMenuItem(
                        text = { Text("Remove from saved") },
                        onClick = { menuOpen = false; onRemoveSaved() }
                    )
                }
            }
        }
    }
}
