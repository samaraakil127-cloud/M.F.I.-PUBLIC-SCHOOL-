package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.entity.StudentEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.ScholasticAmber
import com.example.ui.theme.ScholasticAmberLight
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

@Composable
fun AttendanceScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val attendanceRecords by viewModel.attendanceForDate.collectAsStateWithLifecycle()
    val selectedGrade by viewModel.selectedAttendanceGrade.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val grades = listOf("Grade 10A", "Grade 8A", "Grade 9B", "Grade 11A", "Grade 12B", "All")

    val filteredStudents = remember(students, selectedGrade) {
        if (selectedGrade == "All") students
        else students.filter { it.grade == selectedGrade }
    }

    // Map studentId -> Attendance Status
    val studentAttendanceMap = remember(attendanceRecords) {
        attendanceRecords.associate { it.studentId to it.status }
    }

    val presentCount = filteredStudents.count {
        val st = studentAttendanceMap[it.id] ?: "PRESENT"
        st == "PRESENT"
    }
    val absentCount = filteredStudents.count {
        val st = studentAttendanceMap[it.id]
        st == "ABSENT"
    }
    val lateCount = filteredStudents.count {
        val st = studentAttendanceMap[it.id]
        st == "LATE"
    }
    val excusedCount = filteredStudents.count {
        val st = studentAttendanceMap[it.id]
        st == "EXCUSED"
    }
    val totalCount = filteredStudents.size
    val attendanceRate = if (totalCount > 0) ((presentCount + lateCount).toFloat() / totalCount) * 100f else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("attendance_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Date & Header Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Roll Call & Attendance",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Official Daily Student Register",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable { /* Select Date */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedDate,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Grade / Class Filter Chips
        item {
            Column {
                Text(
                    text = "Select Class / Section",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    grades.forEach { grade ->
                        val isSelected = selectedGrade == grade
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) SchoolBluePrimary else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) SchoolBluePrimary else Slate200,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setSelectedAttendanceGrade(grade) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grade,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Slate800
                            )
                        }
                    }
                }
            }
        }

        // Attendance Realtime Stats Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = "$selectedGrade Status",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${String.format("%.1f", attendanceRate)}% Present",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (attendanceRate >= 85f) StatusGreen else StatusRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { attendanceRate / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = StatusGreen,
                        trackColor = StatusRedLight
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AttendanceStatPill(count = presentCount, label = "Present", color = StatusGreen, bgColor = StatusGreenLight)
                        AttendanceStatPill(count = absentCount, label = "Absent", color = StatusRed, bgColor = StatusRedLight)
                        AttendanceStatPill(count = lateCount, label = "Late", color = ScholasticAmber, bgColor = ScholasticAmberLight)
                        AttendanceStatPill(count = excusedCount, label = "Excused", color = StatusPurple, bgColor = StatusPurpleLight)
                    }
                }
            }
        }

        // Bulk Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val gradeToMark = if (selectedGrade == "All") "Grade 10A" else selectedGrade
                        viewModel.markAllPresentForGrade(gradeToMark)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mark_all_present_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark All Present", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = { viewModel.notifyParentsOfAbsentees(selectedGrade) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("notify_parents_absentees_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Notify Parents", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Student Roster
        item {
            Text(
                text = "Student Roster (${filteredStudents.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (filteredStudents.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No Students in $selectedGrade",
                    message = "Switch grade filter or enroll new students from Directory."
                )
            }
        } else {
            items(filteredStudents, key = { it.id }) { student ->
                val currentStatus = studentAttendanceMap[student.id] ?: "PRESENT"
                StudentAttendanceRowItem(
                    student = student,
                    currentStatus = currentStatus,
                    onStatusChange = { newStatus ->
                        viewModel.toggleStudentAttendance(student, newStatus)
                    }
                )
            }
        }
    }
}

@Composable
private fun AttendanceStatPill(
    count: Int,
    label: String,
    color: Color,
    bgColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
private fun StudentAttendanceRowItem(
    student: StudentEntity,
    currentStatus: String,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_attendance_row_${student.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(student.avatarColorHex))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${student.rollNumber} • Parent: ${student.parentName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Toggle Buttons: P, A, L, E
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AttendanceToggleBadge(
                    letter = "P",
                    isSelected = currentStatus == "PRESENT",
                    activeColor = StatusGreen,
                    onClick = { onStatusChange("PRESENT") }
                )
                AttendanceToggleBadge(
                    letter = "A",
                    isSelected = currentStatus == "ABSENT",
                    activeColor = StatusRed,
                    onClick = { onStatusChange("ABSENT") }
                )
                AttendanceToggleBadge(
                    letter = "L",
                    isSelected = currentStatus == "LATE",
                    activeColor = ScholasticAmber,
                    onClick = { onStatusChange("LATE") }
                )
                AttendanceToggleBadge(
                    letter = "E",
                    isSelected = currentStatus == "EXCUSED",
                    activeColor = StatusPurple,
                    onClick = { onStatusChange("EXCUSED") }
                )
            }
        }
    }
}

@Composable
private fun AttendanceToggleBadge(
    letter: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isSelected) activeColor else Slate200.copy(alpha = 0.5f),
        label = "att_toggle_bg"
    )
    val textColor = if (isSelected) Color.White else Slate500

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}
