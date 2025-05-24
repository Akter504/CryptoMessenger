package ru.java.maryan.file_storage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Chat {
    private final String roomId;
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
