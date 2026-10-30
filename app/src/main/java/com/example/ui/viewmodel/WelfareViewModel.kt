package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.ChandaRecord
import com.example.data.model.ExpenseRecord
import com.example.data.model.ItemIssue
import com.example.data.model.Member
import com.example.data.model.StockLog
import com.example.data.model.WelfareItem
import com.example.data.repository.WelfareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class UserRole(val displayName: String, val subtitle: String, val isAdmin: Boolean) {
    ADMIN("Admin (President / Treasurer)", "صدر و خزانچی - Full Authority", true),
    MEMBER("Society Member", "معزز رکن - View & Transparency", false)
}

enum class AppScreen {
    SPLASH,
    DASHBOARD,
    MEMBERS,
    MEMBER_DETAIL,
    HISAB_KITAB,
    WELFARE_SAMAN,
    SETTINGS
}

data class MemberDuesInfo(
    val member: Member,
    val totalPaid: Double,
    val expectedDues: Double,
    val pendingAmount: Double,
    val hasPendingDues: Boolean,
    val lastPaymentDate: Long?,
    val activeBorrowCount: Int
)

data class DashboardStats(
    val totalCollection: Double = 0.0,
    val totalExpense: Double = 0.0,
    val baqayaBalance: Double = 0.0,
    val totalMembers: Int = 0,
    val activeMembers: Int = 0,
    val pendingDuesMembersCount: Int = 0,
    val totalDegs: Int = 0,
    val availableDegs: Int = 0,
    val totalChairs: Int = 0,
    val availableChairs: Int = 0,
    val totalPlates: Int = 0,
    val availablePlates: Int = 0,
    val totalLoanItems: Int = 0,
    val totalDonationItems: Int = 0,
    val totalEmergencyItems: Int = 0,
    val activeIssuesCount: Int = 0
)

class WelfareViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = WelfareRepository(database)

    // Navigation & Role
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.ADMIN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _selectedMemberId = MutableStateFlow<Long?>(null)
    val selectedMemberId: StateFlow<Long?> = _selectedMemberId.asStateFlow()

    // Search and filter states
    val memberSearchQuery = MutableStateFlow("")
    val memberFilter = MutableStateFlow("ALL") // "ALL", "ACTIVE", "PENDING_DUES", "OFFICIALS"

    val hisabTabFilter = MutableStateFlow("ALL") // "ALL", "JAMA", "KHARCH"
    val hisabSearchQuery = MutableStateFlow("")

    val samanInventoryTypeFilter = MutableStateFlow("ALL") // "ALL", "LOAN", "DONATION", "EMERGENCY"
    val samanSearchQuery = MutableStateFlow("")

    // Notifications / Snackbars
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Dialogs
    val showAddEditMemberDialog = MutableStateFlow(false)
    val editingMember = MutableStateFlow<Member?>(null)

    val showAddEditChandaDialog = MutableStateFlow(false)
    val editingChanda = MutableStateFlow<ChandaRecord?>(null)
    val preselectedMemberForChanda = MutableStateFlow<Member?>(null)

    val showAddEditExpenseDialog = MutableStateFlow(false)
    val editingExpense = MutableStateFlow<ExpenseRecord?>(null)

    val showAddEditWelfareItemDialog = MutableStateFlow(false)
    val editingWelfareItem = MutableStateFlow<WelfareItem?>(null)

    val showUpdateStockDialog = MutableStateFlow(false)
    val stockItemToUpdate = MutableStateFlow<WelfareItem?>(null)

    val showStockHistoryDialog = MutableStateFlow(false)
    val selectedItemForHistory = MutableStateFlow<WelfareItem?>(null)

    val showIssueItemDialog = MutableStateFlow(false)
    val preselectedItemForIssue = MutableStateFlow<WelfareItem?>(null)

    val showReturnItemDialog = MutableStateFlow(false)
    val returningIssue = MutableStateFlow<ItemIssue?>(null)

    val showPinPromptDialog = MutableStateFlow(false)
    val showChangePinDialog = MutableStateFlow(false)

    // Data Streams from Repository
    val allMembers = repository.allMembers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allCollections = repository.allCollections.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allExpenses = repository.allExpenses.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allWelfareItems = repository.allWelfareItems.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allStockLogs = repository.allStockLogs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allItemIssues = repository.allItemIssues.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val activeItemIssues = repository.activeItemIssues.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Member Dues calculation combining members, collections, and active issues
    val membersWithDues: StateFlow<List<MemberDuesInfo>> = combine(
        allMembers,
        allCollections,
        activeItemIssues
    ) { members, collections, issues ->
        val currentMonthNumber = Calendar.getInstance().get(Calendar.MONTH) + 1 // 1-12
        members.map { member ->
            val memberCollections = collections.filter { it.memberId == member.id }
            val totalPaid = memberCollections.sumOf { it.amount }
            val expectedDues = member.monthlyChandaAmount * currentMonthNumber
            val pending = (expectedDues - totalPaid).coerceAtLeast(0.0)
            val lastPayment = memberCollections.maxByOrNull { it.paymentDate }?.paymentDate
            val activeBorrows = issues.count { it.memberId == member.id }

            MemberDuesInfo(
                member = member,
                totalPaid = totalPaid,
                expectedDues = expectedDues,
                pendingAmount = pending,
                hasPendingDues = pending > 0,
                lastPaymentDate = lastPayment,
                activeBorrowCount = activeBorrows
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Statistics
    val dashboardStats: StateFlow<DashboardStats> = combine(
        repository.totalCollectionAmount,
        repository.totalExpenseAmount,
        membersWithDues,
        allWelfareItems,
        activeItemIssues
    ) { collection, expense, membersDues, items, activeIssues ->
        val degItems = items.filter { it.category.contains("Deg", ignoreCase = true) }
        val chairItems = items.filter { it.category.contains("Chair", ignoreCase = true) }
        val plateItems = items.filter { it.category.contains("Plate", ignoreCase = true) || it.name.contains("Plate", ignoreCase = true) }

        val loanCount = items.filter { it.inventoryType == "Loan" }.sumOf { it.totalQuantity }
        val donationCount = items.filter { it.inventoryType == "Donation" }.sumOf { it.totalQuantity }
        val emergencyCount = items.filter { it.inventoryType == "Emergency" }.sumOf { it.totalQuantity }

        DashboardStats(
            totalCollection = collection,
            totalExpense = expense,
            baqayaBalance = collection - expense,
            totalMembers = membersDues.size,
            activeMembers = membersDues.count { it.member.isActive },
            pendingDuesMembersCount = membersDues.count { it.hasPendingDues },
            totalDegs = degItems.sumOf { it.totalQuantity },
            availableDegs = degItems.sumOf { it.availableQuantity },
            totalChairs = chairItems.sumOf { it.totalQuantity },
            availableChairs = chairItems.sumOf { it.availableQuantity },
            totalPlates = plateItems.sumOf { it.totalQuantity },
            availablePlates = plateItems.sumOf { it.availableQuantity },
            totalLoanItems = loanCount,
            totalDonationItems = donationCount,
            totalEmergencyItems = emergencyCount,
            activeIssuesCount = activeIssues.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectMember(memberId: Long) {
        _selectedMemberId.value = memberId
        navigateTo(AppScreen.MEMBER_DETAIL)
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
        showMessage("Logged in as ${role.displayName}")
    }

    fun attemptAdminLogin(enteredPin: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            if (repository.verifyAdminPin(enteredPin)) {
                _userRole.value = UserRole.ADMIN
                onSuccess()
                showMessage("Admin access granted (President / Treasurer)")
            } else {
                onError()
            }
        }
    }

    fun updatePin(currentPin: String, newPin: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (repository.verifyAdminPin(currentPin)) {
                if (newPin.length >= 4) {
                    repository.setAdminPin(newPin)
                    onSuccess()
                    showMessage("Admin PIN updated successfully")
                } else {
                    onError("PIN must be at least 4 digits")
                }
            } else {
                onError("Current PIN is incorrect")
            }
        }
    }

    // Member Operations
    fun saveMember(member: Member) {
        viewModelScope.launch {
            if (member.id == 0L) {
                repository.insertMember(member)
                showMessage("New member ${member.fullName} added successfully")
            } else {
                repository.updateMember(member)
                showMessage("Member ${member.fullName} updated")
            }
            showAddEditMemberDialog.value = false
            editingMember.value = null
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch {
            repository.deleteMember(member)
            showMessage("Member ${member.fullName} removed")
            if (_selectedMemberId.value == member.id) {
                _selectedMemberId.value = null
                _currentScreen.value = AppScreen.MEMBERS
            }
        }
    }

    // Chanda Operations
    fun saveChanda(record: ChandaRecord) {
        viewModelScope.launch {
            if (record.id == 0L) {
                repository.insertChanda(record)
                showMessage("Rs. ${record.amount.toInt()} Chanda collected from ${record.memberName}")
            } else {
                repository.updateChanda(record)
                showMessage("Chanda record updated")
            }
            showAddEditChandaDialog.value = false
            editingChanda.value = null
            preselectedMemberForChanda.value = null
        }
    }

    fun deleteChanda(record: ChandaRecord) {
        viewModelScope.launch {
            repository.deleteChanda(record)
            showMessage("Chanda entry deleted")
        }
    }

    // Expense Operations
    fun saveExpense(record: ExpenseRecord) {
        viewModelScope.launch {
            if (record.id == 0L) {
                repository.insertExpense(record)
                showMessage("Expense of Rs. ${record.amount.toInt()} recorded: ${record.title}")
            } else {
                repository.updateExpense(record)
                showMessage("Expense record updated")
            }
            showAddEditExpenseDialog.value = false
            editingExpense.value = null
        }
    }

    fun deleteExpense(record: ExpenseRecord) {
        viewModelScope.launch {
            repository.deleteExpense(record)
            showMessage("Expense entry deleted")
        }
    }

    // Welfare Item Operations
    fun saveWelfareItem(item: WelfareItem) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.insertWelfareItem(item)
                showMessage("Added ${item.name} (${item.inventoryType}) to inventory")
            } else {
                repository.updateWelfareItem(item)
                showMessage("Inventory item ${item.name} updated")
            }
            showAddEditWelfareItemDialog.value = false
            editingWelfareItem.value = null
        }
    }

    fun deleteWelfareItem(item: WelfareItem) {
        viewModelScope.launch {
            repository.deleteWelfareItem(item)
            showMessage("Deleted ${item.name} from inventory")
        }
    }

    // Stock Level Management (Add Stock or Adjust Stock)
    fun addStockLevel(
        item: WelfareItem,
        quantityToAdd: Int,
        changeType: String,
        reasonOrDonor: String
    ) {
        viewModelScope.launch {
            val success = repository.addStock(
                itemId = item.id,
                quantityToAdd = quantityToAdd,
                changeType = changeType,
                reasonOrDonor = reasonOrDonor
            )
            if (success) {
                showMessage("Added +$quantityToAdd ${item.unit} to ${item.name}. New total: ${item.totalQuantity + quantityToAdd}")
                showUpdateStockDialog.value = false
                stockItemToUpdate.value = null
            }
        }
    }

    fun adjustStockLevel(
        item: WelfareItem,
        newTotal: Int,
        newAvailable: Int,
        reason: String
    ) {
        viewModelScope.launch {
            val success = repository.setStockLevel(
                itemId = item.id,
                newTotal = newTotal,
                newAvailable = newAvailable,
                reason = reason
            )
            if (success) {
                showMessage("Stock level for ${item.name} updated to $newTotal ${item.unit}")
                showUpdateStockDialog.value = false
                stockItemToUpdate.value = null
            }
        }
    }

    // Item Issue & Return Operations
    fun issueItem(issue: ItemIssue) {
        viewModelScope.launch {
            repository.issueItem(issue)
            showMessage("Issued ${issue.quantity}x ${issue.itemName} to ${issue.borrowerName}")
            showIssueItemDialog.value = false
            preselectedItemForIssue.value = null
        }
    }

    fun returnItem(issue: ItemIssue, condition: String) {
        viewModelScope.launch {
            repository.returnItem(issue, condition)
            showMessage("Returned ${issue.quantity}x ${issue.itemName} from ${issue.borrowerName}")
            showReturnItemDialog.value = false
            returningIssue.value = null
        }
    }

    fun deleteIssue(issue: ItemIssue) {
        viewModelScope.launch {
            repository.deleteIssue(issue)
            showMessage("Issue record removed")
        }
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }
}
