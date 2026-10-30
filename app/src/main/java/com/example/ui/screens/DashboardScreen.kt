package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChandaRecord
import com.example.data.model.ExpenseRecord
import com.example.ui.theme.BalanceGold
import com.example.ui.theme.BalanceGoldLight
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
import com.example.ui.viewmodel.WelfareViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: WelfareViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val role by viewModel.userRole.collectAsStateWithLifecycle()
    val collections by viewModel.allCollections.collectAsStateWithLifecycle()
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val activeIssues by viewModel.activeItemIssues.collectAsStateWithLifecycle()

    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Role Badge & Society Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (role.isAdmin) Emerald800 else MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (role.isAdmin) Gold500 else Emerald800,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (role.isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Group,
                                contentDescription = null,
                                tint = if (role.isAdmin) Emerald900 else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = role.displayName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (role.isAdmin) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                            Text(
                                text = if (role.isAdmin) "Full Access (Add/Edit/Delete)" else "Transparency & View Access",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (role.isAdmin) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            if (role.isAdmin) {
                                viewModel.setUserRole(com.example.ui.viewmodel.UserRole.MEMBER)
                            } else {
                                viewModel.showPinPromptDialog.value = true
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = if (role.isAdmin) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (role.isAdmin) "Switch Role" else "Login Admin",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // 1. Hero Baqaya Balance Card (Remaining Balance)
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("baqaya_balance_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Emerald900, Emerald700, Gold700)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Baqaya Balance (بقایا فنڈز)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Surface(
                                color = Gold500.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Gold500.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Gold500,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Cash in Hand",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Rs. ${currencyFormat.format(stats.baqayaBalance)}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        )

                        Text(
                            text = "Available Society Funds for Community Aid & Welfare",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Mini Jama vs Kharch Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = JamaGreenLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Total Jama (Collection)",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.85f))
                                    )
                                }
                                Text(
                                    text = "Rs. ${currencyFormat.format(stats.totalCollection)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = KharchRedLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Total Kharch (Expense)",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.85f))
                                    )
                                }
                                Text(
                                    text = "Rs. ${currencyFormat.format(stats.totalExpense)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Twin Financial Metrics Cards (Jama Shuda & Total Kharch)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Jama Shuda Funds Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("jama_funds_card")
                        .clickable { viewModel.navigateTo(AppScreen.HISAB_KITAB) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = JamaGreenLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(JamaGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "آمدنی",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = JamaGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Total Jama Shuda",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "Rs. ${currencyFormat.format(stats.totalCollection)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = JamaGreen
                            )
                        )
                    }
                }

                // Total Kharch Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("kharch_funds_card")
                        .clickable { viewModel.navigateTo(AppScreen.HISAB_KITAB) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = KharchRedLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(KharchRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "اخراجات",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = KharchRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Total Kharch",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "Rs. ${currencyFormat.format(stats.totalExpense)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = KharchRed
                            )
                        )
                    }
                }
            }
        }

        // 3. Member Management Metrics: Total Members, Active Members, Pending Dues
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("members_metrics_card")
                    .clickable { viewModel.navigateTo(AppScreen.MEMBERS) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = Emerald800,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Society Members (اراکین سوسائٹی)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Members",
                            tint = Emerald800,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Total Members
                        MetricBadge(
                            count = stats.totalMembers.toString(),
                            label = "Total Members",
                            subLabel = "کل اراکین",
                            badgeColor = Emerald800,
                            icon = Icons.Default.Group
                        )

                        // Active Members
                        MetricBadge(
                            count = stats.activeMembers.toString(),
                            label = "Active Members",
                            subLabel = "فعال ممبران",
                            badgeColor = JamaGreen,
                            icon = Icons.Default.CheckCircle
                        )

                        // Pending Dues
                        MetricBadge(
                            count = stats.pendingDuesMembersCount.toString(),
                            label = "Pending Dues",
                            subLabel = "بقایا چندہ",
                            badgeColor = if (stats.pendingDuesMembersCount > 0) KharchRed else Slate500,
                            icon = Icons.Default.Warning
                        )
                    }
                }
            }
        }

        // 4. Welfare Saman Inventory Status (Deg, Plates, Chairs Hisab)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welfare_saman_card")
                    .clickable { viewModel.navigateTo(AppScreen.WELFARE_SAMAN) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SoupKitchen,
                                contentDescription = null,
                                tint = Gold700,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Welfare Items (دیگ، کرسیاں، برتن)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Saman",
                            tint = Gold700,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Deg Status
                        SamanMiniCard(
                            title = "Cooking Degs",
                            available = stats.availableDegs,
                            total = stats.totalDegs,
                            icon = Icons.Default.SoupKitchen
                        )
                        // Chairs Status
                        SamanMiniCard(
                            title = "Chairs",
                            available = stats.availableChairs,
                            total = stats.totalChairs,
                            icon = Icons.Default.Chair
                        )
                        // Plates Status
                        SamanMiniCard(
                            title = "Plates & Glasses",
                            available = stats.availablePlates,
                            total = stats.totalPlates,
                            icon = Icons.Default.SoupKitchen
                        )
                    }

                    if (stats.activeIssuesCount > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Gold100)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Gold700,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${stats.activeIssuesCount} items currently issued to members for events",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Gold700,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. Admin Quick Actions (If in Admin Role)
        if (role.isAdmin) {
            item {
                Text(
                    text = "Admin Quick Actions / انتظامی اختیارات",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.editingChanda.value = null
                            viewModel.preselectedMemberForChanda.value = null
                            viewModel.showAddEditChandaDialog.value = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("collect_chanda_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JamaGreen),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Collect Chanda", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.editingExpense.value = null
                            viewModel.showAddEditExpenseDialog.value = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("record_expense_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KharchRed),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Kharch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.preselectedItemForIssue.value = null
                            viewModel.showIssueItemDialog.value = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("issue_saman_button"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.SoupKitchen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Issue Saman", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.editingMember.value = null
                            viewModel.showAddEditMemberDialog.value = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_member_button"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Member", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // 6. Recent Roznamcha / Transactions Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Hisab Kitab (حالیہ لین دین)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Emerald800,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.HISAB_KITAB) }
                        .padding(4.dp)
                )
            }
        }

        // Recent Collections & Expenses combined preview (first 4)
        val recentCollections = collections.take(3).map {
            TransactionItem(
                title = "Chanda: ${it.memberName}",
                subtitle = "${it.forMonth} • Rec #${it.receiptNumber.ifBlank { "N/A" }}",
                amount = it.amount,
                date = it.paymentDate,
                isIncome = true
            )
        }
        val recentExpenses = expenses.take(3).map {
            TransactionItem(
                title = it.title,
                subtitle = "${it.category} • Paid to: ${it.paidTo.ifBlank { "Society" }}",
                amount = it.amount,
                date = it.date,
                isIncome = false
            )
        }
        val combinedRecent = (recentCollections + recentExpenses).sortedByDescending { it.date }.take(4)

        if (combinedRecent.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "No hisab entries yet. Admin can record collections and expenses.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(combinedRecent) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (item.isIncome) JamaGreenLight else KharchRedLight,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (item.isIncome) JamaGreen else KharchRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = "${dateFormat.format(Date(item.date))} • ${item.subtitle}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate500
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        Text(
                            text = "${if (item.isIncome) "+Rs. " else "-Rs. "}${currencyFormat.format(item.amount)}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isIncome) JamaGreen else KharchRed
                            )
                        )
                    }
                }
            }
        }
    }
}

private data class TransactionItem(
    val title: String,
    val subtitle: String,
    val amount: Double,
    val date: Long,
    val isIncome: Boolean
)

@Composable
private fun MetricBadge(
    count: String,
    label: String,
    subLabel: String,
    badgeColor: Color,
    icon: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(badgeColor.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = badgeColor
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = subLabel,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = Slate500
            )
        )
    }
}

@Composable
private fun SamanMiniCard(
    title: String,
    available: Int,
    total: Int,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier.width(102.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Emerald800,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$available / $total",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Emerald800
                )
            )
            Text(
                text = "Avail / Total",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Slate500)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )
        }
    }
}
