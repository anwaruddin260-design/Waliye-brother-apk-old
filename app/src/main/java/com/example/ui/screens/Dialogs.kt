package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChandaRecord
import com.example.data.model.ExpenseRecord
import com.example.data.model.ItemIssue
import com.example.data.model.Member
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AdminPinPromptDialog(
    viewModel: WelfareViewModel,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Gold100, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Gold700,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Admin Login", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("President / Treasurer Security PIN", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Please enter your 4-digit administration security PIN to activate full edit permissions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 8) {
                            pin = it
                            errorMessage = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input"),
                    label = { Text("Security PIN") },
                    placeholder = { Text("Enter PIN (e.g. 1234)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(errorMessage!!, color = KharchRed)
                        } else {
                            Text("Default setup PIN is 1234", color = Slate500)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { pin = "1234" }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp), tint = Emerald800)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tap here to prefill default PIN (1234)", fontSize = 11.sp, color = Emerald800)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.attemptAdminLogin(
                        enteredPin = pin,
                        onSuccess = {
                            onDismiss()
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                        },
                        onError = {
                            errorMessage = "Incorrect PIN. Default is 1234."
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                modifier = Modifier.testTag("admin_pin_submit_button")
            ) {
                Text("Verify & Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemberDialog(
    viewModel: WelfareViewModel,
    memberToEdit: Member?,
    onDismiss: () -> Unit
) {
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()
    val nextCodeNumber = remember(allMembers) { (allMembers.size + 1) }

    var fullName by remember { mutableStateOf(memberToEdit?.fullName ?: "") }
    var fatherName by remember { mutableStateOf(memberToEdit?.fatherName ?: "") }
    var memberCode by remember { mutableStateOf(memberToEdit?.memberCode ?: "WB-${String.format(java.util.Locale.US, "%03d", nextCodeNumber)}") }
    var phone by remember { mutableStateOf(memberToEdit?.phone ?: "+92 ") }
    var address by remember { mutableStateOf(memberToEdit?.address ?: "") }
    var monthlyChanda by remember { mutableStateOf(memberToEdit?.monthlyChandaAmount?.toInt()?.toString() ?: "500") }
    var selectedRole by remember { mutableStateOf(memberToEdit?.role ?: "Member") }
    var notes by remember { mutableStateOf(memberToEdit?.notes ?: "") }

    var roleExpanded by remember { mutableStateOf(false) }
    val roles = listOf("President", "Treasurer", "General Secretary", "Executive Member", "Member")

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (memberToEdit == null) "Add New Member" else "Edit Member Profile",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name * (نام)") },
                    placeholder = { Text("e.g. Haji Muhammad Anwaruddin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("member_name_input")
                )

                OutlinedTextField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    label = { Text("Father's Name (ولدیت)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = memberCode,
                        onValueChange = { memberCode = it },
                        label = { Text("Member ID") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = monthlyChanda,
                        onValueChange = { monthlyChanda = it },
                        label = { Text("Chanda (Rs/mo)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("member_chanda_input")
                    )
                }

                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedRole,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Society Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    selectedRole = role
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Residential Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Comments") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        val member = Member(
                            id = memberToEdit?.id ?: 0L,
                            memberCode = memberCode.ifBlank { "WB-000" },
                            fullName = fullName.trim(),
                            fatherName = fatherName.trim(),
                            role = selectedRole,
                            phone = phone.trim(),
                            address = address.trim(),
                            monthlyChandaAmount = monthlyChanda.toDoubleOrNull() ?: 500.0,
                            joinDate = memberToEdit?.joinDate ?: System.currentTimeMillis(),
                            isActive = true,
                            notes = notes.trim()
                        )
                        viewModel.saveMember(member)
                    }
                },
                enabled = fullName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                modifier = Modifier.testTag("save_member_button")
            ) {
                Text(if (memberToEdit == null) "Add Member" else "Update Member")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditChandaDialog(
    viewModel: WelfareViewModel,
    chandaToEdit: ChandaRecord?,
    preselectedMember: Member?,
    onDismiss: () -> Unit
) {
    val members by viewModel.allMembers.collectAsStateWithLifecycle()

    var selectedMember by remember {
        mutableStateOf(
            preselectedMember ?: members.find { it.id == chandaToEdit?.memberId } ?: members.firstOrNull()
        )
    }

    val currentMonth = remember {
        val cal = Calendar.getInstance()
        val monthNames = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        "${monthNames[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }

    var amount by remember {
        mutableStateOf(
            chandaToEdit?.amount?.toInt()?.toString()
                ?: selectedMember?.monthlyChandaAmount?.toInt()?.toString()
                ?: "500"
        )
    }

    var forMonth by remember { mutableStateOf(chandaToEdit?.forMonth ?: currentMonth) }
    var paymentMethod by remember { mutableStateOf(chandaToEdit?.paymentMethod ?: "Cash") }
    var receiptNumber by remember {
        mutableStateOf(
            chandaToEdit?.receiptNumber ?: "REC-${(100..999).random()}"
        )
    }
    var collectedBy by remember { mutableStateOf(chandaToEdit?.collectedBy ?: "Treasurer Tariq Wali") }
    var notes by remember { mutableStateOf(chandaToEdit?.notes ?: "Monthly Chanda") }

    var memberExpanded by remember { mutableStateOf(false) }
    var methodExpanded by remember { mutableStateOf(false) }
    val paymentMethods = listOf("Cash", "EasyPaisa", "JazzCash", "Bank Transfer")

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (chandaToEdit == null) "Collect Monthly Chanda (جمع چندہ)" else "Edit Chanda Record",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Member Selector
                ExposedDropdownMenuBox(
                    expanded = memberExpanded,
                    onExpandedChange = { memberExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMember?.let { "${it.fullName} (${it.memberCode})" } ?: "Select Member",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Member (رکن)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable).testTag("chanda_member_select")
                    )
                    ExposedDropdownMenu(
                        expanded = memberExpanded,
                        onDismissRequest = { memberExpanded = false }
                    ) {
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.fullName} (${m.memberCode}) - Rs. ${m.monthlyChandaAmount.toInt()}/mo") },
                                onClick = {
                                    selectedMember = m
                                    amount = m.monthlyChandaAmount.toInt().toString()
                                    memberExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (Rs.) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("chanda_amount_input")
                    )

                    OutlinedTextField(
                        value = forMonth,
                        onValueChange = { forMonth = it },
                        label = { Text("For Month") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Payment Method Dropdown
                    ExposedDropdownMenuBox(
                        expanded = methodExpanded,
                        onExpandedChange = { methodExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Method") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = methodExpanded,
                            onDismissRequest = { methodExpanded = false }
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method) },
                                    onClick = {
                                        paymentMethod = method
                                        methodExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = receiptNumber,
                        onValueChange = { receiptNumber = it },
                        label = { Text("Receipt #") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = collectedBy,
                    onValueChange = { collectedBy = it },
                    label = { Text("Collected By (وصول کنندہ)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (selectedMember != null && parsedAmount != null && parsedAmount > 0) {
                        val record = ChandaRecord(
                            id = chandaToEdit?.id ?: 0L,
                            memberId = selectedMember!!.id,
                            memberName = selectedMember!!.fullName,
                            amount = parsedAmount,
                            forMonth = forMonth.trim(),
                            paymentDate = chandaToEdit?.paymentDate ?: System.currentTimeMillis(),
                            paymentMethod = paymentMethod,
                            receiptNumber = receiptNumber.trim(),
                            collectedBy = collectedBy.trim(),
                            notes = notes.trim()
                        )
                        viewModel.saveChanda(record)
                    }
                },
                enabled = selectedMember != null && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = JamaGreen),
                modifier = Modifier.testTag("save_chanda_button")
            ) {
                Text(if (chandaToEdit == null) "Save & Collect" else "Update Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseDialog(
    viewModel: WelfareViewModel,
    expenseToEdit: ExpenseRecord?,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(expenseToEdit?.title ?: "") }
    var category by remember { mutableStateOf(expenseToEdit?.category ?: "Welfare Equipment") }
    var amount by remember { mutableStateOf(expenseToEdit?.amount?.toInt()?.toString() ?: "") }
    var paidTo by remember { mutableStateOf(expenseToEdit?.paidTo ?: "") }
    var authorizedBy by remember { mutableStateOf(expenseToEdit?.authorizedBy ?: "President & Treasurer") }
    var receiptNote by remember { mutableStateOf(expenseToEdit?.receiptNote ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf(
        "Welfare Equipment",
        "Deg / Saman Maintenance",
        "Kafan Dafan / Funeral Aid",
        "Ration / Needy Aid",
        "Community Event / Majlis",
        "Miscellaneous"
    )

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (expenseToEdit == null) "Record Kharch (خرچ کا اندراج)" else "Edit Expense",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title * (تفصیل)") },
                    placeholder = { Text("e.g. Deg Repair, Purchased 50 Chairs") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("expense_title_input")
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Expense Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (Rs.) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("expense_amount_input")
                    )

                    OutlinedTextField(
                        value = paidTo,
                        onValueChange = { paidTo = it },
                        label = { Text("Paid To") },
                        placeholder = { Text("Shop / Vendor") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = authorizedBy,
                    onValueChange = { authorizedBy = it },
                    label = { Text("Authorized By") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = receiptNote,
                    onValueChange = { receiptNote = it },
                    label = { Text("Receipt / Voucher Note") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (title.isNotBlank() && parsedAmount != null && parsedAmount > 0) {
                        val record = ExpenseRecord(
                            id = expenseToEdit?.id ?: 0L,
                            title = title.trim(),
                            category = category,
                            amount = parsedAmount,
                            date = expenseToEdit?.date ?: System.currentTimeMillis(),
                            paidTo = paidTo.trim(),
                            authorizedBy = authorizedBy.trim(),
                            receiptNote = receiptNote.trim()
                        )
                        viewModel.saveExpense(record)
                    }
                },
                enabled = title.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = KharchRed),
                modifier = Modifier.testTag("save_expense_button")
            ) {
                Text(if (expenseToEdit == null) "Record Expense" else "Update Expense")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWelfareItemDialog(
    viewModel: WelfareViewModel,
    itemToEdit: WelfareItem?,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var inventoryType by remember { mutableStateOf(itemToEdit?.inventoryType ?: "Loan") } // "Loan", "Donation", "Emergency"
    var category by remember { mutableStateOf(itemToEdit?.category ?: "Degs & Cauldrons") }
    var totalQuantity by remember { mutableStateOf(itemToEdit?.totalQuantity?.toString() ?: "5") }
    var unit by remember { mutableStateOf(itemToEdit?.unit ?: "Pieces") }
    var condition by remember { mutableStateOf(itemToEdit?.condition ?: "Good") }
    var storageLocation by remember { mutableStateOf(itemToEdit?.storageLocation ?: "Society Markaz") }
    var donorOrSource by remember { mutableStateOf(itemToEdit?.donorOrSource ?: "Society Fund Purchase") }
    var minStockThreshold by remember { mutableStateOf(itemToEdit?.minStockThreshold?.toString() ?: "2") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    val categories = when (inventoryType) {
        "Donation" -> listOf("Ration & Food Aid", "Winter Relief", "Livelihood Aid", "Clothing & Blankets", "Other Donation")
        "Emergency" -> listOf("Medical & Patient Aid", "Funeral / Kafan Dafan", "Emergency Shelter", "Disaster Relief")
        else -> listOf("Degs & Cauldrons", "Plates & Crockery", "Chairs & Seating", "Gas Burners / Bhatti", "Spoons & Cooking Tools", "Other Welfare Items")
    }

    val units = listOf("Pieces", "Degs", "Chairs", "Plates", "Bags", "Sets", "Units", "Kits")
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (itemToEdit == null) "Add Welfare Item (نیا سامان)" else "Edit Item Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Inventory Type Selector Chips (Emergency, Donation, Loan)
                Text(
                    text = "Inventory Type / سامان کی قسم *",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = inventoryType == "Loan",
                        onClick = {
                            inventoryType = "Loan"
                            category = "Degs & Cauldrons"
                        },
                        label = { Text("Loan", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald800,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = inventoryType == "Donation",
                        onClick = {
                            inventoryType = "Donation"
                            category = "Ration & Food Aid"
                        },
                        label = { Text("Donation", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Gold700,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = inventoryType == "Emergency",
                        onClick = {
                            inventoryType = "Emergency"
                            category = "Medical & Patient Aid"
                        },
                        label = { Text("Emergency", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KharchRed,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name * (سامان کا نام)") },
                    placeholder = {
                        Text(
                            when (inventoryType) {
                                "Donation" -> "e.g. Monthly Ration Pack, Warm Quilt"
                                "Emergency" -> "e.g. Patient Wheelchair, Oxygen Kit"
                                else -> "e.g. Large Daig (15 Kg), Banquet Chairs"
                            }
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("item_name_input")
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Total Quantity & Unit
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalQuantity,
                        onValueChange = { totalQuantity = it },
                        label = { Text("Initial Stock *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("item_qty_input")
                    )

                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { unitExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            units.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u) },
                                    onClick = {
                                        unit = u
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = donorOrSource,
                    onValueChange = { donorOrSource = it },
                    label = { Text("Donor / Source (عطیہ دہندہ یا ذریعہ)") },
                    placeholder = { Text("e.g. Donated by Tariq Wali / Society Fund") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = condition,
                        onValueChange = { condition = it },
                        label = { Text("Condition") },
                        placeholder = { Text("Good, New") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minStockThreshold,
                        onValueChange = { minStockThreshold = it },
                        label = { Text("Alert Below") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = { storageLocation = it },
                    label = { Text("Storage Location (جگہ)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedQty = totalQuantity.toIntOrNull()
                    val threshold = minStockThreshold.toIntOrNull() ?: 2
                    if (name.isNotBlank() && parsedQty != null && parsedQty > 0) {
                        val prevIssued = (itemToEdit?.totalQuantity ?: 0) - (itemToEdit?.availableQuantity ?: 0)
                        val newAvailable = (parsedQty - prevIssued).coerceAtLeast(0)

                        val item = WelfareItem(
                            id = itemToEdit?.id ?: 0L,
                            name = name.trim(),
                            category = category,
                            inventoryType = inventoryType,
                            totalQuantity = parsedQty,
                            availableQuantity = newAvailable,
                            unit = unit,
                            condition = condition.trim().ifBlank { "Good" },
                            storageLocation = storageLocation.trim().ifBlank { "Society Markaz" },
                            donorOrSource = donorOrSource.trim().ifBlank { "Society Welfare Purchase" },
                            minStockThreshold = threshold,
                            lastStockUpdate = System.currentTimeMillis()
                        )
                        viewModel.saveWelfareItem(item)
                    }
                },
                enabled = name.isNotBlank() && (totalQuantity.toIntOrNull() ?: 0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                modifier = Modifier.testTag("save_welfare_item_btn")
            ) {
                Text(if (itemToEdit == null) "Add to Inventory" else "Update Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Dedicated Admin Stock Level Update Dialog (Add stock or adjust stock)
@Composable
fun UpdateStockDialog(
    viewModel: WelfareViewModel,
    item: WelfareItem,
    onDismiss: () -> Unit
) {
    var selectedMode by remember { mutableIntStateOf(0) } // 0: Add Stock (+), 1: Adjust Exact Level

    // Mode 0: Add Incoming Stock
    var quantityToAdd by remember { mutableStateOf("5") }
    var additionReason by remember {
        mutableStateOf(
            when (item.inventoryType) {
                "Donation" -> "Donation received from community well-wisher"
                "Emergency" -> "Emergency reserve stock replenishment"
                else -> "Purchased additional inventory for society"
            }
        )
    }

    // Mode 1: Exact Total & Available Adjustment
    var newTotal by remember { mutableStateOf(item.totalQuantity.toString()) }
    var newAvailable by remember { mutableStateOf(item.availableQuantity.toString()) }
    var auditReason by remember { mutableStateOf("Annual inventory recount & audit") }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            when (item.inventoryType) {
                                "Emergency" -> KharchRedLight
                                "Donation" -> Gold100
                                else -> JamaGreenLight
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.inventoryType) {
                            "Emergency" -> Icons.Default.LocalHospital
                            "Donation" -> Icons.Default.CardGiftcard
                            else -> Icons.Default.SoupKitchen
                        },
                        contentDescription = null,
                        tint = when (item.inventoryType) {
                            "Emergency" -> KharchRed
                            "Donation" -> Gold700
                            else -> JamaGreen
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Update Stock Levels (اسٹاک)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${item.name} • ${item.inventoryType}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                        maxLines = 1
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Current Stock Overview Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Current Total", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                            Text(
                                text = "${item.totalQuantity} ${item.unit}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Available", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                            Text(
                                text = "${item.availableQuantity} ${item.unit}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.availableQuantity > 0) Emerald800 else KharchRed
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Issued/Used", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                            Text(
                                text = "${item.totalQuantity - item.availableQuantity} ${item.unit}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // Tab Switcher between Add Stock vs Adjust Level
                TabRow(
                    selectedTabIndex = selectedMode,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Emerald800
                ) {
                    Tab(
                        selected = selectedMode == 0,
                        onClick = { selectedMode = 0 },
                        text = { Text("Add Stock (+)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedMode == 1,
                        onClick = { selectedMode = 1 },
                        text = { Text("Set Exact Level", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                if (selectedMode == 0) {
                    // MODE 0: Add Incoming Stock
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Add incoming stock quantity to current inventory:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )

                        // Quick increment suggestion chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(1, 5, 10, 25, 50).forEach { qty ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (quantityToAdd == qty.toString()) Emerald800 else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { quantityToAdd = qty.toString() }
                                ) {
                                    Text(
                                        text = "+$qty",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (quantityToAdd == qty.toString()) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = quantityToAdd,
                            onValueChange = { quantityToAdd = it },
                            label = { Text("Quantity to Add (${item.unit}) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_stock_qty_input")
                        )

                        OutlinedTextField(
                            value = additionReason,
                            onValueChange = { additionReason = it },
                            label = { Text("Source / Donor / Reason") },
                            placeholder = { Text("e.g. Received from Br. Bilal / Market Purchase") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_stock_reason_input")
                        )

                        val previewNewTotal = (item.totalQuantity + (quantityToAdd.toIntOrNull() ?: 0))
                        Text(
                            text = "Projected New Total: $previewNewTotal ${item.unit}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald800
                            )
                        )
                    }
                } else {
                    // MODE 1: Exact Total & Available Adjustment
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Update exact total and available stock levels (e.g. after recount or damaged items):",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newTotal,
                                onValueChange = { newTotal = it },
                                label = { Text("Total Stock *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("adjust_total_stock_input")
                            )

                            OutlinedTextField(
                                value = newAvailable,
                                onValueChange = { newAvailable = it },
                                label = { Text("Available *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("adjust_avail_stock_input")
                            )
                        }

                        OutlinedTextField(
                            value = auditReason,
                            onValueChange = { auditReason = it },
                            label = { Text("Adjustment Reason *") },
                            placeholder = { Text("e.g. Physical inventory audit / Damaged item write-off") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("adjust_reason_input")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedMode == 0) {
                        val toAdd = quantityToAdd.toIntOrNull()
                        if (toAdd != null && toAdd > 0) {
                            viewModel.addStockLevel(
                                item = item,
                                quantityToAdd = toAdd,
                                changeType = if (item.inventoryType == "Donation") "DONATION_RECEIVED" else "STOCK_ADDED",
                                reasonOrDonor = additionReason.trim()
                            )
                        }
                    } else {
                        val total = newTotal.toIntOrNull()
                        val available = newAvailable.toIntOrNull()
                        if (total != null && available != null && total >= 0 && available >= 0) {
                            viewModel.adjustStockLevel(
                                item = item,
                                newTotal = total,
                                newAvailable = available.coerceAtMost(total),
                                reason = auditReason.trim()
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                modifier = Modifier.testTag("confirm_update_stock_btn")
            ) {
                Text(if (selectedMode == 0) "Add Stock" else "Save Adjusted Stock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Stock Movement History Dialog
@Composable
fun StockHistoryDialog(
    item: WelfareItem,
    logs: List<StockLog>,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = Emerald800)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Stock Movement History", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(item.name, style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                }
            }
        },
        text = {
            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No stock changes recorded yet.", style = MaterialTheme.typography.bodyMedium, color = Slate500)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(logs) { log ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (log.changeType) {
                                            "STOCK_ADDED" -> "Stock Added (+${log.quantityChange})"
                                            "DONATION_RECEIVED" -> "Donation Received (+${log.quantityChange})"
                                            "EMERGENCY_ALLOCATED" -> "Emergency Allocated"
                                            "STOCK_ADJUSTED" -> "Stock Adjusted (${if (log.quantityChange >= 0) "+${log.quantityChange}" else "${log.quantityChange}"})"
                                            else -> log.changeType
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (log.quantityChange >= 0) JamaGreen else KharchRed
                                        )
                                    )

                                    Text(
                                        text = "${log.previousTotal} → ${log.newTotal} ${item.unit}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                if (log.reasonOrDonor.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = log.reasonOrDonor,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = dateFormat.format(Date(log.timestamp)),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate500)
                                    )
                                    Text(
                                        text = "By: ${log.updatedBy}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate500)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueItemDialog(
    viewModel: WelfareViewModel,
    preselectedItem: WelfareItem?,
    onDismiss: () -> Unit
) {
    val items by viewModel.allWelfareItems.collectAsStateWithLifecycle()
    val members by viewModel.allMembers.collectAsStateWithLifecycle()

    var selectedItem by remember {
        mutableStateOf(
            preselectedItem ?: items.firstOrNull { it.availableQuantity > 0 } ?: items.firstOrNull()
        )
    }

    var selectedMember by remember { mutableStateOf<Member?>(null) }
    var borrowerName by remember { mutableStateOf("") }
    var borrowerPhone by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var returnDays by remember { mutableStateOf("3") }
    var purpose by remember { mutableStateOf("Shadi / Wedding Dawat") }
    var notes by remember { mutableStateOf("") }

    var itemExpanded by remember { mutableStateOf(false) }
    var memberExpanded by remember { mutableStateOf(false) }
    var purposeExpanded by remember { mutableStateOf(false) }

    val purposes = listOf(
        "Shadi / Wedding Dawat",
        "Khatam-e-Quran & Niyaz",
        "Majlis / Religious Gathering",
        "Funeral / Kafan Dafan Assistance",
        "Medical Emergency / Patient Care",
        "Family Gathering / Dawat"
    )

    val maxAvailable = selectedItem?.availableQuantity ?: 0
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Issue Saman (سامان جاری کریں)",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Item Selector
                ExposedDropdownMenuBox(
                    expanded = itemExpanded,
                    onExpandedChange = { itemExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedItem?.let { "${it.name} (${it.availableQuantity} of ${it.totalQuantity} ${it.unit} available)" } ?: "Select Item",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Item to Issue *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = itemExpanded,
                        onDismissRequest = { itemExpanded = false }
                    ) {
                        items.forEach { item ->
                            DropdownMenuItem(
                                text = { Text("${item.name} [${item.inventoryType}] (${item.availableQuantity} ${item.unit} available)") },
                                onClick = {
                                    selectedItem = item
                                    itemExpanded = false
                                }
                            )
                        }
                    }
                }

                // Member Selection (optional, can also be non-member borrower)
                ExposedDropdownMenuBox(
                    expanded = memberExpanded,
                    onExpandedChange = { memberExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMember?.fullName ?: "Select Society Member (Optional)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Society Member") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = memberExpanded,
                        onDismissRequest = { memberExpanded = false }
                    ) {
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.fullName} (${m.memberCode})") },
                                onClick = {
                                    selectedMember = m
                                    borrowerName = m.fullName
                                    borrowerPhone = m.phone
                                    memberExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = borrowerName,
                    onValueChange = { borrowerName = it },
                    label = { Text("Borrower / Beneficiary Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("issue_borrower_name")
                )

                OutlinedTextField(
                    value = borrowerPhone,
                    onValueChange = { borrowerPhone = it },
                    label = { Text("Borrower Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity (Max $maxAvailable) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("issue_qty_input")
                    )

                    OutlinedTextField(
                        value = returnDays,
                        onValueChange = { returnDays = it },
                        label = { Text("Duration (Days)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Purpose Dropdown
                ExposedDropdownMenuBox(
                    expanded = purposeExpanded,
                    onExpandedChange = { purposeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = purpose,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Purpose / Event") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = purposeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = purposeExpanded,
                        onDismissRequest = { purposeExpanded = false }
                    ) {
                        purposes.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    purpose = p
                                    purposeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (e.g. includes accessories)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedQty = quantity.toIntOrNull()
                    val days = returnDays.toLongOrNull() ?: 3L
                    if (selectedItem != null && borrowerName.isNotBlank() && parsedQty != null && parsedQty > 0 && parsedQty <= maxAvailable) {
                        val issue = ItemIssue(
                            itemId = selectedItem!!.id,
                            itemName = selectedItem!!.name,
                            memberId = selectedMember?.id,
                            borrowerName = borrowerName.trim(),
                            borrowerPhone = borrowerPhone.trim(),
                            quantity = parsedQty,
                            issueDate = System.currentTimeMillis(),
                            expectedReturnDate = System.currentTimeMillis() + (days * 86400000L),
                            actualReturnDate = null,
                            purpose = purpose,
                            status = "ISSUED",
                            issueNotes = notes.trim()
                        )
                        viewModel.issueItem(issue)
                    }
                },
                enabled = selectedItem != null && borrowerName.isNotBlank() && (quantity.toIntOrNull() ?: 0) in 1..maxAvailable,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                modifier = Modifier.testTag("confirm_issue_button")
            ) {
                Text("Confirm Issue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReturnItemDialog(
    viewModel: WelfareViewModel,
    issue: ItemIssue,
    onDismiss: () -> Unit
) {
    var condition by remember { mutableStateOf("Good") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AssignmentReturn, contentDescription = null, tint = JamaGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Return Saman (واپسی)", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Confirm return of ${issue.quantity}x ${issue.itemName} from ${issue.borrowerName}.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text("Item Condition on Return") },
                    placeholder = { Text("Cleaned, Good, Needs Qalai/Repair") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Return Remarks") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.returnItem(issue, condition.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = JamaGreen)
            ) {
                Text("Confirm Return to Godown")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
