package ru.java.maryan.cryptomessenger.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {
    private UUID id;
    private UUID userFirstId;
    private UUID userSecondId;
    private String userFirstLogin;
    private String userSecondLogin;
    private String cryptoAlgorithm;
    private boolean isActive;
    private LocalDateTime createdAt;
}
