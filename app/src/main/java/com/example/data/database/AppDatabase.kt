package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ChandaDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.ItemIssueDao
import com.example.data.dao.MemberDao
import com.example.data.dao.SettingDao
import com.example.data.dao.StockLogDao
import com.example.data.dao.WelfareItemDao
import com.example.data.model.AppSetting
import com.example.data.model.ChandaRecord
import com.example.data.model.ExpenseRecord
import com.example.data.model.ItemIssue
import com.example.data.model.Member
import com.example.data.model.StockLog
import com.example.data.model.WelfareItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Member::class,
        ChandaRecord::class,
        ExpenseRecord::class,
        WelfareItem::class,
        StockLog::class,
        ItemIssue::class,
        AppSetting::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun chandaDao(): ChandaDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun welfareItemDao(): WelfareItemDao
    abstract fun stockLogDao(): StockLogDao
    abstract fun itemIssueDao(): ItemIssueDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waliye_brothers_welfare.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Seed Settings
            val settingDao = database.settingDao()
            settingDao.setSetting(AppSetting("admin_pin", "1234"))
            settingDao.setSetting(AppSetting("society_name", "Waliye Brothers Welfare Society"))
            settingDao.setSetting(AppSetting("president_name", "Haji Muhammad Anwaruddin"))
            settingDao.setSetting(AppSetting("treasurer_name", "Tariq Wali"))

            // Seed Members
            val memberDao = database.memberDao()
            val now = System.currentTimeMillis()
            val thirtyDays = 30L * 24 * 60 * 60 * 1000
            val sixtyDays = 60L * 24 * 60 * 60 * 1000

            val members = listOf(
                Member(1, "WB-001", "Haji Muhammad Anwaruddin", "Wali Muhammad", "President", "+92 300 2345678", "Wali Manzil, Sector 4", 1000.0, now - (180L * 86400000L), true, "Founding Member & President"),
                Member(2, "WB-002", "Tariq Wali", "Wali Muhammad", "Treasurer", "+92 321 8765432", "House 45, Street 9", 1000.0, now - (180L * 86400000L), true, "Khazanchi / Society Treasurer"),
                Member(3, "WB-003", "Zubair Khan", "Ghulam Nabi Khan", "General Secretary", "+92 333 1122334", "Plot 12-B, Main Market", 1000.0, now - (150L * 86400000L), true, "Secretary & Coordination"),
                Member(4, "WB-004", "Bilal Ahmed", "Bashir Ahmed", "Executive Member", "+92 345 5566778", "Flat 302, Noor Plaza", 500.0, now - (120L * 86400000L), true, "Welfare Item Incharge"),
                Member(5, "WB-005", "Farhan Qureshi", "Abdul Sattar Qureshi", "Executive Member", "+92 312 9988776", "House 77, Street 3", 500.0, now - (120L * 86400000L), true, "Active community worker"),
                Member(6, "WB-006", "Sajid Ali", "Liaquat Ali", "Member", "+92 301 4455667", "Sector 5, Gali 2", 500.0, now - (90L * 86400000L), true, "Chanda paid regular"),
                Member(7, "WB-007", "Usman Ghani", "Hafiz Abdul Ghaffar", "Member", "+92 306 7788990", "House 18, Block C", 500.0, now - (90L * 86400000L), true, "Active participant"),
                Member(8, "WB-008", "Rashid Minhas", "Muhammad Yaqoob", "Member", "+92 332 3344556", "Street 11, Green Town", 500.0, now - (60L * 86400000L), true, "Pending dues current month"),
                Member(9, "WB-009", "Imran Sheikh", "Sheikh Mushtaq", "Member", "+92 315 2233445", "House 204, Sector 7", 500.0, now - (60L * 86400000L), true, "Pending dues current month"),
                Member(10, "WB-010", "Kamran Wali", "Wali Muhammad", "Member", "+92 322 8899001", "Wali Manzil, Sector 4", 500.0, now - (30L * 86400000L), true, "New member")
            )
            memberDao.insertMembers(members)

            // Seed Chanda Collections (Jama Shuda)
            val chandaDao = database.chandaDao()
            val collections = listOf(
                ChandaRecord(1, 1, "Haji Muhammad Anwaruddin", 1000.0, "October 2026", now - (5L * 86400000L), "Cash", "REC-101", "Tariq Wali", "Monthly Chanda"),
                ChandaRecord(2, 2, "Tariq Wali", 1000.0, "October 2026", now - (5L * 86400000L), "Cash", "REC-102", "Self / President", "Monthly Chanda"),
                ChandaRecord(3, 3, "Zubair Khan", 1000.0, "October 2026", now - (6L * 86400000L), "Bank Transfer", "REC-103", "Tariq Wali", "Online transfer"),
                ChandaRecord(4, 4, "Bilal Ahmed", 500.0, "October 2026", now - (7L * 86400000L), "EasyPaisa", "REC-104", "Tariq Wali", "Monthly Chanda"),
                ChandaRecord(5, 5, "Farhan Qureshi", 500.0, "October 2026", now - (7L * 86400000L), "Cash", "REC-105", "Tariq Wali", "Monthly Chanda"),
                ChandaRecord(6, 6, "Sajid Ali", 500.0, "October 2026", now - (8L * 86400000L), "JazzCash", "REC-106", "Tariq Wali", "Monthly Chanda"),
                ChandaRecord(7, 7, "Usman Ghani", 500.0, "October 2026", now - (9L * 86400000L), "Cash", "REC-107", "Tariq Wali", "Monthly Chanda"),
                ChandaRecord(8, 1, "Haji Muhammad Anwaruddin", 1000.0, "September 2026", now - thirtyDays - (2L * 86400000L), "Cash", "REC-081", "Tariq Wali", "September Chanda"),
                ChandaRecord(9, 2, "Tariq Wali", 1000.0, "September 2026", now - thirtyDays - (2L * 86400000L), "Cash", "REC-082", "President", "September Chanda"),
                ChandaRecord(10, 3, "Zubair Khan", 1000.0, "September 2026", now - thirtyDays - (3L * 86400000L), "Bank", "REC-083", "Tariq Wali", "September Chanda"),
                ChandaRecord(11, 4, "Bilal Ahmed", 500.0, "September 2026", now - thirtyDays - (4L * 86400000L), "Cash", "REC-084", "Tariq Wali", "September Chanda"),
                ChandaRecord(12, 5, "Farhan Qureshi", 500.0, "September 2026", now - thirtyDays - (5L * 86400000L), "Cash", "REC-085", "Tariq Wali", "September Chanda"),
                ChandaRecord(13, 6, "Sajid Ali", 500.0, "September 2026", now - thirtyDays - (5L * 86400000L), "Cash", "REC-086", "Tariq Wali", "September Chanda"),
                ChandaRecord(14, 7, "Usman Ghani", 500.0, "September 2026", now - thirtyDays - (6L * 86400000L), "Cash", "REC-087", "Tariq Wali", "September Chanda"),
                ChandaRecord(15, 8, "Rashid Minhas", 500.0, "September 2026", now - thirtyDays - (7L * 86400000L), "Cash", "REC-088", "Tariq Wali", "September Chanda"),
                ChandaRecord(16, 9, "Imran Sheikh", 500.0, "September 2026", now - thirtyDays - (8L * 86400000L), "Cash", "REC-089", "Tariq Wali", "September Chanda"),
                ChandaRecord(17, 1, "Haji Muhammad Anwaruddin", 25000.0, "Special Welfare Fund", now - sixtyDays, "Cash", "REC-SP01", "Tariq Wali", "Special Contribution for Purchasing Welfare Chairs & Degs"),
                ChandaRecord(18, 2, "Tariq Wali", 15000.0, "Special Welfare Fund", now - sixtyDays, "Cash", "REC-SP02", "President", "Special Contribution for Welfare Saman Setup")
            )
            chandaDao.insertCollections(collections)

            // Seed Expenses (Kharch)
            val expenseDao = database.expenseDao()
            val expenses = listOf(
                ExpenseRecord(1, "Purchased 150 Heavy Duty Banquet Chairs", "Welfare Equipment", 24000.0, now - (45L * 86400000L), "Madina Furniture Market", "President Anwaruddin", "Receipt #8821"),
                ExpenseRecord(2, "Purchased 8 Large Daigs (Degs) with Lids", "Welfare Equipment", 16000.0, now - (40L * 86400000L), "Gujranwala Utensil Mart", "President & Treasurer", "Voucher #441"),
                ExpenseRecord(3, "Purchased 300 Stainless Steel Plates & Glasses", "Welfare Equipment", 7500.0, now - (35L * 86400000L), "Bismillah Crockery", "Treasurer Tariq", "Receipt #102"),
                ExpenseRecord(4, "Tin Coating (Qalai) & Deg Repairing", "Deg / Saman Maintenance", 3200.0, now - (15L * 86400000L), "Ustad Aslam Qalaigar", "Bilal Ahmed", "Cash paid to workshop"),
                ExpenseRecord(5, "Kafan Dafan & Funeral Aid (Needy Family)", "Kafan Dafan / Funeral Aid", 4000.0, now - (20L * 86400000L), "Late Br. Siddique Family", "President Anwaruddin", "Committee Approval #12"),
                ExpenseRecord(6, "Monthly Ration Package for 2 Needy Families", "Ration / Needy Aid", 6000.0, now - (10L * 86400000L), "Al-Falah Cash & Carry", "Treasurer Tariq", "Ration Distribution Drive")
            )
            expenseDao.insertExpenses(expenses)

            // Seed Welfare Items (Categorized as Loan, Donation, Emergency)
            val welfareItemDao = database.welfareItemDao()
            val items = listOf(
                // 1. LOAN ITEMS (Returnable lending equipment)
                WelfareItem(
                    id = 1,
                    name = "Large Daig / Deg (15 Kg Biryani/Pulao)",
                    category = "Degs & Cauldrons",
                    inventoryType = "Loan",
                    totalQuantity = 8,
                    availableQuantity = 6,
                    unit = "Degs",
                    condition = "Good",
                    storageLocation = "Society Markaz / Godown",
                    donorOrSource = "Society Fund Purchase",
                    minStockThreshold = 2,
                    lastStockUpdate = now - (10L * 86400000L)
                ),
                WelfareItem(
                    id = 2,
                    name = "Medium Daig / Deg (10 Kg Capacity)",
                    category = "Degs & Cauldrons",
                    inventoryType = "Loan",
                    totalQuantity = 6,
                    availableQuantity = 6,
                    unit = "Degs",
                    condition = "Excellent",
                    storageLocation = "Society Markaz",
                    donorOrSource = "Donated by Anwaruddin",
                    minStockThreshold = 2,
                    lastStockUpdate = now - (15L * 86400000L)
                ),
                WelfareItem(
                    id = 3,
                    name = "Heavy Stainless Steel Plates (Thali)",
                    category = "Plates & Crockery",
                    inventoryType = "Loan",
                    totalQuantity = 300,
                    availableQuantity = 200,
                    unit = "Plates",
                    condition = "Good",
                    storageLocation = "Box 1 & 2 at Godown",
                    donorOrSource = "Society Crockery Purchase",
                    minStockThreshold = 50,
                    lastStockUpdate = now - (12L * 86400000L)
                ),
                WelfareItem(
                    id = 4,
                    name = "Banquet Heavy Plastic Chairs",
                    category = "Chairs & Seating",
                    inventoryType = "Loan",
                    totalQuantity = 150,
                    availableQuantity = 100,
                    unit = "Chairs",
                    condition = "Good",
                    storageLocation = "Main Markaz Hall",
                    donorOrSource = "Society Furniture Drive",
                    minStockThreshold = 30,
                    lastStockUpdate = now - (8L * 86400000L)
                ),
                WelfareItem(
                    id = 5,
                    name = "High-Pressure Gas Bhatti / Burner",
                    category = "Gas Burners / Bhatti",
                    inventoryType = "Loan",
                    totalQuantity = 4,
                    availableQuantity = 3,
                    unit = "Burners",
                    condition = "Good",
                    storageLocation = "Gas Appliance Store",
                    donorOrSource = "Donated by Tariq Wali",
                    minStockThreshold = 1,
                    lastStockUpdate = now - (14L * 86400000L)
                ),
                WelfareItem(
                    id = 6,
                    name = "Large Deg Kafgeer & Serving Spoons",
                    category = "Spoons & Cooking Tools",
                    inventoryType = "Loan",
                    totalQuantity = 8,
                    availableQuantity = 6,
                    unit = "Pieces",
                    condition = "Good",
                    storageLocation = "Utensil Locker",
                    donorOrSource = "Society Kitchen Kit",
                    minStockThreshold = 2,
                    lastStockUpdate = now - (5L * 86400000L)
                ),

                // 2. DONATION ITEMS (Items received for charity distribution or donated goods)
                WelfareItem(
                    id = 7,
                    name = "Monthly Family Ration Packages (Flour, Ghee, Rice, Dal)",
                    category = "Ration & Food Aid",
                    inventoryType = "Donation",
                    totalQuantity = 15,
                    availableQuantity = 10,
                    unit = "Bags",
                    condition = "New",
                    storageLocation = "Markaz Store Room",
                    donorOrSource = "Ramadan Welfare Donations",
                    minStockThreshold = 3,
                    lastStockUpdate = now - (4L * 86400000L)
                ),
                WelfareItem(
                    id = 8,
                    name = "Winter Warm Quilts & Blankets",
                    category = "Winter Relief",
                    inventoryType = "Donation",
                    totalQuantity = 25,
                    availableQuantity = 20,
                    unit = "Blankets",
                    condition = "New",
                    storageLocation = "Shelf A - Markaz",
                    donorOrSource = "Donated by Haji Brothers",
                    minStockThreshold = 5,
                    lastStockUpdate = now - (6L * 86400000L)
                ),
                WelfareItem(
                    id = 9,
                    name = "Heavy Duty Sewing Machines (For Widows / Deserving Families)",
                    category = "Livelihood Aid",
                    inventoryType = "Donation",
                    totalQuantity = 4,
                    availableQuantity = 3,
                    unit = "Machines",
                    condition = "New",
                    storageLocation = "Office Locker",
                    donorOrSource = "Zakat & Sadaqah Fund",
                    minStockThreshold = 1,
                    lastStockUpdate = now - (2L * 86400000L)
                ),

                // 3. EMERGENCY ITEMS (Urgent relief, patient medical equipment, funeral kits)
                WelfareItem(
                    id = 10,
                    name = "Foldable Patient Wheelchair (for sick/elderly)",
                    category = "Medical & Patient Aid",
                    inventoryType = "Emergency",
                    totalQuantity = 3,
                    availableQuantity = 2,
                    unit = "Units",
                    condition = "Excellent",
                    storageLocation = "Urgent Dispensary Corner",
                    donorOrSource = "Donated by Late Br. Siddique Family",
                    minStockThreshold = 1,
                    lastStockUpdate = now - (7L * 86400000L)
                ),
                WelfareItem(
                    id = 11,
                    name = "Medical Nebulizer Machine & Breathing Kit",
                    category = "Medical & Patient Aid",
                    inventoryType = "Emergency",
                    totalQuantity = 2,
                    availableQuantity = 2,
                    unit = "Sets",
                    condition = "Excellent",
                    storageLocation = "First Aid Cabinet",
                    donorOrSource = "Emergency Health Fund",
                    minStockThreshold = 1,
                    lastStockUpdate = now - (10L * 86400000L)
                ),
                WelfareItem(
                    id = 12,
                    name = "Emergency Kafan Dafan Kit (Full Shroud & Washing Kit)",
                    category = "Funeral / Kafan Dafan",
                    inventoryType = "Emergency",
                    totalQuantity = 6,
                    availableQuantity = 5,
                    unit = "Kits",
                    condition = "New",
                    storageLocation = "Kafan Locker Markaz",
                    donorOrSource = "Permanent Funeral Aid Reserve",
                    minStockThreshold = 2,
                    lastStockUpdate = now - (3L * 86400000L)
                )
            )
            welfareItemDao.insertItems(items)

            // Seed Initial Stock Logs for audit traceability
            val stockLogDao = database.stockLogDao()
            val initialLogs = listOf(
                StockLog(
                    itemId = 1,
                    itemName = "Large Daig / Deg (15 Kg Biryani/Pulao)",
                    inventoryType = "Loan",
                    changeType = "STOCK_ADDED",
                    quantityChange = 8,
                    previousTotal = 0,
                    newTotal = 8,
                    reasonOrDonor = "Initial purchase for welfare lending pool",
                    timestamp = now - (40L * 86400000L),
                    updatedBy = "President Anwaruddin"
                ),
                StockLog(
                    itemId = 4,
                    itemName = "Banquet Heavy Plastic Chairs",
                    inventoryType = "Loan",
                    changeType = "STOCK_ADDED",
                    quantityChange = 150,
                    previousTotal = 0,
                    newTotal = 150,
                    reasonOrDonor = "Purchased from Madina Furniture",
                    timestamp = now - (45L * 86400000L),
                    updatedBy = "Treasurer Tariq Wali"
                ),
                StockLog(
                    itemId = 7,
                    itemName = "Monthly Family Ration Packages",
                    inventoryType = "Donation",
                    changeType = "DONATION_RECEIVED",
                    quantityChange = 15,
                    previousTotal = 0,
                    newTotal = 15,
                    reasonOrDonor = "Donated by Overseas Welfare Well-wishers",
                    timestamp = now - (15L * 86400000L),
                    updatedBy = "President Anwaruddin"
                ),
                StockLog(
                    itemId = 10,
                    itemName = "Foldable Patient Wheelchair",
                    inventoryType = "Emergency",
                    changeType = "STOCK_ADDED",
                    quantityChange = 3,
                    previousTotal = 0,
                    newTotal = 3,
                    reasonOrDonor = "Allocated to emergency medical relief reserve",
                    timestamp = now - (20L * 86400000L),
                    updatedBy = "Treasurer Tariq Wali"
                )
            )
            stockLogDao.insertLogs(initialLogs)

            // Seed Active Issues
            val itemIssueDao = database.itemIssueDao()
            val issues = listOf(
                ItemIssue(
                    id = 1,
                    itemId = 1,
                    itemName = "Large Daig / Deg (15 Kg Biryani/Pulao)",
                    memberId = 4,
                    borrowerName = "Bilal Ahmed",
                    borrowerPhone = "+92 345 5566778",
                    quantity = 2,
                    issueDate = now - (1L * 86400000L),
                    expectedReturnDate = now + (2L * 86400000L),
                    actualReturnDate = null,
                    purpose = "Khatam-e-Quran & Family Niyaz",
                    status = "ISSUED",
                    issueNotes = "Issued with 2 large Kafgeer spoons and lids"
                ),
                ItemIssue(
                    id = 2,
                    itemId = 4,
                    itemName = "Banquet Heavy Plastic Chairs",
                    memberId = 4,
                    borrowerName = "Bilal Ahmed",
                    borrowerPhone = "+92 345 5566778",
                    quantity = 50,
                    issueDate = now - (1L * 86400000L),
                    expectedReturnDate = now + (2L * 86400000L),
                    actualReturnDate = null,
                    purpose = "Khatam-e-Quran & Family Niyaz",
                    status = "ISSUED",
                    issueNotes = "Issued for 3 days"
                ),
                ItemIssue(
                    id = 3,
                    itemId = 3,
                    itemName = "Heavy Stainless Steel Plates (Thali)",
                    memberId = 5,
                    borrowerName = "Farhan Qureshi",
                    borrowerPhone = "+92 312 9988776",
                    quantity = 100,
                    issueDate = now - (2L * 86400000L),
                    expectedReturnDate = now + (1L * 86400000L),
                    actualReturnDate = null,
                    purpose = "Shadi / Wedding Dawat",
                    status = "ISSUED",
                    issueNotes = "Clean condition checked before issue"
                ),
                ItemIssue(
                    id = 4,
                    itemId = 10,
                    itemName = "Foldable Patient Wheelchair (for sick/elderly)",
                    memberId = 8,
                    borrowerName = "Rashid Minhas (Elder Father)",
                    borrowerPhone = "+92 332 3344556",
                    quantity = 1,
                    issueDate = now - (3L * 86400000L),
                    expectedReturnDate = now + (10L * 86400000L),
                    actualReturnDate = null,
                    purpose = "Medical Emergency / Patient Mobility",
                    status = "ISSUED",
                    issueNotes = "Urgent loan for hospital visits"
                )
            )
            itemIssueDao.insertIssues(issues)
        }
    }
}
