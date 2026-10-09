package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.StudentEntity
import com.example.ui.components.BadgeType
import com.example.ui.components.FeePaymentDialog
import com.example.ui.components.MetricCard
import com.example.ui.components.NewNoticeDialog
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ScholasticAmber
import com.example.ui.theme.ScholasticAmberLight
import com.example.ui.theme.SchoolBlueDark
import com.example.ui.theme.SchoolBlueLight
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenLight
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusPurpleLight
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedLight
import com.example.ui.viewmodel.SchoolViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: SchoolViewModel,
    onNavigateToAttendance: () -> Unit,
    onNavigateToFinanceFees: () -> Unit,
    onNavigateToFinanceSalaries: () -> Unit,
    onNavigateToCommunication: () -> Unit,
    onOpenChatThread: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()
    val chatThreads by viewModel.chatThreads.collectAsStateWithLifecycle()

    var showNoticeDialog by remember { mutableStateOf(false) }
    var feePaymentStudent by remember { mutableStateOf<StudentEntity?>(null) }

    val pendingStudents = remember(students) {
        students.filter { it.pendingFee > 0 }.take(4)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Executive Header Card
        item {
            ExecutiveHeaderCard(
                todayDate = viewModel.selectedDate.collectAsStateWithLifecycle().value,
                unreadCount = summary.unreadMessagesCount,
                onPostNotice = { showNoticeDialog = true }
            )
        }

        // Quick Action Grid Bar
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionButton(
                        label = "Attendance",
                        icon = Icons.Default.EventNote,
                        color = SchoolBluePrimary,
                        onClick = onNavigateToAttendance,
                        testTag = "quick_action_attendance"
                    )
                    QuickActionButton(
                        label = "Collect Fees",
                        icon = Icons.Default.MonetizationOn,
                        color = StatusGreen,
                        onClick = onNavigateToFinanceFees,
                        testTag = "quick_action_fees"
                    )
                    QuickActionButton(
                        label = "Salaries",
                        icon = Icons.Default.AccountBalance,
                        color = ScholasticAmber,
                        onClick = onNavigateToFinanceSalaries,
                        testTag = "quick_action_salaries"
                    )
                    QuickActionButton(
                        label = "Broadcast",
                        icon = Icons.Default.Campaign,
                        color = StatusPurple,
                        onClick = { showNoticeDialog = true },
                        testTag = "quick_action_broadcast"
                    )
                }
            }
        }

        // Core 4 Metrics KPI Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pending Fees KPI
                    MetricCard(
                        title = "Pending Fees",
                        value = "$${String.format(Locale.US, "%,.0f", summary.totalFeesPending)}",
                        subtitle = "${summary.studentsWithPendingFeesCount} students overdue",
                        icon = Icons.Default.Payment,
                        iconBgColor = StatusRedLight,
                        iconTint = StatusRed,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFinanceFees,
                        testTag = "metric_pending_fees"
                    )

                    // Teacher Salaries Pending KPI
                    MetricCard(
                        title = "Staff Salaries",
                        value = "$${String.format(Locale.US, "%,.0f", summary.totalSalariesPending)}",
                        subtitle = "${summary.teachersPendingSalaryCount} teachers pending",
                        icon = Icons.Default.AccountBalance,
                        iconBgColor = ScholasticAmberLight,
                        iconTint = ScholasticAmber,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFinanceSalaries,
                        testTag = "metric_pending_salaries"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Student Attendance KPI
                    MetricCard(
                        title = "Today's Attendance",
                        value = "${String.format(Locale.US, "%.1f", summary.todayAttendanceRate)}%",
                        subtitle = "${summary.todayAttendancePresent}/${summary.todayAttendanceTotal} students present",
                        icon = Icons.Default.People,
                        iconBgColor = StatusGreenLight,
                        iconTint = StatusGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAttendance,
                        testTag = "metric_attendance"
                    )

                    // Communication Hub KPI
                    MetricCard(
                        title = "Parent Messages",
                        value = "${chatThreads.size} Active",
                        subtitle = "${summary.unreadMessagesCount} unread responses",
                        icon = Icons.Default.Chat,
                        iconBgColor = StatusPurpleLight,
                        iconTint = StatusPurple,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCommunication,
                        testTag = "metric_messages"
                    )
                }
            }
        }

        // Financial Balance Overview Card
        item {
            FinancialHealthCard(
                collected = summary.totalFeesCollected,
                expected = summary.totalFeesExpected,
                pending = summary.totalFeesPending,
                salariesPaid = summary.totalSalariesDisbursed,
                salariesPending = summary.totalSalariesPending,
                onViewFullFinance = onNavigateToFinanceFees
            )
        }

        // Pending Fees Attention Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "Pending Student Fees",
                    subtitle = "Action required to recover outstanding dues",
                    actionText = "View All (${summary.studentsWithPendingFeesCount})",
                    onActionClick = onNavigateToFinanceFees
                )
            }
        }

        items(pendingStudents) { student ->
            PendingFeeRowCard(
                student = student,
                onRecordPayment = { feePaymentStudent = student },
                onSendReminder = { viewModel.sendFeeReminderToParent(student) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // School Circulars & Notices Preview
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "Recent Notices & Circulars",
                    subtitle = "Parent & faculty announcements",
                    actionText = "Manage Notices",
                    onActionClick = onNavigateToCommunication
                )
            }
        }

        items(notices.take(3)) { notice ->
            NoticePreviewCard(
                title = notice.title,
                content = notice.content,
                target = notice.targetAudience,
                category = notice.category,
                date = notice.date,
                isUrgent = notice.isUrgent,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Parent & Teacher Communication Quick Links
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "Parent-Teacher Conversations",
                    subtitle = "Effortless direct school communication",
                    actionText = "All Messages",
                    onActionClick = onNavigateToCommunication
                )
            }
        }

        items(chatThreads.take(3)) { thread ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onOpenChatThread(thread.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SchoolBluePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = SchoolBluePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${thread.parentName} (${thread.studentName})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = thread.lastMessageTime,
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400
                            )
                        }
                        Text(
                            text = "${thread.teacherName} • ${thread.subject}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = thread.lastMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    // New Notice Dialog
    if (showNoticeDialog) {
        NewNoticeDialog(
            onDismiss = { showNoticeDialog = false },
            onConfirm = { title, content, target, category, isUrgent ->
                viewModel.postNotice(title, content, target, category, isUrgent)
                showNoticeDialog = false
            }
        )
    }

    // Fee Payment Dialog
    feePaymentStudent?.let { stu ->
        FeePaymentDialog(
            student = stu,
            onDismiss = { feePaymentStudent = null },
            onConfirm = { amount, method, notes ->
                viewModel.recordFeePayment(stu, amount, method, notes)
                feePaymentStudent = null
            }
        )
    }
}

@Composable
private fun ExecutiveHeaderCard(
    todayDate: String,
    unreadCount: Int,
    onPostNotice: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(SchoolBlueDark, SchoolBluePrimary, Color(0xFF2563EB))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ACADEMIC YEAR 2026-2027",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "M.F.I. PUBLIC SCHOOL",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Director: Aakil Yusuf",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFFDE68A)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• Management Console",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = todayDate,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Real-time records synchronization active",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    OutlinedButton(
                        onClick = onPostNotice,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color.White, Color.White))),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Circular", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FinancialHealthCard(
    collected: Double,
    expected: Double,
    pending: Double,
    salariesPaid: Double,
    salariesPending: Double,
    onViewFullFinance: () -> Unit
) {
    val collectionProgress = if (expected > 0) (collected / expected).toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "School Financial Summary",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                TextButton(onClick = onViewFullFinance) {
                    Text(
                        text = "Full Ledger",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar of fee collection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tuition Collection Progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(collectionProgress * 100).toInt()}% Collected",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = StatusGreen
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { collectionProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = StatusGreen,
                trackColor = Slate200
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Slate200)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Collected",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%,.0f", collected)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusGreen
                    )
                }
                Column {
                    Text(
                        text = "Outstanding Fees",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%,.0f", pending)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusRed
                    )
                }
                Column {
                    Text(
                        text = "Faculty Payroll Paid",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%,.0f", salariesPaid)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = SchoolBluePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingFeeRowCard(
    student: StudentEntity,
    onRecordPayment: () -> Unit,
    onSendReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(student.avatarColorHex))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${student.grade} • Roll: ${student.rollNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format(Locale.US, "%,.0f", student.pendingFee)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusRed
                    )
                    StatusBadge(
                        text = "Due ${student.feeDueDate}",
                        type = BadgeType.DANGER
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSendReminder,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remind Parent", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onRecordPayment,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pay Now", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun NoticePreviewCard(
    title: String,
    content: String,
    target: String,
    category: String,
    date: String,
    isUrgent: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUrgent) StatusRedLight.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isUrgent) {
                        StatusBadge(text = "URGENT", type = BadgeType.DANGER)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    StatusBadge(text = category, type = BadgeType.INFO)
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(text = "For $target", type = BadgeType.NEUTRAL)
                }
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
