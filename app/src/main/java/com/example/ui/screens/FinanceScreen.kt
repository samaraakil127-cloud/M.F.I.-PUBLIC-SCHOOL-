package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.FeeTransactionEntity
import com.example.data.entity.StudentEntity
import com.example.data.entity.TeacherEntity
import com.example.ui.components.AddTeacherDialog
import com.example.ui.components.BadgeType
import com.example.ui.components.CustomSearchBar
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.FeePaymentDialog
import com.example.ui.components.FeeReceiptPreviewDialog
import com.example.ui.components.MetricCard
import com.example.ui.components.SalaryDisbursementDialog
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ScholasticAmber
import com.example.ui.theme.ScholasticAmberLight
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenLight
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedLight
import com.example.ui.viewmodel.SchoolViewModel
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: SchoolViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val students by viewModel.students.collectAsStateWithLifecycle()
    val teachers by viewModel.teachers.collectAsStateWithLifecycle()
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val feeTransactions by viewModel.feeTransactions.collectAsStateWithLifecycle()
    val salaryPayments by viewModel.salaryPayments.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFeeFilter by remember { mutableStateOf("Pending Only") }
    var selectedSalaryFilter by remember { mutableStateOf("All") }

    // Dialog states
    var feePaymentStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var salaryTeacher by remember { mutableStateOf<TeacherEntity?>(null) }
    var viewingReceipt by remember { mutableStateOf<FeeTransactionEntity?>(null) }
    var showAddTeacherDialog by remember { mutableStateOf(false) }

    val filteredStudents = remember(students, searchQuery, selectedFeeFilter) {
        students.filter { stu ->
            val matchesSearch = stu.name.contains(searchQuery, ignoreCase = true) ||
                    stu.rollNumber.contains(searchQuery, ignoreCase = true) ||
                    stu.grade.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFeeFilter) {
                "Pending Only" -> stu.pendingFee > 0
                "Fully Paid" -> stu.pendingFee <= 0
                "Partial" -> stu.paidFee > 0 && stu.pendingFee > 0
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    val filteredTeachers = remember(teachers, searchQuery, selectedSalaryFilter) {
        teachers.filter { teach ->
            val matchesSearch = teach.name.contains(searchQuery, ignoreCase = true) ||
                    teach.subject.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedSalaryFilter) {
                "Pending Only" -> teach.salaryStatus != "PAID"
                "Paid" -> teach.salaryStatus == "PAID"
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Dual Tab Header (Student Fees vs Faculty Salaries)
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Student Fees (${summary.studentsWithPendingFeesCount} Due)",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null)
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Teacher Payroll (${summary.teachersPendingSalaryCount} Due)",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null)
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("finance_screen_list"),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (selectedTab == 0) {
                    // TAB 0: STUDENT FEES
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricCard(
                                title = "Fees Collected",
                                value = "$${String.format(Locale.US, "%,.0f", summary.totalFeesCollected)}",
                                subtitle = "Target: $${String.format(Locale.US, "%,.0f", summary.totalFeesExpected)}",
                                icon = Icons.Default.CheckCircle,
                                iconBgColor = StatusGreenLight,
                                iconTint = StatusGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Outstanding Dues",
                                value = "$${String.format(Locale.US, "%,.0f", summary.totalFeesPending)}",
                                subtitle = "${summary.studentsWithPendingFeesCount} students pending",
                                icon = Icons.Default.Receipt,
                                iconBgColor = StatusRedLight,
                                iconTint = StatusRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Search & Fee Filters
                    item {
                        CustomSearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "Search student name, roll number, or grade..."
                        )
                    }

                    item {
                        val feeFilters = listOf("Pending Only", "All", "Partial", "Fully Paid")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            feeFilters.forEach { f ->
                                val isSelected = selectedFeeFilter == f
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Slate200, RoundedCornerShape(10.dp))
                                    .clickable { selectedFeeFilter = f }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = f,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color.White else Slate800,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    if (filteredStudents.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "No Fee Records Found",
                                message = "No students match your search or filter criteria."
                            )
                        }
                    } else {
                        items(filteredStudents, key = { it.id }) { student ->
                            StudentFeeItemCard(
                                student = student,
                                onRecordPayment = { feePaymentStudent = student },
                                onSendReminder = { viewModel.sendFeeReminderToParent(student) }
                            )
                        }
                    }

                    // Recent Fee Payments Log
                    if (feeTransactions.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Recent Fee Receipts & Clearances",
                                subtitle = "Audit log of verified collections"
                            )
                        }

                        items(feeTransactions.take(4)) { txn ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = txn.studentName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${txn.paymentDate} • ${txn.paymentMethod}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Receipt: ${txn.receiptNumber}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate400
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "+$${String.format(Locale.US, "%.2f", txn.amount)}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = StatusGreen
                                        )
                                        OutlinedButton(
                                            onClick = { viewingReceipt = txn },
                                            modifier = Modifier.height(30.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("View Receipt", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else {
                    // TAB 1: TEACHER SALARIES & PAYROLL
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricCard(
                                title = "Monthly Payroll Budget",
                                value = "$${String.format(Locale.US, "%,.0f", summary.totalMonthlyPayrollLiability)}",
                                subtitle = "${teachers.size} faculty members",
                                icon = Icons.Default.AccountBalance,
                                iconBgColor = Color(0xFFDBEAFE),
                                iconTint = SchoolBluePrimary,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Pending Disbursement",
                                value = "$${String.format(Locale.US, "%,.0f", summary.totalSalariesPending)}",
                                subtitle = "${summary.teachersPendingSalaryCount} staff awaiting pay",
                                icon = Icons.Default.CreditCard,
                                iconBgColor = ScholasticAmberLight,
                                iconTint = ScholasticAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        CustomSearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "Search faculty name or subject..."
                        )
                    }

                    item {
                        val salaryFilters = listOf("All", "Pending Only", "Paid")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            salaryFilters.forEach { f ->
                                val isSelected = selectedSalaryFilter == f
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Slate200, RoundedCornerShape(10.dp))
                                    .clickable { selectedSalaryFilter = f }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = f,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color.White else Slate800,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    if (filteredTeachers.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "No Faculty Found",
                                message = "No teaching staff match your search or filter."
                            )
                        }
                    } else {
                        items(filteredTeachers, key = { it.id }) { teacher ->
                            TeacherSalaryItemCard(
                                teacher = teacher,
                                onDisburseSalary = { salaryTeacher = teacher }
                            )
                        }
                    }

                    // Disbursed Salary Log
                    if (salaryPayments.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Disbursed Salary Audit Log",
                                subtitle = "Electronic bank transfer confirmations"
                            )
                        }

                        items(salaryPayments.take(4)) { pay ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = pay.teacherName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${pay.monthYear} • ${pay.paymentDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Ref: ${pay.transactionRef}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate400
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "$${String.format(Locale.US, "%,.0f", pay.amount)}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = SchoolBluePrimary
                                        )
                                        StatusBadge(text = "DISBURSED", type = BadgeType.SUCCESS)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button for Teachers Tab
        if (selectedTab == 1) {
            FloatingActionButton(
                onClick = { showAddTeacherDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 20.dp)
                    .testTag("add_teacher_fab"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Teacher")
            }
        }
    }

    // Dialogs
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

    salaryTeacher?.let { teach ->
        SalaryDisbursementDialog(
            teacher = teach,
            onDismiss = { salaryTeacher = null },
            onConfirm = { amount, month, method ->
                viewModel.disburseSalary(teach, amount, month, method)
                salaryTeacher = null
            }
        )
    }

    viewingReceipt?.let { rec ->
        FeeReceiptPreviewDialog(
            transaction = rec,
            onDismiss = { viewingReceipt = null }
        )
    }

    if (showAddTeacherDialog) {
        AddTeacherDialog(
            onDismiss = { showAddTeacherDialog = false },
            onConfirm = { name, subject, grades, phone, email, salary ->
                viewModel.addNewTeacher(name, subject, grades, phone, email, salary)
                showAddTeacherDialog = false
            }
        )
    }
}

@Composable
private fun StudentFeeItemCard(
    student: StudentEntity,
    onRecordPayment: () -> Unit,
    onSendReminder: () -> Unit
) {
    val progress = if (student.totalFee > 0) (student.paidFee / student.totalFee).toFloat() else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_fee_item_${student.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
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
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${student.grade} • Roll: ${student.rollNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (student.isFeeFullyPaid) {
                    StatusBadge(text = "CLEARED", type = BadgeType.SUCCESS)
                } else if (student.paidFee > 0) {
                    StatusBadge(text = "PARTIAL", type = BadgeType.WARNING)
                } else {
                    StatusBadge(text = "OVERDUE", type = BadgeType.DANGER)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fee Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Paid: $${String.format(Locale.US, "%,.0f", student.paidFee)} / $${String.format(Locale.US, "%,.0f", student.totalFee)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Pending: $${String.format(Locale.US, "%,.0f", student.pendingFee)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = if (student.isFeeFullyPaid) StatusGreen else StatusRed
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (student.isFeeFullyPaid) StatusGreen else SchoolBluePrimary,
                trackColor = Slate200
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due Date: ${student.feeDueDate} • ${student.parentName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500
                )
            }

            if (!student.isFeeFullyPaid) {
                Spacer(modifier = Modifier.height(12.dp))
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
                        Text("Fee Reminder", style = MaterialTheme.typography.labelSmall)
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
                        Text("Record Payment", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherSalaryItemCard(
    teacher: TeacherEntity,
    onDisburseSalary: () -> Unit
) {
    val isPaid = teacher.salaryStatus == "PAID"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("teacher_salary_item_${teacher.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = teacher.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${teacher.subject} • ${teacher.gradeAssigned}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "A/C: ${teacher.accountNumber} • ${teacher.phone}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400
                    )
                }

                if (isPaid) {
                    StatusBadge(text = "PAID", type = BadgeType.SUCCESS)
                } else if (teacher.salaryStatus == "PROCESSING") {
                    StatusBadge(text = "PROCESSING", type = BadgeType.WARNING)
                } else {
                    StatusBadge(text = "PENDING", type = BadgeType.DANGER)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Monthly Net Salary",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%,.0f", teacher.monthlySalary)} / mo",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (isPaid && teacher.lastPaidDate.isNotBlank()) {
                        Text(
                            text = "Paid on ${teacher.lastPaidDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusGreen
                        )
                    }
                }

                if (!isPaid) {
                    Button(
                        onClick = onDisburseSalary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Disburse Salary", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
