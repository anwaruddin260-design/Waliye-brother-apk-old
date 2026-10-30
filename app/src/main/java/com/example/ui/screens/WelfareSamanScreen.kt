package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.ItemIssue
import com.example.data.model.StockLog
import com.example.data.model.WelfareItem
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WelfareSamanScreen(
    viewModel: WelfareViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    val context = LocalContext.current
    val items by viewModel.allWelfareItems.collectAsStateWithLifecycle()
    val stockLogs by viewModel.allStockLogs.collectAsStateWithLifecycle()
    val activeIssues by viewModel.activeItemIssues.collectAsStateWithLifecycle()
    val allIssues by viewModel.allItemIssues.collectAsStateWithLifecycle()
    val role by viewModel.userRole.collectAsStateWithLifecycle()

    val inventoryTypeFilter by viewModel.samanInventoryTypeFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.samanSearchQuery.collectAsStateWithLifecycle()

    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: Inventory & Stock, 1: Active Issued, 2: Stock Logs
    var itemToDelete by remember { mutableStateOf<WelfareItem?>(null) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Filter items based on inventory type (Emergency, Donation, Loan) and search
    val filteredItems = items.filter { item ->
        val matchesType = when (inventoryTypeFilter) {
            "LOAN" -> item.inventoryType.equals("Loan", ignoreCase = true)
            "DONATION" -> item.inventoryType.equals("Donation", ignoreCase = true)
            "EMERGENCY" -> item.inventoryType.equals("Emergency", ignoreCase = true)
            else -> true
        }

        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true) ||
                item.donorOrSource.contains(searchQuery, ignoreCase = true) ||
                item.storageLocation.contains(searchQuery, ignoreCase = true)

        matchesType && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Welfare Inventory & Stock",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Emergency, Donation & Loan Management",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                            )
                        }
                    }

                    if (role.isAdmin) {
                        Button(
                            onClick = {
                                viewModel.editingWelfareItem.value = null
                                viewModel.showAddEditWelfareItemDialog.value = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_item_top_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Summary Stats Strip (Loan, Donation, Emergency counts)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Loan Total
                        val loanItems = items.filter { it.inventoryType.equals("Loan", ignoreCase = true) }
                        StockStatPill(
                            title = "Loan Items",
                            urdu = "عاریتاً سامان",
                            count = "${loanItems.sumOf { it.availableQuantity }}/${loanItems.sumOf { it.totalQuantity }}",
                            color = Emerald800,
                            icon = Icons.Default.SwapHoriz
                        )

                        // Donation Total
                        val donationItems = items.filter { it.inventoryType.equals("Donation", ignoreCase = true) }
                        StockStatPill(
                            title = "Donations",
                            urdu = "عطیات و راشن",
                            count = "${donationItems.sumOf { it.availableQuantity }}/${donationItems.sumOf { it.totalQuantity }}",
                            color = Gold700,
                            icon = Icons.Default.CardGiftcard
                        )

                        // Emergency Total
                        val emergencyItems = items.filter { it.inventoryType.equals("Emergency", ignoreCase = true) }
                        StockStatPill(
                            title = "Emergency",
                            urdu = "ہنگامی امداد",
                            count = "${emergencyItems.sumOf { it.availableQuantity }}/${emergencyItems.sumOf { it.totalQuantity }}",
                            color = KharchRed,
                            icon = Icons.Default.LocalHospital
                        )
                    }
                }
            }

            // Tabs (0: Inventory Stock, 1: Currently Issued, 2: Stock Logs)
            item {
                TabRow(
                    selectedTabIndex = selectedMainTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Emerald800
                ) {
                    Tab(
                        selected = selectedMainTab == 0,
                        onClick = { selectedMainTab = 0 },
                        text = { Text("Stock (${items.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedMainTab == 1,
                        onClick = { selectedMainTab = 1 },
                        text = { Text("Issued (${activeIssues.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedMainTab == 2,
                        onClick = { selectedMainTab = 2 },
                        text = { Text("Stock Logs", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                    )
                }
            }

            // Tab 0: Stock Management with Emergency, Donation, Loan Filter Chips
            if (selectedMainTab == 0) {
                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.samanSearchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search by name, donor, category...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Emerald800) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.samanSearchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald800,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }

                // Inventory Type Filters: All, Loan, Donation, Emergency
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = inventoryTypeFilter == "ALL",
                                onClick = { viewModel.samanInventoryTypeFilter.value = "ALL" },
                                label = { Text("All (${items.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Emerald800,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        item {
                            FilterChip(
                                selected = inventoryTypeFilter == "LOAN",
                                onClick = { viewModel.samanInventoryTypeFilter.value = "LOAN" },
                                label = { Text("Loan / عاریتاً (${items.count { it.inventoryType.equals("Loan", ignoreCase = true) }})") },
                                leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Emerald800,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }

                        item {
                            FilterChip(
                                selected = inventoryTypeFilter == "DONATION",
                                onClick = { viewModel.samanInventoryTypeFilter.value = "DONATION" },
                                label = { Text("Donation / عطیات (${items.count { it.inventoryType.equals("Donation", ignoreCase = true) }})") },
                                leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Gold700,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }

                        item {
                            FilterChip(
                                selected = inventoryTypeFilter == "EMERGENCY",
                                onClick = { viewModel.samanInventoryTypeFilter.value = "EMERGENCY" },
                                label = { Text("Emergency / ہنگامی (${items.count { it.inventoryType.equals("Emergency", ignoreCase = true) }})") },
                                leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = KharchRed,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }
                }

                if (filteredItems.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = Slate500, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No items found under this filter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Select another filter chip or add a new welfare item.", style = MaterialTheme.typography.bodySmall, color = Slate500)
                            }
                        }
                    }
                } else {
                    items(filteredItems, key = { it.id }) { item ->
                        WelfareItemCard(
                            item = item,
                            isAdmin = role.isAdmin,
                            onUpdateStockClick = {
                                viewModel.stockItemToUpdate.value = item
                                viewModel.showUpdateStockDialog.value = true
                            },
                            onHistoryClick = {
                                viewModel.selectedItemForHistory.value = item
                                viewModel.showStockHistoryDialog.value = true
                            },
                            onIssueClick = {
                                viewModel.preselectedItemForIssue.value = item
                                viewModel.showIssueItemDialog.value = true
                            },
                            onEditClick = {
                                viewModel.editingWelfareItem.value = item
                                viewModel.showAddEditWelfareItemDialog.value = true
                            },
                            onDeleteClick = { itemToDelete = item }
                        )
                    }
                }
            }

            // Tab 1: Currently Issued Items
            if (selectedMainTab == 1) {
                if (activeIssues.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = JamaGreen, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("All welfare items are currently in Godown / Available", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("No items are currently issued to members.", style = MaterialTheme.typography.bodySmall, color = Slate500)
                            }
                        }
                    }
                } else {
                    items(activeIssues, key = { it.id }) { issue ->
                        ActiveIssueCard(
                            issue = issue,
                            isAdmin = role.isAdmin,
                            onReturnClick = {
                                viewModel.returningIssue.value = issue
                                viewModel.showReturnItemDialog.value = true
                            },
                            onCallClick = {
                                if (issue.borrowerPhone.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = android.net.Uri.parse("tel:${issue.borrowerPhone}")
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        )
                    }
                }
            }

            // Tab 2: Stock Logs
            if (selectedMainTab == 2) {
                if (stockLogs.isEmpty()) {
                    item {
                        Text("No stock movement logs recorded yet.", modifier = Modifier.padding(16.dp), color = Slate500)
                    }
                } else {
                    items(stockLogs, key = { it.id }) { log ->
                        StockLogCard(log = log)
                    }
                }
            }
        }

        // Admin Add New Item FAB
        if (role.isAdmin && selectedMainTab == 0) {
            FloatingActionButton(
                onClick = {
                    viewModel.editingWelfareItem.value = null
                    viewModel.showAddEditWelfareItemDialog.value = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 20.dp)
                    .testTag("add_welfare_item_fab"),
                containerColor = Emerald800,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Item")
            }
        }
    }

    // Delete item dialog
    if (itemToDelete != null) {
        val target = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Inventory Item") },
            text = { Text("Are you sure you want to delete ${target.name} from society inventory?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWelfareItem(target)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KharchRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WelfareItemCard(
    item: WelfareItem,
    isAdmin: Boolean,
    onUpdateStockClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onIssueClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val progress = if (item.totalQuantity > 0) item.availableQuantity.toFloat() / item.totalQuantity.toFloat() else 0f
    val isLowStock = item.availableQuantity <= item.minStockThreshold

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("welfare_item_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Type Badge + Category + Item Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                when (item.inventoryType) {
                                    "Emergency" -> KharchRedLight
                                    "Donation" -> Gold100
                                    else -> Emerald800.copy(alpha = 0.12f)
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (item.inventoryType) {
                                "Emergency" -> Icons.Default.LocalHospital
                                "Donation" -> Icons.Default.CardGiftcard
                                else -> if (item.category.contains("Deg")) Icons.Default.SoupKitchen else Icons.Default.Chair
                            },
                            contentDescription = null,
                            tint = when (item.inventoryType) {
                                "Emergency" -> KharchRed
                                "Donation" -> Gold700
                                else -> Emerald800
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Inventory Type Badge (Emergency, Donation, Loan)
                            Surface(
                                color = when (item.inventoryType) {
                                    "Emergency" -> KharchRedLight
                                    "Donation" -> Gold100
                                    else -> Emerald800.copy(alpha = 0.12f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (item.inventoryType) {
                                            "Emergency" -> "Emergency (ہنگامی)"
                                            "Donation" -> "Donation (عطیہ)"
                                            else -> "Loan (عاریتاً)"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (item.inventoryType) {
                                                "Emergency" -> KharchRed
                                                "Donation" -> Gold700
                                                else -> Emerald800
                                            }
                                        )
                                    )
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = Slate500
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Low Stock Warning Alert
                if (isLowStock) {
                    Surface(
                        color = KharchRedLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = KharchRed, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Low Stock", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KharchRed)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stock Bar & Quantities
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stock Level: ${item.availableQuantity} of ${item.totalQuantity} ${item.unit} Available",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (item.availableQuantity > 0) Emerald800 else KharchRed
                    )
                )

                Text(
                    text = "${item.totalQuantity - item.availableQuantity} Issued",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    progress > 0.4f -> Emerald800
                    progress > 0.15f -> Gold500
                    else -> KharchRed
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Lines (Donor / Source and Location)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🏷 Source: ${item.donorOrSource}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500),
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "📦 ${item.storageLocation}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500),
                    maxLines = 1
                )
            }

            // Admin Action Controls (Update Stock, Stock History, Issue)
            if (isAdmin) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Update Stock Button
                        Button(
                            onClick = onUpdateStockClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("update_stock_btn_${item.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Update Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Stock Movement History Button
                        OutlinedButton(
                            onClick = onHistoryClick,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Logs", fontSize = 11.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Issue item button (for loan or emergency items)
                        if (item.inventoryType != "Donation" || item.availableQuantity > 0) {
                            TextButton(
                                onClick = onIssueClick,
                                enabled = item.availableQuantity > 0,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (item.availableQuantity > 0) "Issue" else "Empty",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.availableQuantity > 0) Emerald800 else Slate500
                                )
                            }
                        }

                        IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Gold700, modifier = Modifier.size(16.dp))
                        }

                        IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = KharchRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StockLogCard(log: StockLog) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (log.quantityChange >= 0) JamaGreenLight else KharchRedLight,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (log.quantityChange >= 0) Icons.Default.TrendingUp else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (log.quantityChange >= 0) JamaGreen else KharchRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = log.itemName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${log.inventoryType} • ${log.reasonOrDonor}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }

                Surface(
                    color = if (log.quantityChange >= 0) JamaGreenLight else KharchRedLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${if (log.quantityChange >= 0) "+${log.quantityChange}" else "${log.quantityChange}"}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (log.quantityChange >= 0) JamaGreen else KharchRed
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Stock: ${log.previousTotal} → ${log.newTotal}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = dateFormat.format(Date(log.timestamp)),
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )
            }
        }
    }
}

@Composable
private fun StockStatPill(
    title: String,
    urdu: String,
    count: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = count, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = color))
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold))
        Text(text = urdu, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Slate500))
    }
}

@Composable
fun ActiveIssueCard(
    issue: ItemIssue,
    isAdmin: Boolean,
    onReturnClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val isOverdue = issue.status == "ISSUED" && System.currentTimeMillis() > issue.expectedReturnDate

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (issue.status == "ISSUED") Gold100 else JamaGreenLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (issue.status == "ISSUED") Icons.Default.Event else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (issue.status == "ISSUED") Gold700 else JamaGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${issue.quantity}x ${issue.itemName}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Borrower: ${issue.borrowerName} (${issue.purpose})",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }

                Surface(
                    color = if (issue.status == "ISSUED") {
                        if (isOverdue) KharchRedLight else Gold100
                    } else JamaGreenLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (issue.status == "ISSUED") {
                            if (isOverdue) "OVERDUE" else "ISSUED"
                        } else "RETURNED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (issue.status == "ISSUED") {
                                if (isOverdue) KharchRed else Gold700
                            } else JamaGreen
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Issued: ${dateFormat.format(Date(issue.issueDate))}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )

                Text(
                    text = "Due: ${dateFormat.format(Date(issue.expectedReturnDate))}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isOverdue) KharchRed else Emerald800
                    )
                )
            }

            if (issue.issueNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${issue.issueNotes}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )
            }

            if (isAdmin && issue.status == "ISSUED") {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (issue.borrowerPhone.isNotBlank()) {
                        IconButton(onClick = onCallClick, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = Emerald800, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = onReturnClick,
                        colors = ButtonDefaults.buttonColors(containerColor = JamaGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark Returned (واپسی درج کریں)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
