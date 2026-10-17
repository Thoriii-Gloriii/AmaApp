package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppListing
import com.example.model.AppPlatform
import com.example.model.AppStatus
import com.example.model.FeedbackReport
import com.example.ui.theme.Avocado
import com.example.ui.theme.CardCream
import com.example.ui.theme.Cream
import com.example.ui.theme.Gold
import com.example.ui.theme.MutedGreen
import com.example.ui.theme.Olive
import com.example.ui.theme.PaleSage
import com.example.ui.theme.WarmBorder

// Shared AmaApp design-system building blocks (see the UI guide, sections 2 and 3)

/** Width / height of drawable/ama_logo.webp (the full "AmaApp - App Store" lockup). */
const val AMA_LOGO_ASPECT = 1.0856f

/** Aggregated rating computed from real review records. Index 0 = one star. */
data class RatingSummary(
    val average: Double,
    val count: Int,
    val distribution: List<Int>
)

/** Builds per-app rating summaries from feedback records that carry a 1-5 rating. */
fun buildRatingSummaries(reports: List<FeedbackReport>): Map<String, RatingSummary> =
    reports
        .filter { it.rating in 1..5 }
        .groupBy { it.appId }
        .mapValues { (_, list) ->
            val dist = MutableList(5) { 0 }
            list.forEach { dist[it.rating - 1] = dist[it.rating - 1] + 1 }
            RatingSummary(
                average = list.sumOf { it.rating }.toDouble() / list.size,
                count = list.size,
                distribution = dist
            )
        }

/** Applies search text, category and platform filters used by Home and Explore. */
fun filterApps(
    apps: List<AppListing>,
    query: String,
    category: String,
    platform: AppPlatform?
): List<AppListing> = apps.filter { app ->
    val q = query.trim()
    val matchesQuery = q.isEmpty() ||
        app.title.contains(q, ignoreCase = true) ||
        app.packageName.contains(q, ignoreCase = true) ||
        app.shortPitch.contains(q, ignoreCase = true) ||
        app.developerName.contains(q, ignoreCase = true)
    val matchesCategory = category == "All" || app.category.equals(category, ignoreCase = true)
    val matchesPlatform = platform == null || app.platform == platform || app.platform == AppPlatform.DUAL
    matchesQuery && matchesCategory && matchesPlatform
}

fun categoryIcon(category: String): ImageVector = when (category) {
    "Media & Video" -> Icons.Default.Movie
    "Security & Tools" -> Icons.Default.Shield
    "Productivity" -> Icons.Default.TaskAlt
    "Games" -> Icons.Default.SportsEsports
    "FinTech" -> Icons.Default.AccountBalance
    "AI Tools" -> Icons.Default.AutoAwesome
    "Development" -> Icons.Default.Code
    else -> Icons.Default.Apps
}

fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "%.1fM".format(n / 1_000_000.0)
    n >= 1_000 -> "%.1fk".format(n / 1_000.0)
    else -> n.toString()
}

fun timeAgo(timestamp: Long, now: Long = System.currentTimeMillis()): String {
    val minutes = ((now - timestamp) / 60_000L).coerceAtLeast(0)
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        minutes < 60 * 24 -> "${minutes / 60} hr ago"
        minutes < 60 * 24 * 7 -> "${minutes / (60 * 24)} days ago"
        else -> "${minutes / (60 * 24 * 7)} wk ago"
    }
}

// --- Brand ---

@Composable
fun AmaLogo(modifier: Modifier = Modifier, height: Dp = 48.dp) {
    Image(
        painter = painterResource(id = R.drawable.ama_logo),
        contentDescription = "AmaApp App Store",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .height(height)
            .aspectRatio(AMA_LOGO_ASPECT)
    )
}

/** Decorative organic curves for the welcome screen. Drawn behind content, never tappable. */
@Composable
fun AmaWaveBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val topBack = Path().apply {
            moveTo(w * 0.50f, 0f)
            cubicTo(w * 0.70f, h * 0.015f, w * 0.88f, h * 0.05f, w, h * 0.14f)
            lineTo(w, 0f)
            close()
        }
        drawPath(topBack, color = Olive.copy(alpha = 0.55f))

        val topFront = Path().apply {
            moveTo(w * 0.68f, 0f)
            cubicTo(w * 0.82f, h * 0.012f, w * 0.94f, h * 0.035f, w, h * 0.075f)
            lineTo(w, 0f)
            close()
        }
        drawPath(topFront, color = Avocado.copy(alpha = 0.92f))

        val bottomBack = Path().apply {
            moveTo(0f, h * 0.90f)
            cubicTo(w * 0.25f, h * 0.84f, w * 0.55f, h * 0.97f, w, h * 0.90f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(bottomBack, color = PaleSage)

        val bottomMid = Path().apply {
            moveTo(0f, h * 0.95f)
            cubicTo(w * 0.30f, h * 0.90f, w * 0.60f, h * 1.00f, w, h * 0.94f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(bottomMid, color = Olive.copy(alpha = 0.6f))

        val bottomFront = Path().apply {
            moveTo(0f, h * 0.985f)
            cubicTo(w * 0.35f, h * 0.95f, w * 0.7f, h * 1.01f, w, h * 0.975f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(bottomFront, color = Avocado)
    }
}

// --- Buttons & chips ---

@Composable
fun AmaPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Avocado,
            contentColor = Cream,
            disabledContainerColor = WarmBorder,
            disabledContentColor = MutedGreen
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun AmaCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) Avocado else PaleSage,
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) Cream else Avocado,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
        )
    }
}

@Composable
fun AmaSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            color = Avocado
        )
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MutedGreen,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

// --- Ratings ---

@Composable
fun AmaRatingStars(rating: Double, modifier: Modifier = Modifier, size: Dp = 14.dp) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (rating >= index + 0.5) Gold else WarmBorder,
                modifier = Modifier.size(size)
            )
        }
    }
}

/** Interactive, accessible 1-5 star selector (each star is a 44dp touch target). */
@Composable
fun AmaStarSelector(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (value in 1..5) {
            IconButton(
                onClick = { onRatingChange(value) },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = if (value <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = if (value == 1) "Rate 1 star" else "Rate $value stars",
                    tint = if (value <= rating) Gold else MutedGreen,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

// --- App tiles ---

@Composable
fun AmaAppIcon(category: String, modifier: Modifier = Modifier, size: Dp = 44.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.27f))
            .background(PaleSage),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = categoryIcon(category),
            contentDescription = null,
            tint = Avocado,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/** Two-column grid card used on Home (icon, name, category, rating, download). */
@Composable
fun AmaGridCard(
    app: AppListing,
    summary: RatingSummary?,
    downloaded: Boolean,
    onClick: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = CardCream,
        border = BorderStroke(1.dp, WarmBorder),
        modifier = modifier.testTag("app_card_${app.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AmaAppIcon(category = app.category, size = 44.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = app.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Avocado
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = app.category,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                color = MutedGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (summary != null) {
                        "%.1f (%s)".format(summary.average, formatCount(summary.count))
                    } else {
                        "New"
                    },
                    fontSize = 12.sp,
                    color = MutedGreen,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_card_download_${app.id}")
                ) {
                    Icon(
                        imageVector = if (downloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                        contentDescription = if (downloaded) "Downloaded" else "Download ${app.title}",
                        tint = Avocado,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// --- Banner ---

/** "Build. Share. Grow." promotional banner - prominent but shorter than the content area. */
@Composable
fun AmaBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Avocado,
        modifier = modifier.fillMaxWidth()
    ) {
        Box {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Olive.copy(alpha = 0.35f),
                    radius = size.height * 0.85f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.92f, size.height * 0.35f)
                )
                drawCircle(
                    color = Olive.copy(alpha = 0.25f),
                    radius = size.height * 0.5f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.70f, size.height * 1.05f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Build\nShare\nGrow",
                        color = Cream,
                        fontSize = 23.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "The home for indie apps and creative developers.",
                        color = Cream.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .rotate(10f)
                            .size(76.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Olive),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "</>",
                            color = Cream,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Cream)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Explore apps",
                            tint = Avocado,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- Empty state ---

@Composable
fun AmaEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(PaleSage),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Avocado, modifier = Modifier.size(30.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Avocado,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedGreen,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(20.dp))
            AmaPrimaryButton(
                label = actionLabel,
                onClick = onAction,
                modifier = Modifier.width(220.dp)
            )
        }
    }
}

// --- Bottom navigation ---

enum class AmaBottomTab(
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    EXPLORE("Explore", Icons.Filled.Explore, Icons.Outlined.Explore, "nav_explore"),
    LIBRARY("Library", Icons.Filled.Bookmark, Icons.Outlined.Bookmark, "nav_library"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

@Composable
fun AmaBottomBar(
    selected: AmaBottomTab,
    onSelect: (AmaBottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = WarmBorder)
        NavigationBar(
            containerColor = CardCream,
            tonalElevation = 0.dp
        ) {
            AmaBottomTab.entries.forEach { tab ->
                val isSelected = tab == selected
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelect(tab) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                            contentDescription = tab.label
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Avocado,
                        selectedTextColor = Avocado,
                        indicatorColor = PaleSage,
                        unselectedIconColor = MutedGreen,
                        unselectedTextColor = MutedGreen
                    ),
                    modifier = Modifier.testTag(tab.testTag)
                )
            }
        }
    }
}

/** Small helper so detail rows can show a platform label consistently. */
fun AppPlatform.shortLabel(): String = when (this) {
    AppPlatform.ANDROID_APK -> "Android"
    AppPlatform.IOS_IPA -> "iOS"
    AppPlatform.DUAL -> "Android & iOS"
}

fun AppStatus.isPubliclyDownloadable(): Boolean =
    this != AppStatus.FLAGGED && this != AppStatus.UNDER_REVIEW
