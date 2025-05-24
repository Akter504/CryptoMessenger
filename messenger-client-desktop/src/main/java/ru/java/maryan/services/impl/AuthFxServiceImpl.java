package ru.java.maryan.services.impl;

import org.springframework.stereotype.Service;
import ru.java.maryan.services.AuthFxService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AuthFxServiceImpl implements AuthFxService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String login(String email, String password) {
        try {
            String requestBody = String.format(
                    "{\"email\":\"%s\", \"password\":\"%s\"}",
                    email, password
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/login-by-email"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? response.body() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String register(String email, String username, String password) {
        try {
            String requestBody = String.format(
                    "{\"email\":\"%s\", \"login\":\"%s\", \"password\":\"%s\"}",
                    email, username, password
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/users/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201 ? response.body() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
