package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.AttendanceEntity
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.ChatThreadEntity
import com.example.data.entity.FeeTransactionEntity
import com.example.data.entity.NoticeEntity
import com.example.data.entity.SalaryPaymentEntity
import com.example.data.entity.StudentEntity
import com.example.data.entity.TeacherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    // Students
    @Query("SELECT * FROM students ORDER BY grade ASC, name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE grade = :grade ORDER BY name ASC")
    fun getStudentsByGrade(grade: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE (totalFee - paidFee) > 0 ORDER BY (totalFee - paidFee) DESC")
    fun getStudentsWithPendingFees(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET paidFee = paidFee + :amount WHERE id = :studentId")
    suspend fun recordStudentFeePayment(studentId: Long, amount: Double)

    // Attendance
    @Query("SELECT * FROM attendance WHERE date = :date AND grade = :grade ORDER BY studentName ASC")
    fun getAttendanceForDateAndGrade(date: String, grade: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY studentName ASC")
    fun getAllAttendanceForDate(date: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>)

    @Query("DELETE FROM attendance WHERE date = :date AND grade = :grade")
    suspend fun deleteAttendanceForDateAndGrade(date: String, grade: String)

    // Teachers
    @Query("SELECT * FROM teachers ORDER BY name ASC")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE salaryStatus = 'PENDING' OR salaryStatus = 'PROCESSING'")
    fun getTeachersWithPendingSalaries(): Flow<List<TeacherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<TeacherEntity>)

    @Update
    suspend fun updateTeacher(teacher: TeacherEntity)

    @Query("UPDATE teachers SET salaryStatus = 'PAID', lastPaidDate = :paidDate WHERE id = :teacherId")
    suspend fun markTeacherSalaryPaid(teacherId: Long, paidDate: String)

    // Fee Transactions
    @Query("SELECT * FROM fee_transactions ORDER BY id DESC")
    fun getAllFeeTransactions(): Flow<List<FeeTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeTransaction(transaction: FeeTransactionEntity): Long

    // Salary Payments
    @Query("SELECT * FROM salary_payments ORDER BY id DESC")
    fun getAllSalaryPayments(): Flow<List<SalaryPaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalaryPayment(payment: SalaryPaymentEntity): Long

    // Notices / Circulars
    @Query("SELECT * FROM notices ORDER BY isUrgent DESC, id DESC")
    fun getAllNotices(): Flow<List<NoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<NoticeEntity>)

    // Chat Threads
    @Query("SELECT * FROM chat_threads ORDER BY lastMessageTimestamp DESC")
    fun getAllChatThreads(): Flow<List<ChatThreadEntity>>

    @Query("SELECT * FROM chat_threads WHERE id = :threadId")
    suspend fun getChatThreadById(threadId: Long): ChatThreadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatThread(thread: ChatThreadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatThreads(threads: List<ChatThreadEntity>)

    @Update
    suspend fun updateChatThread(thread: ChatThreadEntity)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun getMessagesForThread(threadId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_threads SET unreadCount = 0 WHERE id = :threadId")
    suspend fun markThreadAsRead(threadId: Long)
}
