package com.petshop.app.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseTestController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/test-db")
    public String testDatabase() {

        // Crear tabla si no existe
        jdbcTemplate.execute("""
            IF NOT EXISTS (
                SELECT *
                FROM sysobjects
                WHERE name='test_petshop'
                AND xtype='U'
            )
            CREATE TABLE test_petshop (
                id INT IDENTITY(1,1) PRIMARY KEY,
                mensaje VARCHAR(255) NOT NULL
            )
        """);

        // Insertar un registro
        jdbcTemplate.update(
                "INSERT INTO test_petshop (mensaje) VALUES (?)",
                "¡Spring Boot se conectó correctamente!"
        );

        // Obtener cantidad de registros
        Integer cantidad = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM test_petshop",
                Integer.class
        );

        return "Conexión exitosa. Registros en test_petshop: " + cantidad;
    }
}