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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChandaRecord
import com.example.data.model.ExpenseRecord
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

sealed class LedgerEntry {
    abstract val id: Long
    abstract val title: String
    abstract val subtitle: String
    abstract val amount: Double
    abstract val date: Long

    data class Jama(val record: ChandaRecord) : LedgerEntry() {
        override val id: Long = record.id
        override val title: String = "Chanda: ${record.memberName}"
        override val subtitle: String = "${record.forMonth} • Rec #${record.receiptNumber.ifBlank { "N/A" }} • Method: ${record.paymentMethod}"
        override val amount: Double = record.amount
        override val date: Long = record.paymentDate
    }

    data class Kharch(val record: ExpenseRecord) : LedgerEntry() {
        override val id: Long = record.id
        override val title: String = record.title
        override val subtitle: String = "${record.category} • Paid to: ${record.paidTo.ifBlank { "Society" }} • Auth: ${record.authorizedBy}"
        override val amount: Double = record.amount
        override val date: Long = record.date
    }
}

@Composable
fun HisabKitabScreen(
    viewModel: WelfareViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    val context = LocalContext.current
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val collections by viewModel.allCollections.collectAsStateWithLifecycle()
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val role by viewModel.userRole.collectAsStateWithLifecycle()
    val tabFilter by viewModel.hisabTabFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.hisabSearchQuery.collectAsStateWithLifecycle()

    var itemToDelete by remember { mutableStateOf<LedgerEntry?>(null) }

    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Convert both into unified ledger entries
    val allEntries = remember(collections, expenses) {
        val list = mutableListOf<LedgerEntry>()
        collections.forEach { list.add(LedgerEntry.Jama(it)) }
        expenses.forEach { list.add(LedgerEntry.Kharch(it)) }
        list.sortedByDescending { it.date }
    }

    val filteredEntries = allEntries.filter { entry ->
        val matchesTab = when (tabFilter) {
            "JAMA" -> entry is LedgerEntry.Jama
            "KHARCH" -> entry is LedgerEntry.Kharch
            else -> true
        }

        val matchesSearch = entry.title.contains(searchQuery, ignoreCase = true) ||
                entry.subtitle.contains(searchQuery, ignoreCase = true)

        matchesTab && matchesSearch
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Dashboard")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Hisab Kitab (حساب کتاب)",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Financial Ledger & Roznamcha",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                            )
                        }
                    }

                    IconButton(onClick = {
                        shareLedgerSummary(context, stats.totalCollection, stats.totalExpense, stats.baqayaBalance, filteredEntries)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Hisab Summary", tint = Emerald800)
                    }
                }
            }

            // Financial Summary Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Roznamcha Summary (کل گوشوارہ)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                color = Gold100,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Baqaya: Rs. ${currencyFormat.format(stats.baqayaBalance)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Gold700,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = JamaGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Total Jama Shuda", style = MaterialTheme.typography.bodySmall, color = Slate500)
                                }
                                Text(
                                    text = "Rs. ${currencyFormat.format(stats.totalCollection)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = JamaGreen)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = KharchRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Total Kharch", style = MaterialTheme.typography.bodySmall, color = Slate500)
                                }
                                Text(
                                    text = "Rs. ${currencyFormat.format(stats.totalExpense)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = KharchRed)
                                )
                            }
                        }

                        if (role.isAdmin) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))

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
                                        .testTag("hisab_add_chanda_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = JamaGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Jama (Chanda)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.editingExpense.value = null
                                        viewModel.showAddEditExpenseDialog.value = true
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("hisab_add_expense_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = KharchRed),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Kharch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.hisabSearchQuery.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by receipt, title, member...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Emerald800) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.hisabSearchQuery.value = "" }) {
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

            // Tab Filter Chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = tabFilter == "ALL",
                        onClick = { viewModel.hisabTabFilter.value = "ALL" },
                        label = { Text("All (${allEntries.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald800,
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = tabFilter == "JAMA",
                        onClick = { viewModel.hisabTabFilter.value = "JAMA" },
                        label = { Text("Jama / Collections (${collections.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JamaGreen,
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = tabFilter == "KHARCH",
                        onClick = { viewModel.hisabTabFilter.value = "KHARCH" },
                        label = { Text("Kharch / Expenses (${expenses.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KharchRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Transactions List
            if (filteredEntries.isEmpty()) {
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
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Slate500, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No transactions found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                items(filteredEntries, key = { "${if (it is LedgerEntry.Jama) "j_" else "k_"}${it.id}" }) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(
                                                if (entry is LedgerEntry.Jama) JamaGreenLight else KharchRedLight,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (entry is LedgerEntry.Jama) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = if (entry is LedgerEntry.Jama) JamaGreen else KharchRed,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = entry.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = entry.subtitle,
                                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = dateFormat.format(Date(entry.date)),
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, color = Emerald800)
                                        )
                                    }
                                }

                                Text(
                                    text = "${if (entry is LedgerEntry.Jama) "+Rs. " else "-Rs. "}${currencyFormat.format(entry.amount)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (entry is LedgerEntry.Jama) JamaGreen else KharchRed
                                    )
                                )
                            }

                            // Admin Edit & Delete buttons
                            if (role.isAdmin) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            when (entry) {
                                                is LedgerEntry.Jama -> {
                                                    viewModel.editingChanda.value = entry.record
                                                    viewModel.showAddEditChandaDialog.value = true
                                                }
                                                is LedgerEntry.Kharch -> {
                                                    viewModel.editingExpense.value = entry.record
                                                    viewModel.showAddEditExpenseDialog.value = true
                                                }
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Gold700)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit", fontSize = 11.sp, color = Gold700)
                                    }

                                    TextButton(
                                        onClick = { itemToDelete = entry },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = KharchRed)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Delete", fontSize = 11.sp, color = KharchRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete dialog
    if (itemToDelete != null) {
        val entry = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this ${if (entry is LedgerEntry.Jama) "chanda collection" else "expense"} entry of Rs. ${currencyFormat.format(entry.amount)}?") },
            confirmButton = {
                Button(
                    onClick = {
                        when (entry) {
                            is LedgerEntry.Jama -> viewModel.deleteChanda(entry.record)
                            is LedgerEntry.Kharch -> viewModel.deleteExpense(entry.record)
                        }
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

fun shareLedgerSummary(
    context: Context,
    totalJama: Double,
    totalKharch: Double,
    baqaya: Double,
    entries: List<LedgerEntry>
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val sb = StringBuilder()
    sb.append("📊 *Waliye Brothers Welfare Society - Hisab Kitab Report*\n")
    sb.append("📅 Date: ${dateFormat.format(Date())}\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("🟢 *Total Jama Shuda (Collection):* Rs. ${totalJama.toInt()}\n")
    sb.append("🔴 *Total Kharch (Expense):* Rs. ${totalKharch.toInt()}\n")
    sb.append("💰 *Baqaya Balance (Cash in Hand):* Rs. ${baqaya.toInt()}\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("*Recent Transactions Summary:*\n")
    entries.take(10).forEach { item ->
        val sign = if (item is LedgerEntry.Jama) "➕" else "➖"
        sb.append("$sign ${item.title}: Rs. ${item.amount.toInt()} (${item.subtitle.take(25)})\n")
    }
    sb.append("\n_All members can inspect society accounts transparently._")

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Roznamcha Summary"))
}
