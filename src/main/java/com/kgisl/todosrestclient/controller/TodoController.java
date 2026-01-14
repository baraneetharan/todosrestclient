package com.kgisl.todosrestclient.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kgisl.todosrestclient.entity.Todo;
import com.kgisl.todosrestclient.service.TodoService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/todos")
// @RequiredArgsConstructor
@Slf4j

public class TodoController {
    // private final Logger log = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Todo>> getTodos() {
        List<Todo> allTodos = todoService.getAllTodos();
        log.info("allTodos.toString()");
        return ResponseEntity.ok(allTodos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> findById(@PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        log.info("todo.toString()");
        return ResponseEntity.ok(todo);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Todo>> getTodosByUserId(@PathVariable Long userId) {
        List<Todo> todos = todoService.getTodosByUserId(userId);
        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        Todo createdTodo = todoService.createTodo(todo);
        return ResponseEntity.ok(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        Todo updatedTodo = todoService.updateTodo(id, todo);
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

}