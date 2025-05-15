package ru.java.maryan.cryptomessenger.controllers.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.maryan.cryptomessenger.controllers.AuthController;
import ru.java.maryan.cryptomessenger.dto.request.LoginRequest;
import ru.java.maryan.cryptomessenger.dto.response.LoginResponse;
import ru.java.maryan.cryptomessenger.dto.response.RoomResponse;
import ru.java.maryan.cryptomessenger.dto.response.TokenResponse;
import ru.java.maryan.cryptomessenger.exceptions.AuthException;
import ru.java.maryan.cryptomessenger.models.Room;
import ru.java.maryan.cryptomessenger.models.User;
import ru.java.maryan.cryptomessenger.services.LoginService;
import ru.java.maryan.cryptomessenger.services.RoomService;
import ru.java.maryan.cryptomessenger.services.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthControllerImpl implements AuthController {
    private final UserService userService;
    private final LoginService loginService;
    private final RoomService roomService;

    @Autowired
    public AuthControllerImpl(UserService userService, LoginService loginService, RoomService roomService) {
        this.userService = userService;
        this.loginService = loginService;
        this.roomService = roomService;
    }

    @PostMapping("/login-by-email")
    public ResponseEntity<LoginResponse> loginByEmail(
            @Validated @RequestBody LoginRequest request) {
        return processLogin(() -> userService.findUserByEmail(request.getEmail()),
                request.getPassword());
    }

    private ResponseEntity<LoginResponse> processLogin(Supplier<Optional<User>> userSupplier,
                                                       String password) {
        User user = userSupplier.get()
                .orElseThrow(() -> new AuthException("Invalid credentials"));
        TokenResponse token = loginService.login(user, password);
        List<Room> rooms = roomService.findRoomsByUserId(user.getId());
        List<RoomResponse> accountsResponse = rooms == null
                ? Collections.emptyList()
                : convertToRoomResponse(rooms);
        LoginResponse response = LoginResponse.builder()
                .rooms(accountsResponse)
                .token(token)
                .build();
        return ResponseEntity.ok(response);
    }

    private List<RoomResponse> convertToRoomResponse(List<Room> rooms) {
        return rooms.stream()
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getUserFirst().getId(),
                        room.getUserSecond().getId(),
                        room.getUserFirst().getLogin(),
                        room.getUserSecond().getLogin(),
                        room.getCryptoAlgorithm(),
                        room.getIsActive(),
                        room.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
