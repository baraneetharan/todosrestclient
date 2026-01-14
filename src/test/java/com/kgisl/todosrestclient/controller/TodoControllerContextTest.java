package com.kgisl.todosrestclient.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import com.kgisl.todosrestclient.entity.Todo;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // Full Spring context, but NO server by default
public class TodoControllerContextTest {

    @Autowired
    private WebApplicationContext context;

    RestTestClient client;

    @BeforeEach
    public void setup() {
        client = RestTestClient.bindToApplicationContext(context).build();
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
    }

    
    @Test
    void findTodoById() {
        Todo todo = client.get()
                .uri("/api/todos/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("delectus aut autem", todo.title());
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


}