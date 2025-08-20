package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.database.Database;
import org.example.handlers.AuthHandler;
import org.example.repositories.AuthRepository;
import org.example.services.AuthService;
import org.example.utils.PasswordEncoder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) throws IOException {
        var repository = new AuthRepository();
        var passwordEncoder = new PasswordEncoder();
        var authService = new AuthService(passwordEncoder, repository);
        var authHandler = new AuthHandler(authService);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", authHandler);
        server.setExecutor(null);
        server.start();
        System.out.println("Server runs on localhost");

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/sql/create_users_table.sql")));

            stmt.execute(sql);
            System.out.println("Users table created");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


