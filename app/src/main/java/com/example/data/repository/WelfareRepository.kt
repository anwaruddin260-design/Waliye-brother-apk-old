package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.AppSetting
import com.example.data.model.ChandaRecord
import com.example.data.model.ExpenseRecord
import com.example.data.model.ItemIssue
import com.example.data.model.Member
import com.example.data.model.StockLog
import com.example.data.model.WelfareItem
import kotlinx.coroutines.flow.Flow

class WelfareRepository(private val database: AppDatabase) {

    private val memberDao = database.memberDao()
    private val chandaDao = database.chandaDao()
    private val expenseDao = database.expenseDao()
    private val welfareItemDao = database.welfareItemDao()
    private val stockLogDao = database.stockLogDao()
    private val itemIssueDao = database.itemIssueDao()
    private val settingDao = database.settingDao()

    val allMembers: Flow<List<Member>> = memberDao.getAllMembers()
    val allCollections: Flow<List<ChandaRecord>> = chandaDao.getAllCollections()
    val allExpenses: Flow<List<ExpenseRecord>> = expenseDao.getAllExpenses()
    val allWelfareItems: Flow<List<WelfareItem>> = welfareItemDao.getAllItems()
    val allStockLogs: Flow<List<StockLog>> = stockLogDao.getAllLogs()
    val allItemIssues: Flow<List<ItemIssue>> = itemIssueDao.getAllIssues()
    val activeItemIssues: Flow<List<ItemIssue>> = itemIssueDao.getActiveIssues()

    val totalCollectionAmount: Flow<Double> = chandaDao.getTotalCollectedAmount()
    val totalExpenseAmount: Flow<Double> = expenseDao.getTotalExpenseAmount()

    fun getCollectionsByMember(memberId: Long): Flow<List<ChandaRecord>> =
        chandaDao.getCollectionsByMember(memberId)

    fun getIssuesByMember(memberId: Long): Flow<List<ItemIssue>> =
        itemIssueDao.getIssuesByMember(memberId)

    fun observeMemberById(id: Long): Flow<Member?> = memberDao.observeMemberById(id)

    fun getItemsByType(inventoryType: String): Flow<List<WelfareItem>> =
        welfareItemDao.getItemsByType(inventoryType)

    fun getStockLogsForItem(itemId: Long): Flow<List<StockLog>> =
        stockLogDao.getLogsForItem(itemId)

    // Member operations
    suspend fun insertMember(member: Member): Long = memberDao.insertMember(member)
    suspend fun updateMember(member: Member) = memberDao.updateMember(member)
    suspend fun deleteMember(member: Member) {
        memberDao.deleteMember(member)
        chandaDao.deleteCollectionsByMember(member.id)
    }

    // Chanda operations
    suspend fun insertChanda(record: ChandaRecord): Long = chandaDao.insertCollection(record)
    suspend fun updateChanda(record: ChandaRecord) = chandaDao.updateCollection(record)
    suspend fun deleteChanda(record: ChandaRecord) = chandaDao.deleteCollection(record)

    // Expense operations
    suspend fun insertExpense(record: ExpenseRecord): Long = expenseDao.insertExpense(record)
    suspend fun updateExpense(record: ExpenseRecord) = expenseDao.updateExpense(record)
    suspend fun deleteExpense(record: ExpenseRecord) = expenseDao.deleteExpense(record)

    // Welfare Item operations
    suspend fun insertWelfareItem(item: WelfareItem): Long {
        val id = welfareItemDao.insertItem(item)
        // Record initial stock creation in audit log
        stockLogDao.insertLog(
            StockLog(
                itemId = id,
                itemName = item.name,
                inventoryType = item.inventoryType,
                changeType = "STOCK_ADDED",
                quantityChange = item.totalQuantity,
                previousTotal = 0,
                newTotal = item.totalQuantity,
                reasonOrDonor = item.donorOrSource.ifBlank { "Initial stock registration" },
                timestamp = System.currentTimeMillis()
            )
        )
        return id
    }

    suspend fun updateWelfareItem(item: WelfareItem) = welfareItemDao.updateItem(item)
    suspend fun deleteWelfareItem(item: WelfareItem) = welfareItemDao.deleteItem(item)

    // Stock Management Operations (Adding stock, adjusting levels)
    suspend fun addStock(
        itemId: Long,
        quantityToAdd: Int,
        changeType: String = "STOCK_ADDED",
        reasonOrDonor: String = "",
        updatedBy: String = "President / Treasurer"
    ): Boolean {
        val item = welfareItemDao.getItemById(itemId) ?: return false
        val prevTotal = item.totalQuantity
        val newTotal = (prevTotal + quantityToAdd).coerceAtLeast(0)
        val newAvailable = (item.availableQuantity + quantityToAdd).coerceAtLeast(0)
        val now = System.currentTimeMillis()

        welfareItemDao.updateStock(itemId, newTotal, newAvailable, now)
        stockLogDao.insertLog(
            StockLog(
                itemId = itemId,
                itemName = item.name,
                inventoryType = item.inventoryType,
                changeType = changeType,
                quantityChange = quantityToAdd,
                previousTotal = prevTotal,
                newTotal = newTotal,
                reasonOrDonor = reasonOrDonor,
                timestamp = now,
                updatedBy = updatedBy
            )
        )
        return true
    }

    suspend fun setStockLevel(
        itemId: Long,
        newTotal: Int,
        newAvailable: Int,
        reason: String,
        updatedBy: String = "President / Treasurer"
    ): Boolean {
        val item = welfareItemDao.getItemById(itemId) ?: return false
        val prevTotal = item.totalQuantity
        val now = System.currentTimeMillis()

        welfareItemDao.updateStock(itemId, newTotal, newAvailable, now)
        stockLogDao.insertLog(
            StockLog(
                itemId = itemId,
                itemName = item.name,
                inventoryType = item.inventoryType,
                changeType = "STOCK_ADJUSTED",
                quantityChange = newTotal - prevTotal,
                previousTotal = prevTotal,
                newTotal = newTotal,
                reasonOrDonor = reason,
                timestamp = now,
                updatedBy = updatedBy
            )
        )
        return true
    }

    // Item Issue & Return operations
    suspend fun issueItem(issue: ItemIssue): Long {
        val item = welfareItemDao.getItemById(issue.itemId)
        if (item != null) {
            val newAvailable = (item.availableQuantity - issue.quantity).coerceAtLeast(0)
            welfareItemDao.updateItem(item.copy(availableQuantity = newAvailable))
        }
        return itemIssueDao.insertIssue(issue)
    }

    suspend fun returnItem(issue: ItemIssue, condition: String) {
        val updatedIssue = issue.copy(
            status = "RETURNED",
            actualReturnDate = System.currentTimeMillis()
        )
        itemIssueDao.updateIssue(updatedIssue)

        val item = welfareItemDao.getItemById(issue.itemId)
        if (item != null) {
            val newAvailable = (item.availableQuantity + issue.quantity).coerceAtMost(item.totalQuantity)
            welfareItemDao.updateItem(
                item.copy(
                    availableQuantity = newAvailable,
                    condition = if (condition.isNotBlank()) condition else item.condition
                )
            )
        }
    }

    suspend fun deleteIssue(issue: ItemIssue) {
        itemIssueDao.deleteIssue(issue)
    }

    // Settings & PIN
    suspend fun verifyAdminPin(pin: String): Boolean {
        val storedPin = settingDao.getSetting("admin_pin") ?: "1234"
        return pin.trim() == storedPin.trim()
    }

    suspend fun setAdminPin(newPin: String) {
        settingDao.setSetting(AppSetting("admin_pin", newPin))
    }

    fun observeSetting(key: String): Flow<String?> = settingDao.observeSetting(key)
    suspend fun setSetting(key: String, value: String) {
        settingDao.setSetting(AppSetting(key, value))
    }
}
