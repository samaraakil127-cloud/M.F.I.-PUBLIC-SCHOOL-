package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.CommunicationScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DirectoryScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.viewmodel.SchoolViewModel

enum class MainDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
    ATTENDANCE("Attendance", Icons.Filled.EventNote, Icons.Outlined.EventNote, "nav_attendance"),
    FINANCE("Finance", Icons.Filled.MonetizationOn, Icons.Outlined.MonetizationOn, "nav_finance"),
    COMMUNICATION("Connect", Icons.Filled.Chat, Icons.Outlined.Chat, "nav_communication"),
    DIRECTORY("Directory", Icons.Filled.FolderShared, Icons.Outlined.FolderShared, "nav_directory")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SchoolAppRoot()
            }
        }
    }
}

@Composable
fun SchoolAppRoot(viewModel: SchoolViewModel = viewModel()) {
    var currentDestination by remember { mutableStateOf(MainDestination.DASHBOARD) }
    var financeInitialTab by remember { mutableIntStateOf(0) }
    val activeThreadId by viewModel.activeChatThreadId.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (activeThreadId == null) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    MainDestination.entries.forEach { destination ->
                        val isSelected = currentDestination == destination

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (destination == MainDestination.FINANCE) {
                                    financeInitialTab = 0
                                }
                                currentDestination = destination
                            },
                            icon = {
                                if (destination == MainDestination.COMMUNICATION && summary.unreadMessagesCount > 0) {
                                    BadgedBox(badge = {
                                        Badge {
                                            Text(summary.unreadMessagesCount.toString())
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                            contentDescription = destination.title
                                        )
                                    }
                                } else if (destination == MainDestination.FINANCE && summary.studentsWithPendingFeesCount > 0) {
                                    BadgedBox(badge = {
                                        Badge {
                                            Text(summary.studentsWithPendingFeesCount.toString())
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                            contentDescription = destination.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = destination.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag(destination.testTag),
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SchoolBluePrimary,
                                selectedTextColor = SchoolBluePrimary,
                                indicatorColor = SchoolBluePrimary.copy(alpha = 0.12f)
                            )
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
            if (activeThreadId != null) {
                ChatDetailScreen(
                    threadId = activeThreadId!!,
                    viewModel = viewModel,
                    onBack = { viewModel.setActiveChatThread(null) }
                )
            } else {
                when (currentDestination) {
                    MainDestination.DASHBOARD -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToAttendance = { currentDestination = MainDestination.ATTENDANCE },
                            onNavigateToFinanceFees = {
                                financeInitialTab = 0
                                currentDestination = MainDestination.FINANCE
                            },
                            onNavigateToFinanceSalaries = {
                                financeInitialTab = 1
                                currentDestination = MainDestination.FINANCE
                            },
                            onNavigateToCommunication = { currentDestination = MainDestination.COMMUNICATION },
                            onOpenChatThread = { threadId ->
                                viewModel.setActiveChatThread(threadId)
                            }
                        )
                    }

                    MainDestination.ATTENDANCE -> {
                        AttendanceScreen(viewModel = viewModel)
                    }

                    MainDestination.FINANCE -> {
                        FinanceScreen(
                            viewModel = viewModel,
                            initialTab = financeInitialTab
                        )
                    }

                    MainDestination.COMMUNICATION -> {
                        CommunicationScreen(
                            viewModel = viewModel,
                            onOpenThread = { threadId ->
                                viewModel.setActiveChatThread(threadId)
                            }
                        )
                    }

                    MainDestination.DIRECTORY -> {
                        DirectoryScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
