package ru.java.maryan.cryptomessenger.services;


import ru.java.maryan.cryptomessenger.dto.response.TokenResponse;
import ru.java.maryan.cryptomessenger.exceptions.AuthException;
import ru.java.maryan.cryptomessenger.models.User;

public interface LoginService {
    TokenResponse login(User user, String password) throws AuthException;
}
