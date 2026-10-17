package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AppDetailDialog(
    app: AppListing,
    blockchainProof: BlockchainTimestampProof?,
    onDismiss: () -> Unit,
    onDownloadClick: (AppBuild, Boolean) -> Unit,
    onFeedbackClick: () -> Unit,
    onViewProofClick: (BlockchainTimestampProof) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${app.packageName} • by ${app.developerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Badges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlatformBadge(platform = app.platform)
                    StatusBadge(status = app.status)
                }

                // Description
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Blockchain Proof Callout
                if (blockchainProof != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmaSage,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldTertiary)
                                Column {
                                    Text(
                                        text = "IP TIMESTAMP VERIFIED",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                        color = EmeraldTertiary
                                    )
                                    Text(
                                        text = "Block #${blockchainProof.blockHeight}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                        color = CyanPrimaryLight
                                    )
                                }
                            }

                            FilledTonalButton(
                                onClick = { onViewProofClick(blockchainProof) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("View Certificate", fontSize = 11.sp)
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Build Version History & Downloads
                Text(
                    text = "Builds & Download Tracks",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                app.builds.forEach { build ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "v${build.versionName} (#${build.buildNumber})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${build.binarySizeMb} MB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = build.releaseNotes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onDownloadClick(build, true) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = AmaCream),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download APK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onDownloadClick(build, false) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Play / TestFlight", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Submit Feedback CTA
                Button(
                    onClick = onFeedbackClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VioletSecondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit Tester Feedback & Bug Report")
                }
            }
        }
    }
}

@Composable
fun UploadAppDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, packageName: String, pitch: String, desc: String, category: String, platform: AppPlatform, version: String, sizeMb: Double, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("com.mydev.") }
    var pitch by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Productivity") }
    var platform by remember { mutableStateOf(AppPlatform.ANDROID_APK) }
    var versionName by remember { mutableStateOf("1.0.0") }
    var binarySize by remember { mutableStateOf("32.5") }
    var releaseNotes by remember { mutableStateOf("Initial beta release for AmaApp community.") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Post New App to AmaApp",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Includes automatic Blockchain IP Timestamping to safeguard copyright origin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanPrimary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("App Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_new_app_title")
                )

                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name / Bundle ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_new_app_package")
                )

                OutlinedTextField(
                    value = pitch,
                    onValueChange = { pitch = it },
                    label = { Text("One-line Elevator Pitch") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Overview & What to Test") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                // Category & Platform Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = versionName,
                        onValueChange = { versionName = it },
                        label = { Text("Version") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = binarySize,
                    onValueChange = { binarySize = it },
                    label = { Text("APK / IPA Size (MB)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = releaseNotes,
                    onValueChange = { releaseNotes = it },
                    label = { Text("Release Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && packageName.isNotBlank()) {
                                onSubmit(
                                    title,
                                    packageName,
                                    pitch,
                                    description,
                                    category,
                                    platform,
                                    versionName,
                                    binarySize.toDoubleOrNull() ?: 25.0,
                                    releaseNotes
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = AmaCream),
                        modifier = Modifier.testTag("btn_submit_post_app")
                    ) {
                        Text("Post & Timestamp IP", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun UploadBuildDialog(
    app: AppListing,
    onDismiss: () -> Unit,
    onSubmit: (versionName: String, buildNumber: Int, platform: AppPlatform, sizeMb: Double, notes: String, rollout: Int) -> Unit
) {
    var versionName by remember { mutableStateOf("2.4.1") }
    var buildNumber by remember { mutableStateOf("241") }
    var binarySize by remember { mutableStateOf("48.9") }
    var releaseNotes by remember { mutableStateOf("Bug fixes and performance improvements reported by testers.") }
    var rolloutSlider by remember { mutableStateOf(100f) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Upload Build for ${app.title}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = versionName,
                        onValueChange = { versionName = it },
                        label = { Text("Version (e.g. 2.4.1)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = buildNumber,
                        onValueChange = { buildNumber = it },
                        label = { Text("Build #") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = binarySize,
                    onValueChange = { binarySize = it },
                    label = { Text("Size in MB") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = releaseNotes,
                    onValueChange = { releaseNotes = it },
                    label = { Text("What's New in This Build") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Text(
                    text = "Staged Rollout: ${rolloutSlider.toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Slider(
                    value = rolloutSlider,
                    onValueChange = { rolloutSlider = it },
                    valueRange = 10f..100f,
                    steps = 8
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSubmit(
                                versionName,
                                buildNumber.toIntOrNull() ?: 1,
                                app.platform,
                                binarySize.toDoubleOrNull() ?: 30.0,
                                releaseNotes,
                                rolloutSlider.toInt()
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = AmaCream)
                    ) {
                        Text("Deploy Build", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackDialog(
    app: AppListing,
    onDismiss: () -> Unit,
    onSubmit: (type: FeedbackType, severity: FeedbackSeverity, rating: Int, title: String, desc: String, device: String, os: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(FeedbackType.BUG_REPORT) }
    var selectedSeverity by remember { mutableStateOf(FeedbackSeverity.MAJOR_ISSUE) }
    var rating by remember { mutableStateOf(5) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deviceModel by remember { mutableStateOf("Google Pixel 8 Pro") }
    var osVersion by remember { mutableStateOf("Android 15 (AP2A.240905.003)") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Direct Tester Feedback",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Sending directly to ${app.developerName} for ${app.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanPrimary
                )

                // Feedback Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FeedbackType.values().forEach { t ->
                        FilterChip(
                            selected = selectedType == t,
                            onClick = { selectedType = t },
                            label = { Text(t.label, fontSize = 11.sp) }
                        )
                    }
                }

                // Rating Stars
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Rating:", style = MaterialTheme.typography.bodyMedium)
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = BlockchainGold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Summary / Suggestion") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_feedback_title")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Reproduction Steps / Details") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                // Device telemetry
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = deviceModel,
                        onValueChange = { deviceModel = it },
                        label = { Text("Device Model") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = osVersion,
                        onValueChange = { osVersion = it },
                        label = { Text("OS Version") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSubmit(selectedType, selectedSeverity, rating, title, description, deviceModel, osVersion)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = AmaCream),
                        modifier = Modifier.testTag("btn_submit_feedback")
                    ) {
                        Text("Send Feedback", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun NewThreadDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, content: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("#general-testing") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Start Community Discussion",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("#general-testing", "#bug-reports", "#feature-requests").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Topic Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_forum_title")
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Discussion Content") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSubmit(title, content, category)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = AmaCream)
                    ) {
                        Text("Post Topic", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterIpDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, bundleId: String, codeSnippet: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var bundleId by remember { mutableStateOf("com.proprietary.") }
    var codeSnippet by remember { mutableStateOf("package com.proprietary\n\nclass CoreEngine {\n  // Proprietary patented algorithm\n}") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Register Software IP On-Chain",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Calculates an immutable SHA-256 fingerprint & anchors it to the blockchain ledger to prove first-origin ownership.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanPrimary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("App / Software Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_register_ip_title")
                )

                OutlinedTextField(
                    value = bundleId,
                    onValueChange = { bundleId = it },
                    label = { Text("Bundle ID / Package") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = codeSnippet,
                    onValueChange = { codeSnippet = it },
                    label = { Text("Raw Source Code or APK Hash Input") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSubmit(title, bundleId, codeSnippet)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = AmaCream),
                        modifier = Modifier.testTag("btn_confirm_register_ip")
                    ) {
                        Text("Calculate & Anchor", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BlockchainCertificateDialog(
    proof: BlockchainTimestampProof,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BlockchainCertificateCard(proof = proof)

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = AmaCream)
            ) {
                Text("Close Certificate", fontWeight = FontWeight.Bold)
            }
        }
    }
}
