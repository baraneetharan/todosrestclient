package com.kgisl.todosrestclient.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.kgisl.todosrestclient.entity.Todo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoControllerServerTest {

    @LocalServerPort
    private int port;

    private RestTestClient client;

    @BeforeEach
    public void setup() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void findAllTodos() {
        List<Todo> todos = client.get()
                .uri("/api/todos/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Todo>>() {})
                .returnResult()
                .getResponseBody();

        assertEquals(200, todos.size());
        assertEquals("delectus aut autem", todos.get(0).title());
        assertFalse(todos.get(0).completed());
    }

    @Test
    void findTodoById() {
        client.get()
                .uri("/api/todos/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .value(todo -> {
                    assertEquals(1L, todo.id());
                    assertEquals("delectus aut autem", todo.title());
                    assertFalse(todo.completed());
                    assertEquals(1L, todo.userId());
                });
    }

    @Test
    void createTodo() {
        Todo newTodo = new Todo(null, 1L, "New Todo", false);

        Todo createdTodo = client.post()
                .uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newTodo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .returnResult()
                .getResponseBody();

        assertEquals(newTodo.userId(), createdTodo.userId());
        assertEquals(newTodo.title(), createdTodo.title());
        assertEquals(newTodo.completed(), createdTodo.completed());
    }

    @Test
    void updateTodo() {
        Todo updatedTodo = new Todo(1L, 1L, "Updated Todo", true);

        Todo updated = client.put()
                .uri("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedTodo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .returnResult()
                .getResponseBody();

        assertEquals(updatedTodo.title(), updated.title());
        assertEquals(updatedTodo.completed(), updated.completed());
    }

    @Test
    void deleteTodo() {
        client.delete()
                .uri("/api/todos/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void findTodosByUserId() {
        List<Todo> todos = client.get()
                .uri("/api/todos/user/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Todo>>() {})
                .returnResult()
                .getResponseBody();

        assertEquals(20, todos.size());
        assertEquals("delectus aut autem", todos.get(0).title());
        assertFalse(todos.get(0).completed());
    }

    @Test
    void testServerIsActuallyRunning() {
        // Verify the port is actually set (server is running)
        assertTrue(port > 0, "Server should be running on a port");
        assertNotEquals(8080, port, "Should be random port, not default");
    }
}