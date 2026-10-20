package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppListing
import com.example.model.FeedbackReport
import com.example.ui.components.AmaEmptyState
import com.example.ui.components.AmaPrimaryButton
import com.example.ui.components.AmaRatingStars
import com.example.ui.components.AmaStarSelector
import com.example.ui.components.RatingSummary
import com.example.ui.components.timeAgo
import com.example.ui.theme.Avocado
import com.example.ui.theme.CardCream
import com.example.ui.theme.Danger
import com.example.ui.theme.Gold
import com.example.ui.theme.MutedGreen
import com.example.ui.theme.PaleSage
import com.example.ui.theme.WarmBorder

@Composable
fun ReviewsScreen(
    app: AppListing,
    reviews: List<FeedbackReport>,
    summary: RatingSummary?,
    onBack: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sorted = reviews.sortedByDescending { it.timestamp }

    LazyColumn(
        modifier = modifier.testTag("reviews_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Avocado)
                }
                Column {
                    Text(
                        text = "Reviews",
                        style = MaterialTheme.typography.titleLarge,
                        color = Avocado
                    )
                    Text(text = app.title, fontSize = 12.sp, color = MutedGreen, maxLines = 1)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            RatingSummaryCard(summary)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            // key(app.id) resets the form when a different app's reviews are opened
            key(app.id) { WriteReviewCard(onSubmit = onSubmit) }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Recent Reviews",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = Avocado
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (sorted.isEmpty()) {
            item {
                AmaEmptyState(
                    icon = Icons.Default.RateReview,
                    title = "No reviews yet",
                    message = "Be the first to share your experience with ${app.title}."
                )
            }
        } else {
            items(sorted, key = { it.id }) { report ->
                AmaReviewRow(report)
                HorizontalDivider(color = WarmBorder)
            }
        }
    }
}

@Composable
private fun RatingSummaryCard(summary: RatingSummary?) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardCream,
        border = BorderStroke(1.dp, WarmBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (summary == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No ratings yet", fontWeight = FontWeight.SemiBold, color = Avocado)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "The rating summary appears once the first review is submitted.",
                    fontSize = 12.sp,
                    color = MutedGreen
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1f".format(summary.average),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Avocado
                    )
                    AmaRatingStars(rating = summary.average, size = 16.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (summary.count == 1) "(1 review)" else "(${summary.count} reviews)",
                        fontSize = 11.sp,
                        color = MutedGreen
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (stars in 5 downTo 1) {
                        val n = summary.distribution[stars - 1]
                        val fraction = if (summary.count == 0) 0f else n.toFloat() / summary.count
                        DistributionRow(stars = stars, fraction = fraction)
                    }
                }
            }
        }
    }
}

@Composable
private fun DistributionRow(stars: Int, fraction: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$stars", fontSize = 11.sp, color = MutedGreen, modifier = Modifier.width(10.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape)
                .background(PaleSage)
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Avocado)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${(fraction * 100).toInt()}%",
            fontSize = 11.sp,
            color = MutedGreen,
            modifier = Modifier.width(32.dp)
        )
    }
}

@Composable
private fun WriteReviewCard(onSubmit: (Int, String) -> Unit) {
    var rating by rememberSaveable { mutableIntStateOf(0) }
    var text by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val focusManager: FocusManager = LocalFocusManager.current

    Column {
        Text(
            text = "Write a Review",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            color = Avocado
        )
        Spacer(modifier = Modifier.height(8.dp))
        AmaStarSelector(
            rating = rating,
            onRatingChange = {
                rating = it
                error = null
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                error = null
            },
            placeholder = { Text("Share your experience...", fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("review_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardCream,
                unfocusedContainerColor = CardCream,
                focusedBorderColor = Avocado,
                unfocusedBorderColor = WarmBorder,
                focusedTextColor = Avocado,
                unfocusedTextColor = Avocado,
                cursorColor = Avocado
            )
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = error.orEmpty(), color = Danger, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        AmaPrimaryButton(
            label = "Submit",
            modifier = Modifier.testTag("btn_submit_review"),
            onClick = {
                val trimmed = text.trim()
                when {
                    rating == 0 -> error = "Please choose a star rating."
                    trimmed.length < 10 -> error = "Tell us a little more (at least 10 characters)."
                    else -> {
                        focusManager.clearFocus()
                        onSubmit(rating, trimmed)
                        rating = 0
                        text = ""
                        error = null
                    }
                }
            }
        )
    }
}

/** One row of the review list; also reused on the app details page. */
@Composable
fun AmaReviewRow(report: FeedbackReport, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PaleSage),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = report.testerName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = Avocado
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.testerName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Avocado,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AmaRatingStars(rating = report.rating.toDouble(), size = 12.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "%.1f".format(report.rating.toDouble()),
                        fontSize = 11.sp,
                        color = Gold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(text = timeAgo(report.timestamp), fontSize = 11.sp, color = MutedGreen)
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (report.title.isNotBlank()) {
            Text(
                text = report.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Avocado
            )
        }
        if (report.description.isNotBlank()) {
            Text(
                text = report.description,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MutedGreen
            )
        }
        val reply = report.developerResponse
        if (!reply.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = PaleSage) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Developer response",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Avocado
                    )
                    Text(text = reply, fontSize = 12.sp, color = MutedGreen)
                }
            }
        }
    }
}
