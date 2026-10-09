package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val rollNumber: String,
    val grade: String,
    val parentName: String,
    val parentPhone: String,
    val parentEmail: String,
    val totalFee: Double,
    val paidFee: Double,
    val feeDueDate: String,
    val avatarColorHex: String = "#3B82F6"
) {
    val pendingFee: Double get() = (totalFee - paidFee).coerceAtLeast(0.0)
    val isFeeFullyPaid: Boolean get() = pendingFee <= 0.0
    val isFeeOverdue: Boolean get() = pendingFee > 0.0
}

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val grade: String,
    val date: String, // YYYY-MM-DD
    val status: String, // PRESENT, ABSENT, LATE, EXCUSED
    val remark: String = ""
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val subject: String,
    val gradeAssigned: String,
    val phone: String,
    val email: String,
    val monthlySalary: Double,
    val salaryStatus: String, // PAID, PENDING, PROCESSING
    val lastPaidDate: String = "",
    val accountNumber: String = "AC-8892-019"
)

@Entity(tableName = "fee_transactions")
data class FeeTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val amount: Double,
    val paymentDate: String,
    val paymentMethod: String, // Online, Cash, Bank Transfer, Cheque
    val receiptNumber: String,
    val notes: String = ""
)

@Entity(tableName = "salary_payments")
data class SalaryPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teacherId: Long,
    val teacherName: String,
    val amount: Double,
    val monthYear: String,
    val paymentDate: String,
    val transactionRef: String,
    val paymentMethod: String = "Bank Direct Deposit"
)

@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val targetAudience: String, // All, Parents, Teachers, High School
    val category: String, // Academic, Notice, Event, Holiday, Urgent
    val date: String,
    val isUrgent: Boolean = false,
    val author: String = "School Administration"
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentName: String,
    val studentGrade: String,
    val parentName: String,
    val teacherName: String,
    val subject: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threadId: Long,
    val senderName: String,
    val senderRole: String, // "Parent", "Teacher", "Admin"
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = true
)
