package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.MainNavigationTab
import com.example.ui.components.AmaCategoryChip
import com.example.ui.components.RolePermissionBanner
import com.example.ui.theme.Avocado
import com.example.ui.theme.CardCream
import com.example.ui.theme.Cream
import com.example.ui.theme.MutedGreen
import com.example.ui.theme.Success
import com.example.ui.theme.WarmBorder

@Composable
fun ProfileScreen(
    user: UserProfile,
    onSwitchRole: (UserRole) -> Unit,
    onOpen: (MainNavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag("profile_screen")
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineSmall,
            color = Avocado,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
        )

        // Identity card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardCream,
            border = BorderStroke(1.dp, WarmBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Avocado),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            color = Cream,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Avocado
                            )
                            if (user.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = Success,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(text = user.handle, fontSize = 13.sp, color = MutedGreen)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Stat("Reputation", "${user.reputationScore}")
                    Stat("Apps", "${user.appsPublishedCount}")
                    Stat("Bugs reported", "${user.bugsReportedCount}")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preview role (local only - there is no sign-in backend yet)
        Text(
            text = "Preview role",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Avocado
        )
        Text(
            text = "Switch roles to preview what developers, testers and admins can do. " +
                "This is a local preview; real sign-in isn't connected yet.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = MutedGreen,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UserRole.entries.forEach { role ->
                AmaCategoryChip(
                    label = role.title,
                    selected = role == user.role,
                    onClick = { onSwitchRole(role) },
                    modifier = Modifier.testTag("role_chip_${role.name.lowercase()}")
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        RolePermissionBanner(user = user)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tools",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Avocado
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardCream,
            border = BorderStroke(1.dp, WarmBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ToolRow(
                    icon = Icons.Default.Dashboard,
                    title = "Developer Dashboard",
                    subtitle = "Builds, rollouts and tester feedback",
                    tag = "tool_dashboard",
                    onClick = { onOpen(MainNavigationTab.DASHBOARD) }
                )
                HorizontalDivider(color = WarmBorder)
                ToolRow(
                    icon = Icons.Default.Forum,
                    title = "Community",
                    subtitle = "Discussions with developers and testers",
                    tag = "tool_community",
                    onClick = { onOpen(MainNavigationTab.COMMUNITY) }
                )
                HorizontalDivider(color = WarmBorder)
                ToolRow(
                    icon = Icons.Default.Security,
                    title = "IP Vault",
                    subtitle = "Timestamp and verify your app's origin",
                    tag = "tool_ip_vault",
                    onClick = { onOpen(MainNavigationTab.IP_VAULT) }
                )
                HorizontalDivider(color = WarmBorder)
                ToolRow(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Admin Console",
                    subtitle = "App approvals and moderation",
                    tag = "tool_admin",
                    onClick = { onOpen(MainNavigationTab.ADMIN) }
                )
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Avocado)
        Text(text = label, fontSize = 11.sp, color = MutedGreen)
    }
}

@Composable
private fun ToolRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Avocado)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Avocado)
            Text(text = subtitle, fontSize = 12.sp, color = MutedGreen)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MutedGreen)
    }
}
