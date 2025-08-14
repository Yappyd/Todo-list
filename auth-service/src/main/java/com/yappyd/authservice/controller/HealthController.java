package com.yappyd.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "Эндпоинт проверки работоспособности сервиса авторизации")
@RestController
public class HealthController {

    @Operation(summary = "Проверка работы сервиса авторизации", description = "Возвращает статус сервиса авторизации, если он запущен")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}