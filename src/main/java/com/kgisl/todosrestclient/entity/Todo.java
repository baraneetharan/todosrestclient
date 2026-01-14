package com.kgisl.todosrestclient.entity;

public record Todo(Long id, Long userId, String title, Boolean completed) {
} 