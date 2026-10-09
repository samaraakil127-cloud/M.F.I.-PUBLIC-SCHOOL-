package com.example.data.repository

import com.example.data.dao.SchoolDao
import com.example.data.entity.AttendanceEntity
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.ChatThreadEntity
import com.example.data.entity.FeeTransactionEntity
import com.example.data.entity.NoticeEntity
import com.example.data.entity.SalaryPaymentEntity
import com.example.data.entity.StudentEntity
import com.example.data.entity.TeacherEntity
import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val dao: SchoolDao) {

    // Students
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    val pendingFeeStudents: Flow<List<StudentEntity>> = dao.getStudentsWithPendingFees()

    fun getStudentsByGrade(grade: String): Flow<List<StudentEntity>> = dao.getStudentsByGrade(grade)

    suspend fun getStudentById(id: Long): StudentEntity? = dao.getStudentById(id)

    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)

    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)

    suspend fun recordFeePayment(
        studentId: Long,
        studentName: String,
        amount: Double,
        date: String,
        method: String,
        receiptNo: String,
        notes: String
    ) {
        dao.recordStudentFeePayment(studentId, amount)
        val transaction = FeeTransactionEntity(
            studentId = studentId,
            studentName = studentName,
            amount = amount,
            paymentDate = date,
            paymentMethod = method,
            receiptNumber = receiptNo,
            notes = notes
        )
        dao.insertFeeTransaction(transaction)
    }

    // Attendance
    fun getAttendanceForDateAndGrade(date: String, grade: String): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForDateAndGrade(date, grade)

    fun getAllAttendanceForDate(date: String): Flow<List<AttendanceEntity>> =
        dao.getAllAttendanceForDate(date)

    suspend fun updateAttendanceRecord(record: AttendanceEntity) =
        dao.insertOrUpdateAttendance(record)

    suspend fun saveBatchAttendance(records: List<AttendanceEntity>) =
        dao.insertAttendanceList(records)

    // Teachers & Payroll
    val allTeachers: Flow<List<TeacherEntity>> = dao.getAllTeachers()
    val pendingSalaryTeachers: Flow<List<TeacherEntity>> = dao.getTeachersWithPendingSalaries()

    suspend fun insertTeacher(teacher: TeacherEntity): Long = dao.insertTeacher(teacher)

    suspend fun updateTeacher(teacher: TeacherEntity) = dao.updateTeacher(teacher)

    suspend fun disburseSalary(
        teacherId: Long,
        teacherName: String,
        amount: Double,
        monthYear: String,
        date: String,
        txnRef: String,
        method: String
    ) {
        dao.markTeacherSalaryPaid(teacherId, date)
        val payment = SalaryPaymentEntity(
            teacherId = teacherId,
            teacherName = teacherName,
            amount = amount,
            monthYear = monthYear,
            paymentDate = date,
            transactionRef = txnRef,
            paymentMethod = method
        )
        dao.insertSalaryPayment(payment)
    }

    // Transactions & History
    val allFeeTransactions: Flow<List<FeeTransactionEntity>> = dao.getAllFeeTransactions()
    val allSalaryPayments: Flow<List<SalaryPaymentEntity>> = dao.getAllSalaryPayments()

    // Notices
    val allNotices: Flow<List<NoticeEntity>> = dao.getAllNotices()

    suspend fun insertNotice(notice: NoticeEntity): Long = dao.insertNotice(notice)

    // Communication
    val allChatThreads: Flow<List<ChatThreadEntity>> = dao.getAllChatThreads()

    suspend fun getChatThreadById(threadId: Long): ChatThreadEntity? = dao.getChatThreadById(threadId)

    suspend fun insertChatThread(thread: ChatThreadEntity): Long = dao.insertChatThread(thread)

    fun getMessagesForThread(threadId: Long): Flow<List<ChatMessageEntity>> =
        dao.getMessagesForThread(threadId)

    suspend fun sendMessage(
        threadId: Long,
        senderName: String,
        senderRole: String,
        content: String
    ) {
        val now = System.currentTimeMillis()
        val msg = ChatMessageEntity(
            threadId = threadId,
            senderName = senderName,
            senderRole = senderRole,
            content = content,
            timestamp = now
        )
        dao.insertChatMessage(msg)

        val thread = dao.getChatThreadById(threadId)
        if (thread != null) {
            val updated = thread.copy(
                lastMessage = content,
                lastMessageTime = "Just now"
            )
            dao.updateChatThread(updated)
        }
    }

    suspend fun markThreadAsRead(threadId: Long) = dao.markThreadAsRead(threadId)
}
