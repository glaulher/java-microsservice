package com.glaulher.service_tasks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TasksController {

  public final TasksRepository tasksRepository;

  public TasksController(TasksRepository tasksRepository) {
    this.tasksRepository = tasksRepository;
  }

  @PostMapping
  ResponseEntity<TasksEntity> createTask(@RequestBody TasksEntity task) {
    return ResponseEntity.ok(tasksRepository.save(task));
  }
}
