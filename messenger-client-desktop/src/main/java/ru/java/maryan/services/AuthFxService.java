package ru.java.maryan.services;

public interface AuthFxService {
    String login(String email, String password);

    String register(String email, String username, String password);
}
