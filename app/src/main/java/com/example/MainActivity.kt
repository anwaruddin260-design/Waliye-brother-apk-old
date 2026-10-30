package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddEditChandaDialog
import com.example.ui.screens.AddEditExpenseDialog
import com.example.ui.screens.AddEditMemberDialog
import com.example.ui.screens.AddEditWelfareItemDialog
import com.example.ui.screens.AdminPinPromptDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HisabKitabScreen
import com.example.ui.screens.IssueItemDialog
import com.example.ui.screens.MemberDetailScreen
import com.example.ui.screens.MemberManagementScreen
import com.example.ui.screens.ReturnItemDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StockHistoryDialog
import com.example.ui.screens.UpdateStockDialog
import com.example.ui.screens.WelfareSamanScreen
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold700
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.UserRole
import com.example.ui.viewmodel.WelfareViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WelfareApp()
            }
        }
    }
}

private data class NavItem(
    val screen: AppScreen,
    val title: String,
    val titleUrdu: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelfareApp(
    viewModel: WelfareViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val role by viewModel.userRole.collectAsStateWithLifecycle()
    val selectedMemberId by viewModel.selectedMemberId.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    // Dialog state collectors
    val showPinPrompt by viewModel.showPinPromptDialog.collectAsStateWithLifecycle()
    val showAddMember by viewModel.showAddEditMemberDialog.collectAsStateWithLifecycle()
    val editingMember by viewModel.editingMember.collectAsStateWithLifecycle()

    val showAddChanda by viewModel.showAddEditChandaDialog.collectAsStateWithLifecycle()
    val editingChanda by viewModel.editingChanda.collectAsStateWithLifecycle()
    val preselectedMemberForChanda by viewModel.preselectedMemberForChanda.collectAsStateWithLifecycle()

    val showAddExpense by viewModel.showAddEditExpenseDialog.collectAsStateWithLifecycle()
    val editingExpense by viewModel.editingExpense.collectAsStateWithLifecycle()

    val showAddWelfareItem by viewModel.showAddEditWelfareItemDialog.collectAsStateWithLifecycle()
    val editingWelfareItem by viewModel.editingWelfareItem.collectAsStateWithLifecycle()

    val showIssueItem by viewModel.showIssueItemDialog.collectAsStateWithLifecycle()
    val preselectedItemForIssue by viewModel.preselectedItemForIssue.collectAsStateWithLifecycle()

    val showReturnItem by viewModel.showReturnItemDialog.collectAsStateWithLifecycle()
    val returningIssue by viewModel.returningIssue.collectAsStateWithLifecycle()

    val showUpdateStock by viewModel.showUpdateStockDialog.collectAsStateWithLifecycle()
    val stockItemToUpdate by viewModel.stockItemToUpdate.collectAsStateWithLifecycle()

    val showStockHistory by viewModel.showStockHistoryDialog.collectAsStateWithLifecycle()
    val selectedItemForHistory by viewModel.selectedItemForHistory.collectAsStateWithLifecycle()
    val allStockLogs by viewModel.allStockLogs.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessage()
        }
    }

    val navItems = listOf(
        NavItem(AppScreen.DASHBOARD, "Dashboard", "ڈیش بورڈ", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
        NavItem(AppScreen.MEMBERS, "Members", "اراکین", Icons.Filled.Groups, Icons.Outlined.Groups, "nav_members"),
        NavItem(AppScreen.HISAB_KITAB, "Hisab", "حساب کتاب", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "nav_hisab"),
        NavItem(AppScreen.WELFARE_SAMAN, "Saman", "سامان", Icons.Filled.SoupKitchen, Icons.Outlined.SoupKitchen, "nav_saman"),
        NavItem(AppScreen.SETTINGS, "Settings", "سیٹنگز", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
    )

    // Handle back button on secondary screens
    if (currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.DASHBOARD) {
        BackHandler {
            if (currentScreen == AppScreen.MEMBER_DETAIL) {
                viewModel.navigateTo(AppScreen.MEMBERS)
            } else {
                viewModel.navigateTo(AppScreen.DASHBOARD)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen != AppScreen.SPLASH) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                shape = CircleShape
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_app_logo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Waliye Brothers",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Welfare Society • کراچی",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Gold100,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    },
                    actions = {
                        Surface(
                            color = if (role.isAdmin) Gold500 else Emerald900,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable {
                                    if (role.isAdmin) {
                                        viewModel.setUserRole(UserRole.MEMBER)
                                    } else {
                                        viewModel.showPinPromptDialog.value = true
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (role.isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = if (role.isAdmin) Emerald900 else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (role.isAdmin) "Admin" else "Member",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (role.isAdmin) Emerald900 else Color.White
                                    )
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Emerald800
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.SPLASH) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        val selected = currentScreen == item.screen ||
                                (item.screen == AppScreen.MEMBERS && currentScreen == AppScreen.MEMBER_DETAIL)

                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Emerald800,
                                selectedTextColor = Emerald800,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.SPLASH -> SplashScreen(viewModel = viewModel)
                AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppScreen.MEMBERS -> MemberManagementScreen(viewModel = viewModel)
                AppScreen.MEMBER_DETAIL -> {
                    if (selectedMemberId != null) {
                        MemberDetailScreen(memberId = selectedMemberId!!, viewModel = viewModel)
                    } else {
                        MemberManagementScreen(viewModel = viewModel)
                    }
                }
                AppScreen.HISAB_KITAB -> HisabKitabScreen(viewModel = viewModel)
                AppScreen.WELFARE_SAMAN -> WelfareSamanScreen(viewModel = viewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    // Interactive Dialogs
    if (showPinPrompt) {
        AdminPinPromptDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showPinPromptDialog.value = false }
        )
    }

    if (showAddMember) {
        AddEditMemberDialog(
            viewModel = viewModel,
            memberToEdit = editingMember,
            onDismiss = {
                viewModel.showAddEditMemberDialog.value = false
                viewModel.editingMember.value = null
            }
        )
    }

    if (showAddChanda) {
        AddEditChandaDialog(
            viewModel = viewModel,
            chandaToEdit = editingChanda,
            preselectedMember = preselectedMemberForChanda,
            onDismiss = {
                viewModel.showAddEditChandaDialog.value = false
                viewModel.editingChanda.value = null
                viewModel.preselectedMemberForChanda.value = null
            }
        )
    }

    if (showAddExpense) {
        AddEditExpenseDialog(
            viewModel = viewModel,
            expenseToEdit = editingExpense,
            onDismiss = {
                viewModel.showAddEditExpenseDialog.value = false
                viewModel.editingExpense.value = null
            }
        )
    }

    if (showAddWelfareItem) {
        AddEditWelfareItemDialog(
            viewModel = viewModel,
            itemToEdit = editingWelfareItem,
            onDismiss = {
                viewModel.showAddEditWelfareItemDialog.value = false
                viewModel.editingWelfareItem.value = null
            }
        )
    }

    if (showIssueItem) {
        IssueItemDialog(
            viewModel = viewModel,
            preselectedItem = preselectedItemForIssue,
            onDismiss = {
                viewModel.showIssueItemDialog.value = false
                viewModel.preselectedItemForIssue.value = null
            }
        )
    }

    if (showReturnItem && returningIssue != null) {
        ReturnItemDialog(
            viewModel = viewModel,
            issue = returningIssue!!,
            onDismiss = {
                viewModel.showReturnItemDialog.value = false
                viewModel.returningIssue.value = null
            }
        )
    }

    if (showUpdateStock && stockItemToUpdate != null) {
        UpdateStockDialog(
            viewModel = viewModel,
            item = stockItemToUpdate!!,
            onDismiss = {
                viewModel.showUpdateStockDialog.value = false
                viewModel.stockItemToUpdate.value = null
            }
        )
    }

    if (showStockHistory && selectedItemForHistory != null) {
        val itemLogs = allStockLogs.filter { it.itemId == selectedItemForHistory!!.id }
        StockHistoryDialog(
            item = selectedItemForHistory!!,
            logs = itemLogs,
            onDismiss = {
                viewModel.showStockHistoryDialog.value = false
                viewModel.selectedItemForHistory.value = null
            }
        )
    }
}
