package ru.java.maryan.services;

import com.securechat.proto.RoomConfig;
import com.securechat.proto.RoomResponse;
import javafx.collections.ObservableList;
import ru.java.maryan.file_storage.models.Chat;
import ru.java.maryan.file_storage.models.RoomRequest;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface ChatFxService {
    CompletableFuture<RoomResponse> createChat(RoomRequest request);

    void subscribeToInvites();

    void addNewChatListener(Consumer<RoomConfig> listener);

    ObservableList<Chat> getAvailableChats() throws IOException;

    void removeChatListener(Consumer<RoomConfig> listener);
}
