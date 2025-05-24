package ru.java.maryan.services.impl;

import com.google.protobuf.ByteString;
import com.securechat.proto.*;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.java.maryan.AsymmetricCryptoAlgs.impl.DiffieHellman;
import ru.java.maryan.file_storage.models.Chat;
import ru.java.maryan.file_storage.models.RoomRequest;
import ru.java.maryan.file_storage.services.RoomFileService;
import ru.java.maryan.services.ChatFxService;
import ru.java.maryan.token_handler.TokenStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ChatFxServiceImpl implements ChatFxService {
    private final ChatServiceGrpc.ChatServiceBlockingStub blockingStub;
    private final ChatServiceGrpc.ChatServiceStub asyncStub;
    private final List<Consumer<RoomConfig>> newChatListeners = new ArrayList<>();
    private final Map<String, CompletableFuture<RoomResponse>> pendingRequests = new ConcurrentHashMap<>();
    private final RoomFileService roomFileService;

    @Autowired
    public ChatFxServiceImpl(@Qualifier("chat-service") Channel channel, RoomFileService roomFileService) {
        this.blockingStub = ChatServiceGrpc.newBlockingStub(channel);
        this.asyncStub = ChatServiceGrpc.newStub(channel);
        this.roomFileService = roomFileService;
    }

    @Override
    public CompletableFuture<RoomResponse> createChat(RoomRequest request) {
        RoomConfig config = roomFileService.createRoomConfig(request);
        DiffieHellman sender = new DiffieHellman();
        config = config.toBuilder()
                .setG(ByteString.copyFrom(sender.getG()))
                .setP(ByteString.copyFrom(sender.getP()))
                .setPublicKey(ByteString.copyFrom(sender.getPublicKey()))
                .build();

        CompletableFuture<RoomResponse> future = new CompletableFuture<>();
        pendingRequests.put(config.getRoomId(), future);

        RoomConfig finalConfig = config;
        asyncStub.startRoom(config, new StreamObserver<RoomResponse>() {
            @Override
            public void onNext(RoomResponse value) {
                if (value.getAccepted()) {
                    try {
                        byte[] receiverPublicKey = value.getPublicKey().toByteArray();
                        sender.createSecretKey(receiverPublicKey);

                        String login = roomFileService.getSubject(TokenStorage.getInstance().getToken());
                        roomFileService.saveRoomConfig(finalConfig, sender.getSharedSecretBytes(), login);

                        future.complete(value);
                    } catch (Exception e) {
                        future.completeExceptionally(new RuntimeException("Key exchange failed", e));
                    }
                } else {
                    future.complete(value);
                }
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {
            }
        });

        return future;
    }

    @Override
    public void subscribeToInvites() {
        UserLogin currentUser = UserLogin.newBuilder()
                .setLogin(roomFileService.getSubject(TokenStorage.getInstance().getToken()))
                .build();

        asyncStub.subscribeInvites(currentUser, new StreamObserver<RoomInvite>() {
            @Override
            public void onNext(RoomInvite invite) {
                handleIncomingInvite(invite.getConfig());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Invite subscription error: " + t.getMessage());
                tryReconnect();
            }

            @Override
            public void onCompleted() {
                System.out.println("Invite subscription completed");
            }
        });
    }

    private void handleIncomingInvite(RoomConfig config) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("New Chat Invitation");
            alert.setHeaderText(String.format(
                    "User %s wants to start a chat (%s/%s/%s)",
                    config.getSenderLogin(),
                    config.getEncryptionAlgorithm(),
                    config.getMode(),
                    config.getPadding()
            ));
            alert.setContentText("Do you want to accept?");

            Optional<ButtonType> result = alert.showAndWait();
            boolean accepted = result.isPresent() && result.get() == ButtonType.OK;

            byte[] receiverPublicKey = new byte[0];
            if (accepted) {
                DiffieHellman receiver = new DiffieHellman(
                        config.getP().toByteArray(),
                        config.getG().toByteArray()
                );
                receiverPublicKey = receiver.getPublicKey();
                receiver.createSecretKey(config.getPublicKey().toByteArray());
                try {
                    String login = roomFileService.getSubject(TokenStorage.getInstance().getToken());
                    roomFileService.saveRoomConfig(config, receiver.getSharedSecretBytes(), login);
                    notifyNewChat(config);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            RoomDecision decision = RoomDecision.newBuilder()
                    .setRoomId(config.getRoomId())
                    .setAccepted(accepted)
                    .setPublicKey(accepted ?
                            ByteString.copyFrom(receiverPublicKey) :
                            ByteString.EMPTY)
                    .setMessage(accepted ? "Accepted" : "Rejected")
                    .build();

            asyncStub.respondToInvite(decision, new StreamObserver<RoomResponse>() {
                @Override
                public void onNext(RoomResponse value) {
                    if (value.getAccepted()) {
                        Platform.runLater(() -> {
                            openChatWindow(config);
                        });
                    }
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Error responding to invite: " + t.getMessage());
                }

                @Override
                public void onCompleted() {
                    System.out.println("Invite response completed");
                }
            });
        });
    }

    @Override
    public void addNewChatListener(Consumer<RoomConfig> listener) {
        newChatListeners.add(listener);
    }

    @Override
    public void removeChatListener(Consumer<RoomConfig> listener) {
        newChatListeners.remove(listener);
    }

    private void notifyNewChat(RoomConfig config) {
        Platform.runLater(() -> {
            newChatListeners.forEach(listener -> listener.accept(config));
        });
    }

    private void openChatWindow(RoomConfig config) {
        System.out.println("Opening chat with " + roomFileService.getSubject(TokenStorage.getInstance().getToken()));
    }

    @Override
    public ObservableList<Chat> getAvailableChats() throws IOException {
        String login = roomFileService.getSubject(TokenStorage.getInstance().getToken());
        List<Chat> chats = roomFileService.loadAllRoomConfigs(login).stream()
                .map(conf -> new Chat(conf.getRoomId(), conf.getReceiverLogin()))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(chats);
    }

    private void tryReconnect() {
        try {
            Thread.sleep(5000);
            subscribeToInvites();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
