package com.kgisl.todosrestclient.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.kgisl.todosrestclient.entity.Todo;
import com.kgisl.todosrestclient.service.TodoService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(TodoController.class) // ← This creates a Spring context!
public class TodoControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean // ← Now this works because Spring context exists
    TodoService todoService;

    RestTestClient client;

    @BeforeEach
    public void setup() {
        client = RestTestClient.bindTo(mockMvc).build();
    }

    @Test
    void findAllTodos() {
        when(todoService.getAllTodos()).thenReturn(
                List.of(new Todo(1L, 1L, "First Todo", true)));

        List<Todo> todos = client.get()
                .uri("/api/todos/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Todo>>() {
                })
                .returnResult()
                .getResponseBody();

        assertEquals(1, todos.size());
        assertEquals("First Todo", todos.get(0).title());
    }

    @Test
    void findTodoById() {
        Todo testTodo = new Todo(1L, 1L, "First Todo", true);
        when(todoService.getTodoById(1L)).thenReturn(testTodo);

        Todo todo = client.get()
                .uri("/api/todos/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .returnResult()
                .getResponseBody();

        assertEquals(testTodo, todo);
    }

    @Test
    void findTodosByUserId() {
        when(todoService.getTodosByUserId(1L)).thenReturn(
                List.of(new Todo(1L, 1L, "First Todo", true)));

        List<Todo> todos = client.get()
                .uri("/api/todos/user/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Todo>>() {
                })
                .returnResult()
                .getResponseBody();

        assertEquals(1, todos.size());
        assertEquals("First Todo", todos.get(0).title());
    }

    @Test
    void createTodo() throws Exception {
        Todo newTodo = new Todo(null, 2L, "New Todo", false);
        Todo savedTodo = new Todo(1L, 2L, "New Todo", false); // Note the ID is set here

        // Mock the service call
        when(todoService.createTodo(any(Todo.class))).thenReturn(savedTodo);

        client.post()
                .uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newTodo) // Changed from .content() to .body()
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .value(todo -> {
                    assertEquals(newTodo.title(), todo.title());
                });
    }

    @Test
    void updateTodo() throws Exception {
        Todo updatedTodo = new Todo(1L, 1L, "Updated Todo", true);
        when(todoService.updateTodo(eq(1L), any(Todo.class))).thenReturn(updatedTodo);

        client.put()
                .uri("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedTodo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .value(todo -> {
                    assertEquals(updatedTodo.title(), todo.title());
                });
    }

    @Test
    void deleteTodo() {
        client.delete()
                .uri("/api/todos/1")
                .exchange()
                .expectStatus().isNoContent();
    }

}