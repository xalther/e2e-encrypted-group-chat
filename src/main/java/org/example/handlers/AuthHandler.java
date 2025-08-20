package org.example.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.services.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuthHandler implements HttpHandler {
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String method = httpExchange.getRequestMethod();
        if (!"POST".equalsIgnoreCase(method)) {
            sendJsonResponse(httpExchange, 405, Map.of("code", 405, "message", "Method not allowed"));
            return;
        }

        switch (path) {
            case "/register" -> handleRegister(httpExchange);
            case "/login" -> handleLogin(httpExchange);
            default -> sendJsonResponse(httpExchange, 404, Map.of("code", 404, "message", "Not found"));
        }
    }

    private void handleRegister(HttpExchange httpExchange) throws IOException {
        try (InputStream is = httpExchange.getRequestBody()) {
            Map<String, String> body = mapper.readValue(is, Map.class);
            String username = body.get("username");
            String password = body.get("password");
            String publicKey = body.get("publicKey");

            boolean success = authService.registerUser(username, password, publicKey);

            if (success) {
                sendJsonResponse(httpExchange, 201, Map.of("code", 201, "message", "User created successfully"));
            } else {
                sendJsonResponse(httpExchange, 400, Map.of("code", 400, "message", "Registration failed"));
            }
        }
    }

    private void handleLogin(HttpExchange httpExchange) throws IOException {
        try (InputStream is = httpExchange.getRequestBody()) {
            Map<String, String> body = mapper.readValue(is, Map.class);
            String username = body.get("username");
            String password = body.get("password");

            boolean success = authService.loginUser(username, password);

            if (success) {
                sendJsonResponse(httpExchange, 200, Map.of("code", 200, "message", "Login successful"));
            } else {
                sendJsonResponse(httpExchange, 401, Map.of("code", 401, "message", "Invalid credentials"));
            }
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Map<String, Object> response) throws IOException {
        byte[] jsonBytes = mapper.writeValueAsString(response).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonBytes);
        }
    }
}
