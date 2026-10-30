package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.model.ChandaRecord
import com.example.data.model.ExpenseRecord
import com.example.data.model.ItemIssue
import com.example.data.model.Member
import com.example.data.model.WelfareItem
import com.example.data.repository.WelfareRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@OptIn(ExperimentalCoroutinesApi::class)
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: WelfareRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = WelfareRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context matches Waliye Brothers`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Waliye Brothers", appName)
    }

    @Test
    fun `test member repository and dues calculation`() = runTest {
        // Insert test member
        val member = Member(
            memberCode = "WB-TEST",
            fullName = "Test Brother",
            role = "Member",
            monthlyChandaAmount = 500.0
        )
        val memberId = repository.insertMember(member)
        assertTrue(memberId > 0)

        // Insert test chanda collection
        val chanda = ChandaRecord(
            memberId = memberId,
            memberName = "Test Brother",
            amount = 1000.0,
            forMonth = "October 2026",
            receiptNumber = "REC-TEST"
        )
        val chandaId = repository.insertChanda(chanda)
        assertTrue(chandaId > 0)

        // Insert test expense
        val expense = ExpenseRecord(
            title = "Test Deg Repair",
            category = "Deg / Saman Maintenance",
            amount = 300.0
        )
        val expenseId = repository.insertExpense(expense)
        assertTrue(expenseId > 0)

        // Verify welfare items with Loan type
        val item = WelfareItem(
            name = "Test Deg (15 Kg)",
            category = "Degs & Cauldrons",
            inventoryType = "Loan",
            totalQuantity = 5,
            availableQuantity = 5
        )
        val itemId = repository.insertWelfareItem(item)

        // Test issue item
        val issue = ItemIssue(
            itemId = itemId,
            itemName = "Test Deg (15 Kg)",
            borrowerName = "Test Borrower",
            quantity = 2
        )
        repository.issueItem(issue)

        val updatedItems = repository.allWelfareItems.first()
        val savedItem = updatedItems.find { it.id == itemId }
        assertNotNull(savedItem)
        assertEquals(3, savedItem!!.availableQuantity)

        // Test return item
        repository.returnItem(issue.copy(id = 1), "Good")
        val returnedItems = repository.allWelfareItems.first()
        val restoredItem = returnedItems.find { it.id == itemId }
        assertNotNull(restoredItem)
        assertEquals(5, restoredItem!!.availableQuantity)
    }

    @Test
    fun `test managing Emergency, Donation, and Loan inventory and stock levels`() = runTest {
        // 1. Insert Loan item
        val loanItem = WelfareItem(
            name = "Banquet Chairs",
            category = "Chairs & Seating",
            inventoryType = "Loan",
            totalQuantity = 50,
            availableQuantity = 50,
            unit = "Chairs"
        )
        val loanId = repository.insertWelfareItem(loanItem)

        // 2. Insert Donation item
        val donationItem = WelfareItem(
            name = "Winter Warm Blankets",
            category = "Winter Relief",
            inventoryType = "Donation",
            totalQuantity = 20,
            availableQuantity = 20,
            unit = "Blankets",
            donorOrSource = "Donated by Brother Tariq"
        )
        val donationId = repository.insertWelfareItem(donationItem)

        // 3. Insert Emergency item
        val emergencyItem = WelfareItem(
            name = "Foldable Patient Wheelchair",
            category = "Medical & Patient Aid",
            inventoryType = "Emergency",
            totalQuantity = 2,
            availableQuantity = 2,
            unit = "Units",
            donorOrSource = "Emergency Welfare Reserve"
        )
        val emergencyId = repository.insertWelfareItem(emergencyItem)

        // Verify inventory types
        val loanList = repository.getItemsByType("Loan").first()
        val donationList = repository.getItemsByType("Donation").first()
        val emergencyList = repository.getItemsByType("Emergency").first()

        assertTrue(loanList.any { it.id == loanId })
        assertTrue(donationList.any { it.id == donationId })
        assertTrue(emergencyList.any { it.id == emergencyId })

        // 4. Test Adding Stock (+10 blankets donated)
        val addSuccess = repository.addStock(
            itemId = donationId,
            quantityToAdd = 10,
            changeType = "DONATION_RECEIVED",
            reasonOrDonor = "Additional 10 blankets donated by Haji Anwaruddin"
        )
        assertTrue(addSuccess)

        val updatedDonation = repository.allWelfareItems.first().find { it.id == donationId }
        assertNotNull(updatedDonation)
        assertEquals(30, updatedDonation!!.totalQuantity)
        assertEquals(30, updatedDonation.availableQuantity)

        // 5. Test Adjusting Stock Level (recout chairs to 48 total, 45 available)
        val adjustSuccess = repository.setStockLevel(
            itemId = loanId,
            newTotal = 48,
            newAvailable = 45,
            reason = "Stock audit - 2 chairs damaged"
        )
        assertTrue(adjustSuccess)

        val updatedLoan = repository.allWelfareItems.first().find { it.id == loanId }
        assertNotNull(updatedLoan)
        assertEquals(48, updatedLoan!!.totalQuantity)
        assertEquals(45, updatedLoan.availableQuantity)

        // 6. Verify Stock Logs were recorded
        val logs = repository.getStockLogsForItem(donationId).first()
        assertTrue(logs.isNotEmpty())
        assertEquals("DONATION_RECEIVED", logs.first().changeType)
        assertEquals(10, logs.first().quantityChange)
        assertEquals(30, logs.first().newTotal)
    }
}
