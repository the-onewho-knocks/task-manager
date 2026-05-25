package com.taskmanager.controller;

import com.taskmanager.dto.*;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Auth")
@Tag(name = "Tasks", description = "CRUD operations for tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO requestDTO) {
        return new ResponseEntity<>(taskService.createTask(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    @Operation(summary = "Get all tasks or filter by status")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status) {
        if (status != null) {
            return ResponseEntity.ok(taskService.getTasksByStatus(status));
        }
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO requestDTO) {
        return ResponseEntity.ok(taskService.updateTask(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}