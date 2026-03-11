package br.unisanta.apptodolist

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    var status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskStatus { PENDING, COMPLETED }
