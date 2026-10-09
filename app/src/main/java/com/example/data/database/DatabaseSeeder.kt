package com.example.data.database

import com.example.data.dao.SchoolDao
import com.example.data.entity.AttendanceEntity
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.ChatThreadEntity
import com.example.data.entity.FeeTransactionEntity
import com.example.data.entity.NoticeEntity
import com.example.data.entity.SalaryPaymentEntity
import com.example.data.entity.StudentEntity
import com.example.data.entity.TeacherEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DatabaseSeeder {
    suspend fun populateDatabase(dao: SchoolDao) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val students = listOf(
            StudentEntity(
                name = "Liam Alexander",
                rollNumber = "10A-01",
                grade = "Grade 10A",
                parentName = "Arthur Alexander",
                parentPhone = "+1 (555) 234-8901",
                parentEmail = "arthur.alex@example.com",
                totalFee = 4500.0,
                paidFee = 4500.0,
                feeDueDate = "2026-10-15",
                avatarColorHex = "#3B82F6"
            ),
            StudentEntity(
                name = "Sophia Martinez",
                rollNumber = "10A-02",
                grade = "Grade 10A",
                parentName = "Elena Martinez",
                parentPhone = "+1 (555) 345-6789",
                parentEmail = "elena.m@example.com",
                totalFee = 4800.0,
                paidFee = 2800.0,
                feeDueDate = "2026-10-10",
                avatarColorHex = "#EC4899"
            ),
            StudentEntity(
                name = "Ethan Cole Vance",
                rollNumber = "10A-03",
                grade = "Grade 10A",
                parentName = "Claire Vance",
                parentPhone = "+1 (555) 456-7890",
                parentEmail = "claire.vance@example.com",
                totalFee = 4800.0,
                paidFee = 1600.0,
                feeDueDate = "2026-09-30",
                avatarColorHex = "#8B5CF6"
            ),
            StudentEntity(
                name = "Olivia Jenkins",
                rollNumber = "10A-04",
                grade = "Grade 10A",
                parentName = "Paul Jenkins",
                parentPhone = "+1 (555) 567-8901",
                parentEmail = "paul.j@example.com",
                totalFee = 4500.0,
                paidFee = 4500.0,
                feeDueDate = "2026-10-15",
                avatarColorHex = "#10B981"
            ),
            StudentEntity(
                name = "Kabir Patel",
                rollNumber = "8A-01",
                grade = "Grade 8A",
                parentName = "Dr. Anita Patel",
                parentPhone = "+1 (555) 678-9012",
                parentEmail = "anita.patel@example.com",
                totalFee = 4200.0,
                paidFee = 2100.0,
                feeDueDate = "2026-10-20",
                avatarColorHex = "#F59E0B"
            ),
            StudentEntity(
                name = "Maya Lin",
                rollNumber = "8A-02",
                grade = "Grade 8A",
                parentName = "Wei Lin",
                parentPhone = "+1 (555) 789-0123",
                parentEmail = "wei.lin@example.com",
                totalFee = 4200.0,
                paidFee = 4200.0,
                feeDueDate = "2026-10-20",
                avatarColorHex = "#06B6D4"
            ),
            StudentEntity(
                name = "Emma Miller",
                rollNumber = "9B-01",
                grade = "Grade 9B",
                parentName = "David Miller",
                parentPhone = "+1 (555) 890-1234",
                parentEmail = "david.miller@example.com",
                totalFee = 4400.0,
                paidFee = 4400.0,
                feeDueDate = "2026-10-25",
                avatarColorHex = "#6366F1"
            ),
            StudentEntity(
                name = "Noah Brooks",
                rollNumber = "9B-02",
                grade = "Grade 9B",
                parentName = "Karen Brooks",
                parentPhone = "+1 (555) 901-2345",
                parentEmail = "karen.brooks@example.com",
                totalFee = 4400.0,
                paidFee = 1400.0,
                feeDueDate = "2026-09-25",
                avatarColorHex = "#EF4444"
            ),
            StudentEntity(
                name = "Lucas Johnson",
                rollNumber = "11A-01",
                grade = "Grade 11A",
                parentName = "Robert Johnson",
                parentPhone = "+1 (555) 012-3456",
                parentEmail = "robert.j@example.com",
                totalFee = 5200.0,
                paidFee = 5200.0,
                feeDueDate = "2026-10-05",
                avatarColorHex = "#14B8A6"
            ),
            StudentEntity(
                name = "Aaliyah Khan",
                rollNumber = "11A-02",
                grade = "Grade 11A",
                parentName = "Tariq Khan",
                parentPhone = "+1 (555) 123-4567",
                parentEmail = "tariq.khan@example.com",
                totalFee = 5200.0,
                paidFee = 2600.0,
                feeDueDate = "2026-10-18",
                avatarColorHex = "#A855F7"
            ),
            StudentEntity(
                name = "Benjamin Scott",
                rollNumber = "12B-01",
                grade = "Grade 12B",
                parentName = "Sandra Scott",
                parentPhone = "+1 (555) 234-5678",
                parentEmail = "sandra.s@example.com",
                totalFee = 5500.0,
                paidFee = 5500.0,
                feeDueDate = "2026-10-12",
                avatarColorHex = "#F97316"
            ),
            StudentEntity(
                name = "Chloe Dubois",
                rollNumber = "12B-02",
                grade = "Grade 12B",
                parentName = "Marc Dubois",
                parentPhone = "+1 (555) 345-7890",
                parentEmail = "marc.dubois@example.com",
                totalFee = 5500.0,
                paidFee = 2500.0,
                feeDueDate = "2026-09-28",
                avatarColorHex = "#3B82F6"
            )
        )
        dao.insertStudents(students)

        val teachers = listOf(
            TeacherEntity(
                name = "Dr. Marcus Vance",
                subject = "Mathematics & Statistics",
                gradeAssigned = "Grade 10 & 11",
                phone = "+1 (555) 782-9901",
                email = "marcus.vance@school.edu",
                monthlySalary = 4800.0,
                salaryStatus = "PENDING",
                lastPaidDate = "2026-09-01",
                accountNumber = "CHASE-****-4412"
            ),
            TeacherEntity(
                name = "Sarah Jenkins",
                subject = "Advanced Physics",
                gradeAssigned = "Grade 9 & 12",
                phone = "+1 (555) 671-8822",
                email = "sarah.j@school.edu",
                monthlySalary = 4500.0,
                salaryStatus = "PAID",
                lastPaidDate = "2026-10-01",
                accountNumber = "WF-****-9810"
            ),
            TeacherEntity(
                name = "David Chen",
                subject = "Computer Science & Robotics",
                gradeAssigned = "Grade 8, 10 & 11",
                phone = "+1 (555) 542-7711",
                email = "david.chen@school.edu",
                monthlySalary = 4600.0,
                salaryStatus = "PENDING",
                lastPaidDate = "2026-09-01",
                accountNumber = "BOA-****-3321"
            ),
            TeacherEntity(
                name = "Elena Rostova",
                subject = "English Literature & Drama",
                gradeAssigned = "Grade 8 & 10",
                phone = "+1 (555) 431-6600",
                email = "elena.r@school.edu",
                monthlySalary = 4200.0,
                salaryStatus = "PAID",
                lastPaidDate = "2026-10-01",
                accountNumber = "CITI-****-5590"
            ),
            TeacherEntity(
                name = "Michael Torres",
                subject = "Chemistry & Organic Lab",
                gradeAssigned = "Grade 11 & 12",
                phone = "+1 (555) 321-5544",
                email = "m.torres@school.edu",
                monthlySalary = 4400.0,
                salaryStatus = "PROCESSING",
                lastPaidDate = "2026-09-01",
                accountNumber = "CAPONE-****-7781"
            ),
            TeacherEntity(
                name = "Rachel Adams",
                subject = "World History & Civics",
                gradeAssigned = "Grade 9 & 10",
                phone = "+1 (555) 210-4433",
                email = "rachel.adams@school.edu",
                monthlySalary = 4100.0,
                salaryStatus = "PAID",
                lastPaidDate = "2026-10-01",
                accountNumber = "USAA-****-1123"
            ),
            TeacherEntity(
                name = "Olivia Taylor",
                subject = "Visual Arts & Digital Media",
                gradeAssigned = "Grade 8 to 12",
                phone = "+1 (555) 109-3322",
                email = "olivia.t@school.edu",
                monthlySalary = 4000.0,
                salaryStatus = "PENDING",
                lastPaidDate = "2026-09-01",
                accountNumber = "PNC-****-6644"
            )
        )
        dao.insertTeachers(teachers)

        // Seed today's initial attendance
        val attendanceList = listOf(
            AttendanceEntity(studentId = 1, studentName = "Liam Alexander", grade = "Grade 10A", date = today, status = "PRESENT"),
            AttendanceEntity(studentId = 2, studentName = "Sophia Martinez", grade = "Grade 10A", date = today, status = "PRESENT"),
            AttendanceEntity(studentId = 3, studentName = "Ethan Cole Vance", grade = "Grade 10A", date = today, status = "ABSENT", remark = "Unexcused - Call sent"),
            AttendanceEntity(studentId = 4, studentName = "Olivia Jenkins", grade = "Grade 10A", date = today, status = "LATE", remark = "Dental appointment - 15m late"),
            AttendanceEntity(studentId = 5, studentName = "Kabir Patel", grade = "Grade 8A", date = today, status = "PRESENT"),
            AttendanceEntity(studentId = 6, studentName = "Maya Lin", grade = "Grade 8A", date = today, status = "PRESENT"),
            AttendanceEntity(studentId = 7, studentName = "Emma Miller", grade = "Grade 9B", date = today, status = "PRESENT"),
            AttendanceEntity(studentId = 8, studentName = "Noah Brooks", grade = "Grade 9B", date = today, status = "ABSENT", remark = "Sick leave notified"),
            AttendanceEntity(studentId = 9, studentName = "Lucas Johnson", grade = "Grade 11A", date = today, status = "PRESENT"),
            AttendanceEntity(studentId = 10, studentName = "Aaliyah Khan", grade = "Grade 11A", date = today, status = "PRESENT"),
            AttendanceEntity(studentId = 11, studentName = "Benjamin Scott", grade = "Grade 12B", date = today, status = "PRESENT"),
            AttendanceEntity(studentId = 12, studentName = "Chloe Dubois", grade = "Grade 12B", date = today, status = "EXCUSED", remark = "College tour representation")
        )
        dao.insertAttendanceList(attendanceList)

        // Seed recent fee transactions
        val feeTransactions = listOf(
            FeeTransactionEntity(
                studentId = 1,
                studentName = "Liam Alexander",
                amount = 2500.0,
                paymentDate = today,
                paymentMethod = "Online UPI/Card",
                receiptNumber = "REC-2026-9901",
                notes = "Second installment Q2 term cleared"
            ),
            FeeTransactionEntity(
                studentId = 9,
                studentName = "Lucas Johnson",
                amount = 3000.0,
                paymentDate = "2026-10-04",
                paymentMethod = "Bank Wire",
                receiptNumber = "REC-2026-9884",
                notes = "Term 1 full final clearance"
            ),
            FeeTransactionEntity(
                studentId = 4,
                studentName = "Olivia Jenkins",
                amount = 2000.0,
                paymentDate = "2026-10-02",
                paymentMethod = "Cashier Desk (Cash)",
                receiptNumber = "REC-2026-9872",
                notes = "Tuition & lab fee installment"
            )
        )
        feeTransactions.forEach { dao.insertFeeTransaction(it) }

        // Seed recent salary payments
        val salaryPayments = listOf(
            SalaryPaymentEntity(
                teacherId = 2,
                teacherName = "Sarah Jenkins",
                amount = 4500.0,
                monthYear = "October 2026",
                paymentDate = "2026-10-01",
                transactionRef = "TXN-SAL-882910"
            ),
            SalaryPaymentEntity(
                teacherId = 4,
                teacherName = "Elena Rostova",
                amount = 4200.0,
                monthYear = "October 2026",
                paymentDate = "2026-10-01",
                transactionRef = "TXN-SAL-882911"
            ),
            SalaryPaymentEntity(
                teacherId = 6,
                teacherName = "Rachel Adams",
                amount = 4100.0,
                monthYear = "October 2026",
                paymentDate = "2026-10-01",
                transactionRef = "TXN-SAL-882912"
            )
        )
        salaryPayments.forEach { dao.insertSalaryPayment(it) }

        // Seed notices
        val notices = listOf(
            NoticeEntity(
                title = "Welcome Address from Director Aakil Yusuf",
                content = "Welcome to M.F.I. Public School. Our dedicated faculty and leadership are committed to academic excellence, student character, and transparent parent-teacher collaboration. Please use this unified portal for all fee payments, attendance tracking, and faculty messaging.",
                targetAudience = "All",
                category = "Academic",
                date = today,
                isUrgent = false,
                author = "Director Aakil Yusuf"
            ),
            NoticeEntity(
                title = "Q2 Term Fee Clearance & Payment Portal Notice",
                content = "Dear Parents and Guardians of M.F.I. Public School, please note that the deadline for Q2 tuition dues is October 15, 2026. Automated receipts and installment options are available via the school dashboard.",
                targetAudience = "Parents",
                category = "Fees",
                date = today,
                isUrgent = true,
                author = "Accounts Office • M.F.I. Public School"
            ),
            NoticeEntity(
                title = "Mid-Term Parent-Teacher Conference (PTM) Schedule",
                content = "Formal Parent-Teacher sessions will be held on Friday, October 24, from 9:00 AM to 3:30 PM. Parents may reserve individual 15-minute consultation slots with class and subject teachers.",
                targetAudience = "All",
                category = "Academic",
                date = today,
                isUrgent = false,
                author = "Director's Office (Aakil Yusuf)"
            ),
            NoticeEntity(
                title = "Inter-School Science Olympiad & Robotics Expo 2026",
                content = "Registration is open for students from Grades 8 through 12 interested in entering projects for the Regional STEM Fair. Please contact Mr. Chen or Mrs. Jenkins by Wednesday.",
                targetAudience = "All",
                category = "Event",
                date = "2026-10-05",
                isUrgent = false,
                author = "Science Department"
            ),
            NoticeEntity(
                title = "Faculty Meeting: Mid-Term Curriculum Review & Salary Disbursal",
                content = "All departmental heads and teaching staff are requested to attend the monthly administrative briefing on Monday at 3:45 PM in the Main Conference Hall.",
                targetAudience = "Teachers",
                category = "Notice",
                date = "2026-10-04",
                isUrgent = false,
                author = "Academic Dean"
            )
        )
        dao.insertNotices(notices)

        // Seed Chat Threads
        val thread1Id = dao.insertChatThread(
            ChatThreadEntity(
                studentName = "Ethan Cole Vance",
                studentGrade = "Grade 10A",
                parentName = "Claire Vance",
                teacherName = "Dr. Marcus Vance",
                subject = "Mathematics & Calculus",
                lastMessage = "Thank you Dr. Vance, Ethan will complete the extra worksheets tonight.",
                lastMessageTime = "10:14 AM",
                unreadCount = 1
            )
        )
        val thread2Id = dao.insertChatThread(
            ChatThreadEntity(
                studentName = "Emma Miller",
                studentGrade = "Grade 9B",
                parentName = "David Miller",
                teacherName = "Sarah Jenkins",
                subject = "Advanced Physics Lab",
                lastMessage = "Emma's optics experiment scored an A+! She showed great curiosity today.",
                lastMessageTime = "Yesterday",
                unreadCount = 0
            )
        )
        val thread3Id = dao.insertChatThread(
            ChatThreadEntity(
                studentName = "Kabir Patel",
                studentGrade = "Grade 8A",
                parentName = "Dr. Anita Patel",
                teacherName = "Elena Rostova",
                subject = "English Literature",
                lastMessage = "Could you suggest additional debate prep material for Kabir?",
                lastMessageTime = "Oct 05",
                unreadCount = 0
            )
        )
        val thread4Id = dao.insertChatThread(
            ChatThreadEntity(
                studentName = "Sophia Martinez",
                studentGrade = "Grade 10A",
                parentName = "Elena Martinez",
                teacherName = "Accounts Desk (Bursar)",
                subject = "Fee Payment & Installments",
                lastMessage = "Payment of $2,800 received and receipt generated successfully.",
                lastMessageTime = "Oct 04",
                unreadCount = 0
            )
        )

        // Seed messages for Thread 1
        val now = System.currentTimeMillis()
        val messagesThread1 = listOf(
            ChatMessageEntity(
                threadId = thread1Id,
                senderName = "Claire Vance",
                senderRole = "Parent",
                content = "Good morning Dr. Vance. I noticed Ethan struggled a bit on the last quadratic polynomial quiz. Is extra help available?",
                timestamp = now - 3600000 * 4
            ),
            ChatMessageEntity(
                threadId = thread1Id,
                senderName = "Dr. Marcus Vance",
                senderRole = "Teacher",
                content = "Hello Mrs. Vance! Ethan has strong fundamentals, he just needs a bit more structured practice on factorization steps. I gave him two sample worksheets today.",
                timestamp = now - 3600000 * 2
            ),
            ChatMessageEntity(
                threadId = thread1Id,
                senderName = "Claire Vance",
                senderRole = "Parent",
                content = "Thank you Dr. Vance, Ethan will complete the extra worksheets tonight.",
                timestamp = now - 1800000
            )
        )
        dao.insertChatMessages(messagesThread1)

        // Seed messages for Thread 2
        val messagesThread2 = listOf(
            ChatMessageEntity(
                threadId = thread2Id,
                senderName = "David Miller",
                senderRole = "Parent",
                content = "Hello Mrs. Jenkins, just following up on Emma's physics group project.",
                timestamp = now - 86400000 * 2
            ),
            ChatMessageEntity(
                threadId = thread2Id,
                senderName = "Sarah Jenkins",
                senderRole = "Teacher",
                content = "Emma's optics experiment scored an A+! She showed great curiosity today.",
                timestamp = now - 86400000
            )
        )
        dao.insertChatMessages(messagesThread2)
    }
}
