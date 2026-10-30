package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberCode: String, // e.g. "WB-001"
    val fullName: String,
    val fatherName: String = "",
    val role: String = "Member", // "President", "Treasurer", "General Secretary", "Executive Member", "Member"
    val phone: String = "",
    val address: String = "",
    val monthlyChandaAmount: Double = 500.0,
    val joinDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val notes: String = ""
)

@Entity(tableName = "chanda_collections")
data class ChandaRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val amount: Double,
    val forMonth: String, // e.g. "October 2026"
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Cash", // "Cash", "EasyPaisa", "JazzCash", "Bank Transfer"
    val receiptNumber: String = "",
    val collectedBy: String = "Treasurer",
    val notes: String = ""
)

@Entity(tableName = "expenses")
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "Welfare Equipment", "Deg / Saman Maintenance", "Kafan Dafan / Funeral Aid", "Ration / Needy Aid", "Community Event", "Miscellaneous"
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val paidTo: String = "",
    val authorizedBy: String = "President / Treasurer",
    val receiptNote: String = ""
)

@Entity(tableName = "welfare_items")
data class WelfareItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // e.g. "Large Deg (15 Kg)", "Patient Wheelchair", "Family Ration Pack"
    val category: String, // "Degs & Cauldrons", "Plates & Crockery", "Chairs & Seating", "Medical & Patient Aid", "Ration & Food Aid", "Funeral / Kafan Dafan"
    val inventoryType: String = "Loan", // "Loan", "Donation", "Emergency"
    val totalQuantity: Int,
    val availableQuantity: Int,
    val unit: String = "Pieces", // "Pieces", "Sets", "Bags", "Units"
    val condition: String = "Good", // "Excellent", "Good", "Needs Repair"
    val storageLocation: String = "Society Markaz",
    val donorOrSource: String = "Society Welfare Purchase",
    val minStockThreshold: Int = 2,
    val lastStockUpdate: Long = System.currentTimeMillis()
)

@Entity(tableName = "stock_logs")
data class StockLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val itemName: String,
    val inventoryType: String = "Loan", // "Loan", "Donation", "Emergency"
    val changeType: String = "STOCK_ADDED", // "STOCK_ADDED", "STOCK_ADJUSTED", "DONATION_RECEIVED", "EMERGENCY_ALLOCATED", "DAMAGE_REMOVED"
    val quantityChange: Int, // e.g. +10, -2
    val previousTotal: Int,
    val newTotal: Int,
    val reasonOrDonor: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val updatedBy: String = "President / Treasurer"
)

@Entity(tableName = "item_issues")
data class ItemIssue(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val itemName: String,
    val memberId: Long? = null,
    val borrowerName: String,
    val borrowerPhone: String = "",
    val quantity: Int,
    val issueDate: Long = System.currentTimeMillis(),
    val expectedReturnDate: Long = System.currentTimeMillis() + 86400000L * 3, // 3 days
    val actualReturnDate: Long? = null,
    val purpose: String = "Shadi / Dawat", // "Shadi / Dawat", "Khatam-e-Quran", "Majlis / Niyaz", "Funeral / Kafan Dafan", "Medical Emergency"
    val status: String = "ISSUED", // "ISSUED", "RETURNED"
    val issueNotes: String = ""
)

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey
    val key: String,
    val value: String
)
