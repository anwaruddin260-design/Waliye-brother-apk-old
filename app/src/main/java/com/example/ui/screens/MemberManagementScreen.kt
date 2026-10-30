package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Member
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold700
import com.example.ui.theme.JamaGreen
import com.example.ui.theme.JamaGreenLight
import com.example.ui.theme.KharchRed
import com.example.ui.theme.KharchRedLight
import com.example.ui.theme.Slate500
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MemberDuesInfo
import com.example.ui.viewmodel.WelfareViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MemberManagementScreen(
    viewModel: WelfareViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    val context = LocalContext.current
    val membersWithDues by viewModel.membersWithDues.collectAsStateWithLifecycle()
    val role by viewModel.userRole.collectAsStateWithLifecycle()
    val searchQuery by viewModel.memberSearchQuery.collectAsStateWithLifecycle()
    val activeFilter by viewModel.memberFilter.collectAsStateWithLifecycle()

    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    // Filter members based on search and selected chip
    val filteredMembers = membersWithDues.filter { duesInfo ->
        val member = duesInfo.member
        val matchesSearch = member.fullName.contains(searchQuery, ignoreCase = true) ||
                member.phone.contains(searchQuery, ignoreCase = true) ||
                member.memberCode.contains(searchQuery, ignoreCase = true) ||
                member.role.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (activeFilter) {
            "ACTIVE" -> member.isActive
            "PENDING_DUES" -> duesInfo.hasPendingDues
            "OFFICIALS" -> member.role.contains("President", ignoreCase = true) ||
                    member.role.contains("Treasurer", ignoreCase = true) ||
                    member.role.contains("Secretary", ignoreCase = true)
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Member Management",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "اراکین سوسائٹی • Directory & Dues Profile",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }

                    if (role.isAdmin) {
                        Button(
                            onClick = {
                                viewModel.editingMember.value = null
                                viewModel.showAddEditMemberDialog.value = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_member_top_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Member", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.memberSearchQuery.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("member_search_input"),
                    placeholder = { Text("Search by name, phone, WB-001...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Emerald800)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.memberSearchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Emerald800,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = activeFilter == "ALL",
                            onClick = { viewModel.memberFilter.value = "ALL" },
                            label = { Text("All (${membersWithDues.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald800,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeFilter == "ACTIVE",
                            onClick = { viewModel.memberFilter.value = "ACTIVE" },
                            label = { Text("Active (${membersWithDues.count { it.member.isActive }})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald800,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeFilter == "PENDING_DUES",
                            onClick = { viewModel.memberFilter.value = "PENDING_DUES" },
                            label = { Text("Pending Dues (${membersWithDues.count { it.hasPendingDues }})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KharchRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeFilter == "OFFICIALS",
                            onClick = { viewModel.memberFilter.value = "OFFICIALS" },
                            label = { Text("Officials") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Gold700,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Member Cards List
            if (filteredMembers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = Slate500,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No members match the current filter",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Try adjusting your search query or reset filter.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500
                            )
                        }
                    }
                }
            } else {
                items(filteredMembers, key = { it.member.id }) { duesInfo ->
                    MemberListItemCard(
                        duesInfo = duesInfo,
                        isAdmin = role.isAdmin,
                        onMemberClick = { viewModel.selectMember(duesInfo.member.id) },
                        onCollectChandaClick = {
                            viewModel.preselectedMemberForChanda.value = duesInfo.member
                            viewModel.editingChanda.value = null
                            viewModel.showAddEditChandaDialog.value = true
                        },
                        onCallClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${duesInfo.member.phone}")
                            }
                            context.startActivity(intent)
                        },
                        onWhatsAppClick = {
                            openWhatsApp(context, duesInfo.member.phone)
                        }
                    )
                }
            }
        }

        // Floating Action Button for Admin
        if (role.isAdmin) {
            FloatingActionButton(
                onClick = {
                    viewModel.editingMember.value = null
                    viewModel.showAddEditMemberDialog.value = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 20.dp)
                    .testTag("add_member_fab"),
                containerColor = Emerald800,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Member")
            }
        }
    }
}

@Composable
fun MemberListItemCard(
    duesInfo: MemberDuesInfo,
    isAdmin: Boolean,
    onMemberClick: () -> Unit,
    onCollectChandaClick: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    val member = duesInfo.member
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    // Initial letters for avatar
    val initials = member.fullName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifBlank { "M" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("member_card_${member.id}")
            .clickable { onMemberClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Member Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            when {
                                member.role.contains("President") -> Gold500
                                member.role.contains("Treasurer") -> Gold700
                                else -> Emerald800
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Code
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Member Code badge
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = member.memberCode,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald800
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Role badge
                        Surface(
                            color = if (member.role == "Member") Emerald800.copy(alpha = 0.1f) else Gold100,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = member.role,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (member.role == "Member") Emerald800 else Gold700
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Profile",
                    tint = Slate500,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dues & Saman Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Monthly Chanda rate & Dues status
                Column {
                    Text(
                        text = "Monthly Chanda: Rs. ${currencyFormat.format(member.monthlyChandaAmount)}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    if (duesInfo.hasPendingDues) {
                        Surface(
                            color = KharchRedLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = KharchRed,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Pending Dues: Rs. ${currencyFormat.format(duesInfo.pendingAmount)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = KharchRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = JamaGreenLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = JamaGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Chanda Paid Up to Date",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = JamaGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                // Quick Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (member.phone.isNotBlank()) {
                        IconButton(
                            onClick = onCallClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call ${member.fullName}",
                                tint = Emerald800,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onWhatsAppClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "WhatsApp message",
                                tint = JamaGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isAdmin) {
                        IconButton(
                            onClick = onCollectChandaClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Collect Chanda",
                                tint = Gold700,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun openWhatsApp(context: Context, phone: String) {
    val cleanPhone = phone.replace(Regex("[^0-9]"), "")
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val fallback = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        context.startActivity(fallback)
    }
}
