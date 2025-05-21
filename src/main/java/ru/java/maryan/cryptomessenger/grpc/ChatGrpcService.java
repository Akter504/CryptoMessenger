package ru.java.maryan.cryptomessenger.grpc;


import com.securechat.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.java.maryan.cryptomessenger.services.RoomService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@GrpcService
public class ChatGrpcService extends ChatServiceGrpc.ChatServiceImplBase {
    private final Map<String, StreamObserver<RoomInvite>> inviteSubscribers = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<RoomResponse>> pendingRoomFutures = new ConcurrentHashMap<>();
    private final Set<String> activeRoomRequests = ConcurrentHashMap.newKeySet();
    private final Map<String, StreamObserver<EncryptedMessage>> observers = new ConcurrentHashMap<>();
    private final RoomService roomService;

    @Autowired
    public ChatGrpcService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public void subscribeInvites(UserLogin request, StreamObserver<RoomInvite> responseObserver) {
        String login = request.getLogin();
        inviteSubscribers.put(login, responseObserver);

    }

    @Override
    public void startRoom(RoomConfig config, StreamObserver<RoomResponse> responseObserver) {
        String roomId = config.getRoomId();
        String receiverLogin = config.getReceiverLogin();
        if (receiverLogin.isEmpty()) {
            responseObserver.onNext(RoomResponse.newBuilder()
                    .setAccepted(false)
                    .setMessage("User not found")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        if (!inviteSubscribers.containsKey(receiverLogin)) {
            responseObserver.onNext(RoomResponse.newBuilder()
                    .setAccepted(false)
                    .setMessage("User not online")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        if (!activeRoomRequests.add(roomId)) {
            responseObserver.onNext(RoomResponse.newBuilder()
                    .setAccepted(false)
                    .setMessage("Room is already pending")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        try {
            roomService.createRoom(config);
        } catch (ObjectOptimisticLockingFailureException e) {
            System.out.println("Room already deleted by another process, safe to ignore");
        }

        inviteSubscribers.get(receiverLogin).onNext(
                RoomInvite.newBuilder().setConfig(config).build()
        );

        CompletableFuture<RoomResponse> future = new CompletableFuture<>();
        pendingRoomFutures.put(roomId, future);

        future.orTimeout(15, TimeUnit.SECONDS)
                .whenComplete((resp, ex) -> {
                    try {
                        if (ex != null || (resp != null && !resp.getAccepted())) {
                            roomService.deleteRoom(UUID.fromString(roomId));
                        } else {
                            responseObserver.onNext(resp);
                        }
                    } catch (ObjectOptimisticLockingFailureException e) {
                        System.out.println("Room already deleted by another process, safe to ignore");
                    } finally {
                        responseObserver.onCompleted();
                        activeRoomRequests.remove(roomId);
                        pendingRoomFutures.remove(roomId);
                    }
                });
    }

    @Override
    public void respondToInvite(RoomDecision decision, StreamObserver<RoomResponse> responseObserver) {
        String roomId = decision.getRoomId();

        CompletableFuture<RoomResponse> future = pendingRoomFutures.get(roomId);
        if (future == null) {
            responseObserver.onNext(RoomResponse.newBuilder()
                    .setAccepted(false)
                    .setMessage("No pending invite found")
                    .build());
        } else {
            RoomResponse resp = RoomResponse.newBuilder()
                    .setAccepted(decision.getAccepted())
                    .setPublicKey(decision.getPublicKey())
                    .setMessage(decision.getMessage())
                    .build();
            future.complete(resp);
            responseObserver.onNext(resp);
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<EncryptedMessage> chat(StreamObserver<EncryptedMessage> responseObserver) {
        return new StreamObserver<EncryptedMessage>() {
            String roomId;
            String senderId;

            @Override
            public void onNext(EncryptedMessage message) {
                roomId = message.getRoomId();
                senderId = message.getSenderId();

                registerObserver(roomId, senderId, responseObserver);

                String receiverId = getOtherUser(roomId, senderId);
                StreamObserver<EncryptedMessage> receiverObserver = getObserver(roomId, receiverId);

                if (receiverObserver != null) {
                    receiverObserver.onNext(message);
                } else {
                    System.out.println("Receiver not connected.");
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Stream error from " + senderId + ": " + t.getMessage());
                unregisterObserver(roomId, senderId);
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed from " + senderId);
                unregisterObserver(roomId, senderId);
                responseObserver.onCompleted();
            }
        };
    }

    private void registerObserver(String roomId, String userId, StreamObserver<EncryptedMessage> observer) {
        observers.put(composeKey(roomId, userId), observer);
    }

    private void unregisterObserver(String roomId, String userId) {
        observers.remove(composeKey(roomId, userId));
    }

    private StreamObserver<EncryptedMessage> getObserver(String roomId, String userId) {
        return observers.get(composeKey(roomId, userId));
    }

    private String getOtherUser(String roomId, String senderId) {
        return roomService.findOtherUser(UUID.fromString(roomId), UUID.fromString(senderId)).toString();
    }

    private String composeKey(String roomId, String userId) {
        return roomId + "_" + userId;
    }
}
