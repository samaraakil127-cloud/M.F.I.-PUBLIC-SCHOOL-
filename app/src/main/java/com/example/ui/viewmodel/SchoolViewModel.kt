package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.AttendanceEntity
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.ChatThreadEntity
import com.example.data.entity.NoticeEntity
import com.example.data.entity.StudentEntity
import com.example.data.entity.TeacherEntity
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class DashboardSummary(
    val totalStudents: Int = 0,
    val totalTeachers: Int = 0,
    val totalFeesExpected: Double = 0.0,
    val totalFeesCollected: Double = 0.0,
    val totalFeesPending: Double = 0.0,
    val studentsWithPendingFeesCount: Int = 0,
    val totalMonthlyPayrollLiability: Double = 0.0,
    val totalSalariesDisbursed: Double = 0.0,
    val totalSalariesPending: Double = 0.0,
    val teachersPendingSalaryCount: Int = 0,
    val todayAttendancePresent: Int = 0,
    val todayAttendanceTotal: Int = 0,
    val todayAttendanceRate: Float = 0f,
    val unreadMessagesCount: Int = 0
)

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchoolRepository

    private val todayString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _selectedDate = MutableStateFlow(todayString)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedAttendanceGrade = MutableStateFlow("Grade 10A")
    val selectedAttendanceGrade: StateFlow<String> = _selectedAttendanceGrade.asStateFlow()

    private val _activeChatThreadId = MutableStateFlow<Long?>(null)
    val activeChatThreadId: StateFlow<Long?> = _activeChatThreadId.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = SchoolRepository(database.schoolDao())
    }

    val students: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teachers: StateFlow<List<TeacherEntity>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notices: StateFlow<List<NoticeEntity>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatThreads: StateFlow<List<ChatThreadEntity>> = repository.allChatThreads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feeTransactions = repository.allFeeTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salaryPayments = repository.allSalaryPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-time attendance for selected date
    @OptIn(ExperimentalCoroutinesApi::class)
    val attendanceForDate: StateFlow<List<AttendanceEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getAllAttendanceForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Messages for currently active chat thread
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = _activeChatThreadId
        .flatMapLatest { threadId ->
            if (threadId != null) repository.getMessagesForThread(threadId)
            else kotlinx.coroutines.flow.flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unified executive dashboard KPI calculations
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        students,
        teachers,
        attendanceForDate,
        chatThreads
    ) { stuList, teachList, attendList, threads ->
        val totalStu = stuList.size
        val totalTeach = teachList.size
        val feesExpected = stuList.sumOf { it.totalFee }
        val feesCollected = stuList.sumOf { it.paidFee }
        val feesPending = (feesExpected - feesCollected).coerceAtLeast(0.0)
        val stuPendingCount = stuList.count { it.pendingFee > 0 }

        val payrollLiability = teachList.sumOf { it.monthlySalary }
        val salariesDisbursed = teachList.filter { it.salaryStatus == "PAID" }.sumOf { it.monthlySalary }
        val salariesPending = teachList.filter { it.salaryStatus != "PAID" }.sumOf { it.monthlySalary }
        val teachersPendingCount = teachList.count { it.salaryStatus != "PAID" }

        val presentCount = attendList.count { it.status == "PRESENT" || it.status == "LATE" }
        val totalAttendToday = if (attendList.isNotEmpty()) attendList.size else totalStu
        val attendRate = if (totalAttendToday > 0) (presentCount.toFloat() / totalAttendToday) * 100f else 0f

        val unreadTotal = threads.sumOf { it.unreadCount }

        DashboardSummary(
            totalStudents = totalStu,
            totalTeachers = totalTeach,
            totalFeesExpected = feesExpected,
            totalFeesCollected = feesCollected,
            totalFeesPending = feesPending,
            studentsWithPendingFeesCount = stuPendingCount,
            totalMonthlyPayrollLiability = payrollLiability,
            totalSalariesDisbursed = salariesDisbursed,
            totalSalariesPending = salariesPending,
            teachersPendingSalaryCount = teachersPendingCount,
            todayAttendancePresent = presentCount,
            todayAttendanceTotal = totalAttendToday,
            todayAttendanceRate = attendRate,
            unreadMessagesCount = unreadTotal
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    fun clearToast() {
        _toastMessage.value = null
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setSelectedAttendanceGrade(grade: String) {
        _selectedAttendanceGrade.value = grade
    }

    fun setActiveChatThread(threadId: Long?) {
        _activeChatThreadId.value = threadId
        if (threadId != null) {
            viewModelScope.launch {
                repository.markThreadAsRead(threadId)
            }
        }
    }

    // Attendance Operations
    fun toggleStudentAttendance(student: StudentEntity, nextStatus: String, remark: String = "") {
        viewModelScope.launch {
            val record = AttendanceEntity(
                studentId = student.id,
                studentName = student.name,
                grade = student.grade,
                date = _selectedDate.value,
                status = nextStatus,
                remark = remark
            )
            repository.updateAttendanceRecord(record)
        }
    }

    fun markAllPresentForGrade(grade: String) {
        viewModelScope.launch {
            val studentsInGrade = students.value.filter { it.grade == grade }
            val records = studentsInGrade.map {
                AttendanceEntity(
                    studentId = it.id,
                    studentName = it.name,
                    grade = it.grade,
                    date = _selectedDate.value,
                    status = "PRESENT",
                    remark = ""
                )
            }
            repository.saveBatchAttendance(records)
            _toastMessage.value = "All ${studentsInGrade.size} students in $grade marked Present"
        }
    }

    fun notifyParentsOfAbsentees(grade: String) {
        viewModelScope.launch {
            val absentList = attendanceForDate.value.filter {
                (grade == "All" || it.grade == grade) && it.status == "ABSENT"
            }
            if (absentList.isEmpty()) {
                _toastMessage.value = "No absent students to notify in $grade."
                return@launch
            }
            // Create automated notice & notification
            val notice = NoticeEntity(
                title = "Daily Attendance Alert: ${absentList.size} Student(s) Absent ($grade)",
                content = "Automated SMS & App alerts sent to parents of: " +
                        absentList.joinToString(", ") { it.studentName } +
                        ". Please submit leave letters if excusable.",
                targetAudience = "Parents",
                category = "Urgent",
                date = _selectedDate.value,
                isUrgent = true,
                author = "Attendance Officer"
            )
            repository.insertNotice(notice)
            _toastMessage.value = "Alerts sent to ${absentList.size} parent(s) successfully!"
        }
    }

    // Fee Operations
    fun recordFeePayment(
        student: StudentEntity,
        amount: Double,
        paymentMethod: String,
        notes: String
    ) {
        viewModelScope.launch {
            val receiptNo = "REC-${System.currentTimeMillis().toString().takeLast(6)}"
            repository.recordFeePayment(
                studentId = student.id,
                studentName = student.name,
                amount = amount,
                date = todayString,
                method = paymentMethod,
                receiptNo = receiptNo,
                notes = notes
            )
            _toastMessage.value = "Payment of $${String.format(Locale.US, "%.2f", amount)} recorded! Receipt: $receiptNo"
        }
    }

    fun sendFeeReminderToParent(student: StudentEntity) {
        viewModelScope.launch {
            val reminderContent = "Friendly Reminder: Tuition fees of $${String.format(Locale.US, "%.2f", student.pendingFee)} for ${student.name} (${student.grade}) are pending (Due: ${student.feeDueDate}). Please clear dues to avoid late penalties."
            
            // Post urgent notice
            val notice = NoticeEntity(
                title = "Fee Reminder: ${student.name} (${student.grade})",
                content = reminderContent,
                targetAudience = "Parents",
                category = "Fees",
                date = todayString,
                isUrgent = true,
                author = "Accounts Office"
            )
            repository.insertNotice(notice)

            // Also send direct chat message if thread exists or create one
            val existingThread = chatThreads.value.find { it.studentName.contains(student.name, ignoreCase = true) }
            if (existingThread != null) {
                repository.sendMessage(
                    threadId = existingThread.id,
                    senderName = "Accounts Desk",
                    senderRole = "Admin",
                    content = reminderContent
                )
            } else {
                val newThreadId = repository.insertChatThread(
                    ChatThreadEntity(
                        studentName = student.name,
                        studentGrade = student.grade,
                        parentName = student.parentName,
                        teacherName = "Accounts Desk (Bursar)",
                        subject = "Pending Fee Notification",
                        lastMessage = reminderContent,
                        lastMessageTime = "Just now",
                        unreadCount = 1
                    )
                )
                repository.sendMessage(
                    threadId = newThreadId,
                    senderName = "Accounts Desk",
                    senderRole = "Admin",
                    content = reminderContent
                )
            }
            _toastMessage.value = "Fee reminder sent to ${student.parentName} via SMS & Message Hub!"
        }
    }

    // Salary Operations
    fun disburseSalary(
        teacher: TeacherEntity,
        amount: Double,
        monthYear: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            val txnRef = "TXN-SAL-${UUID.randomUUID().toString().take(8).uppercase()}"
            repository.disburseSalary(
                teacherId = teacher.id,
                teacherName = teacher.name,
                amount = amount,
                monthYear = monthYear,
                date = todayString,
                txnRef = txnRef,
                method = paymentMethod
            )
            _toastMessage.value = "Salary of $${String.format(Locale.US, "%.2f", amount)} disbursed to ${teacher.name}! Ref: $txnRef"
        }
    }

    // Add New Student
    fun addNewStudent(
        name: String,
        roll: String,
        grade: String,
        parentName: String,
        phone: String,
        email: String,
        totalFee: Double,
        dueDate: String
    ) {
        viewModelScope.launch {
            val colors = listOf("#3B82F6", "#10B981", "#8B5CF6", "#F59E0B", "#EC4899", "#06B6D4")
            val randomColor = colors.random()
            val student = StudentEntity(
                name = name,
                rollNumber = roll,
                grade = grade,
                parentName = parentName,
                parentPhone = phone,
                parentEmail = email,
                totalFee = totalFee,
                paidFee = 0.0,
                feeDueDate = dueDate,
                avatarColorHex = randomColor
            )
            repository.insertStudent(student)
            _toastMessage.value = "Student $name added to $grade!"
        }
    }

    // Add New Teacher
    fun addNewTeacher(
        name: String,
        subject: String,
        grades: String,
        phone: String,
        email: String,
        salary: Double
    ) {
        viewModelScope.launch {
            val teacher = TeacherEntity(
                name = name,
                subject = subject,
                gradeAssigned = grades,
                phone = phone,
                email = email,
                monthlySalary = salary,
                salaryStatus = "PENDING",
                lastPaidDate = "",
                accountNumber = "AC-NEW-${(1000..9999).random()}"
            )
            repository.insertTeacher(teacher)
            _toastMessage.value = "Teacher $name ($subject) registered successfully!"
        }
    }

    // Add Notice
    fun postNotice(
        title: String,
        content: String,
        target: String,
        category: String,
        isUrgent: Boolean
    ) {
        viewModelScope.launch {
            val notice = NoticeEntity(
                title = title,
                content = content,
                targetAudience = target,
                category = category,
                date = todayString,
                isUrgent = isUrgent,
                author = "Administration"
            )
            repository.insertNotice(notice)
            _toastMessage.value = "Notice posted successfully to $target!"
        }
    }

    // Messaging
    fun sendMessage(content: String, senderRole: String = "Admin") {
        val threadId = _activeChatThreadId.value ?: return
        viewModelScope.launch {
            val senderName = if (senderRole == "Admin") "School Office" else "Teacher"
            repository.sendMessage(threadId, senderName, senderRole, content)
            
            // Auto-generate realistic simulated reply from parent or teacher after short delay
            kotlinx.coroutines.delay(1000)
            val thread = repository.getChatThreadById(threadId)
            if (thread != null) {
                val replies = listOf(
                    "Thank you for informing me, I have noted this down.",
                    "Understood! We will ensure this is taken care of.",
                    "Thanks for the prompt communication and support.",
                    "Confirmed. We appreciate the update from the school."
                )
                val replyMsg = replies.random()
                repository.sendMessage(
                    threadId = threadId,
                    senderName = thread.parentName,
                    senderRole = "Parent",
                    content = replyMsg
                )
            }
        }
    }

    fun startNewParentTeacherThread(
        student: StudentEntity,
        teacher: TeacherEntity,
        initialTopic: String,
        initialMessage: String
    ) {
        viewModelScope.launch {
            val threadId = repository.insertChatThread(
                ChatThreadEntity(
                    studentName = student.name,
                    studentGrade = student.grade,
                    parentName = student.parentName,
                    teacherName = teacher.name,
                    subject = initialTopic,
                    lastMessage = initialMessage,
                    lastMessageTime = "Just now",
                    unreadCount = 0
                )
            )
            repository.sendMessage(
                threadId = threadId,
                senderName = teacher.name,
                senderRole = "Teacher",
                content = initialMessage
            )
            setActiveChatThread(threadId)
            _toastMessage.value = "Conversation started with ${student.parentName}!"
        }
    }
}
