package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSetting
import com.example.data.model.ChandaRecord
import com.example.data.model.ExpenseRecord
import com.example.data.model.ItemIssue
import com.example.data.model.Member
import com.example.data.model.StockLog
import com.example.data.model.WelfareItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY id ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: Long): Member?

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    fun observeMemberById(id: Long): Flow<Member?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<Member>)

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("SELECT COUNT(*) FROM members")
    suspend fun getMemberCount(): Int
}

@Dao
interface ChandaDao {
    @Query("SELECT * FROM chanda_collections ORDER BY paymentDate DESC")
    fun getAllCollections(): Flow<List<ChandaRecord>>

    @Query("SELECT * FROM chanda_collections WHERE memberId = :memberId ORDER BY paymentDate DESC")
    fun getCollectionsByMember(memberId: Long): Flow<List<ChandaRecord>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM chanda_collections")
    fun getTotalCollectedAmount(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(record: ChandaRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(records: List<ChandaRecord>)

    @Update
    suspend fun updateCollection(record: ChandaRecord)

    @Delete
    suspend fun deleteCollection(record: ChandaRecord)

    @Query("DELETE FROM chanda_collections WHERE memberId = :memberId")
    suspend fun deleteCollectionsByMember(memberId: Long)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseRecord>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses")
    fun getTotalExpenseAmount(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(record: ExpenseRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(records: List<ExpenseRecord>)

    @Update
    suspend fun updateExpense(record: ExpenseRecord)

    @Delete
    suspend fun deleteExpense(record: ExpenseRecord)
}

@Dao
interface WelfareItemDao {
    @Query("SELECT * FROM welfare_items ORDER BY inventoryType ASC, category ASC, name ASC")
    fun getAllItems(): Flow<List<WelfareItem>>

    @Query("SELECT * FROM welfare_items WHERE inventoryType = :type ORDER BY category ASC, name ASC")
    fun getItemsByType(type: String): Flow<List<WelfareItem>>

    @Query("SELECT * FROM welfare_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): WelfareItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WelfareItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<WelfareItem>)

    @Update
    suspend fun updateItem(item: WelfareItem)

    @Delete
    suspend fun deleteItem(item: WelfareItem)

    @Query("UPDATE welfare_items SET totalQuantity = :newTotal, availableQuantity = :newAvailable, lastStockUpdate = :updateTime WHERE id = :itemId")
    suspend fun updateStock(itemId: Long, newTotal: Int, newAvailable: Int, updateTime: Long)
}

@Dao
interface StockLogDao {
    @Query("SELECT * FROM stock_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<StockLog>>

    @Query("SELECT * FROM stock_logs WHERE itemId = :itemId ORDER BY timestamp DESC")
    fun getLogsForItem(itemId: Long): Flow<List<StockLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: StockLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<StockLog>)
}

@Dao
interface ItemIssueDao {
    @Query("SELECT * FROM item_issues ORDER BY issueDate DESC")
    fun getAllIssues(): Flow<List<ItemIssue>>

    @Query("SELECT * FROM item_issues WHERE status = 'ISSUED' ORDER BY issueDate DESC")
    fun getActiveIssues(): Flow<List<ItemIssue>>

    @Query("SELECT * FROM item_issues WHERE memberId = :memberId ORDER BY issueDate DESC")
    fun getIssuesByMember(memberId: Long): Flow<List<ItemIssue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: ItemIssue): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssues(issues: List<ItemIssue>)

    @Update
    suspend fun updateIssue(issue: ItemIssue)

    @Delete
    suspend fun deleteIssue(issue: ItemIssue)
}

@Dao
interface SettingDao {
    @Query("SELECT value FROM app_settings WHERE key = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Query("SELECT value FROM app_settings WHERE key = :key LIMIT 1")
    fun observeSetting(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSetting)
}
