package com.kgisl.todosrestclient.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgisl.todosrestclient.entity.Todo;
import com.kgisl.todosrestclient.service.TodoService;

public class TodoControllerMockTest {
    private RestTestClient client;
    private TodoService todoService;
    private Todo testTodo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        // Setup test data
        testTodo = new Todo(1L, 1L, "First Todo", true);

        // Mock service
        todoService = Mockito.mock(TodoService.class);

        // Setup mock behaviors
        Mockito.when(todoService.getAllTodos()).thenReturn(List.of(testTodo));
        Mockito.when(todoService.getTodoById(1L)).thenReturn(testTodo);
        Mockito.when(todoService.getTodosByUserId(1L)).thenReturn(List.of(testTodo));
        Mockito.when(todoService.createTodo(any(Todo.class))).thenReturn(testTodo);
        Mockito.when(todoService.updateTodo(eq(1L), any(Todo.class))).thenReturn(testTodo);
        Mockito.doNothing().when(todoService).deleteTodo(1L);

        // Setup test client
        client = RestTestClient.bindToController(new TodoController(todoService)).build();
    }

    @Test
    public void findAllTodos() {
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
    public void findTodoById() {
        client.get()
                .uri("/api/todos/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .isEqualTo(testTodo)
                .value(t -> {
                    assertEquals(1L, t.id());
                    assertEquals(1L, t.userId());
                    assertEquals("First Todo", t.title());
                    assertEquals(true, t.completed());
                });
    }

    @Test
    public void findTodosByUserId() {
        List<Todo> todos = client.get()
                .uri("/api/todos/user/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Todo>>() {
                })
                .returnResult()
                .getResponseBody();

        assertEquals(1, todos.size());
        assertEquals(1L, todos.get(0).userId());
    }

    @Test
    public void createTodo() throws JsonProcessingException {
        Todo newTodo = new Todo(null, 2L, "New Todo", false);

        client.post()
                .uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newTodo) // Let Spring handle the JSON serialization
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Todo.class)
                .value(todo -> {
                    assertEquals("First Todo", todo.title());
                });
    }

    @Test
    public void updateTodo() throws JsonProcessingException {
        Todo updatedTodo = new Todo(1L, 1L, "Updated Todo", true);

        client.put()
                .uri("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(updatedTodo))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .value(todo -> {
                    assertEquals("First Todo", todo.title()); // Mock returns testTodo
                });
    }

    @Test
    public void deleteTodo() {
        client.delete()
                .uri("/api/todos/1")
                .exchange()
                .expectStatus().isNoContent();

        // Verify the service method was called
        Mockito.verify(todoService, Mockito.times(1)).deleteTodo(1L);
    }
}
