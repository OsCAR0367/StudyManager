package com.example.studymanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.studymanager.ui.theme.StudyManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudyTaskManager(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class StudyTask(
    val id: Int,
    val subject: String,
    val description: String,
    var isCompleted: Boolean = false
)

@Composable
fun StudyTaskManager(modifier: Modifier = Modifier) {
    var tasks by rememberSaveable { mutableStateOf(listOf<StudyTask>()) }
    var subject by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var nextId by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Study Task Manager",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input fields
        TextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (subject.isNotBlank() && description.isNotBlank()) {
                    val newTask = StudyTask(
                        id = nextId,
                        subject = subject,
                        description = description
                    )
                    tasks = tasks + newTask
                    nextId++
                    subject = ""
                    description = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Task counter
        Text(
            text = "Pending Tasks: ${tasks.count { !it.isCompleted }}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Task list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    onCompleteTask = { taskId ->
                        tasks = tasks.map {
                            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted)
                            else it
                        }
                    },
                    onDeleteTask = { taskId ->
                        tasks = tasks.filter { it.id != taskId }
                    }
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: StudyTask,
    onCompleteTask: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.subject,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                IconButton(onClick = { onCompleteTask(task.id) }) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Complete Task",
                        tint = if (task.isCompleted) Color.Green else Color.Gray
                    )
                }
                IconButton(onClick = { onDeleteTask(task.id) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStudyTaskManager() {
    StudyManagerTheme {
        StudyTaskManager()
    }
}