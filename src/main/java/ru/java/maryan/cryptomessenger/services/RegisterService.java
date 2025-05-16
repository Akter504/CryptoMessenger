package ru.java.maryan.cryptomessenger.services;


import ru.java.maryan.cryptomessenger.dto.request.RegisterRequest;
import ru.java.maryan.cryptomessenger.dto.response.TokenResponse;

public interface RegisterService {
    TokenResponse register(RegisterRequest id);
}
