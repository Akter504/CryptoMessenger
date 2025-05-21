package ru.java.maryan.file_storage.models.converters;

import com.google.protobuf.ByteString;
import com.securechat.proto.RoomConfig;
import org.springframework.stereotype.Component;
import ru.java.maryan.file_storage.models.LocalRoomConfig;

@Component
public class RoomConfigConverter {
    public LocalRoomConfig convert(RoomConfig protoConfig) {
        return LocalRoomConfig.builder()
                        .roomId(protoConfig.getRoomId())
                        .senderLogin(protoConfig.getSenderLogin())
                        .receiverLogin(protoConfig.getReceiverLogin())
                        .encryptionAlgorithm(protoConfig.getEncryptionAlgorithm())
                        .padding(protoConfig.getPadding())
                        .mode(protoConfig.getMode())
                        .iv(protoConfig.getIv().toByteArray())
                        .delta(protoConfig.getDelta().toByteArray())
                        .g(protoConfig.getG().toByteArray())
                        .p(protoConfig.getP().toByteArray())
                        .publicKey(protoConfig.getPublicKey().toByteArray())
                        .build();
    }

    public RoomConfig convert(LocalRoomConfig localConfig) {
        return RoomConfig.newBuilder()
                .setRoomId(localConfig.getRoomId())
                .setSenderLogin(localConfig.getSenderLogin())
                .setReceiverLogin(localConfig.getReceiverLogin())
                .setEncryptionAlgorithm(localConfig.getEncryptionAlgorithm())
                .setPadding(localConfig.getPadding())
                .setMode(localConfig.getMode())
                .setIv(ByteString.copyFrom(localConfig.getIv()))
                .setDelta(ByteString.copyFrom(localConfig.getDelta()))
                .setG(ByteString.copyFrom(localConfig.getG()))
                .setP(ByteString.copyFrom(localConfig.getP()))
                .setPublicKey(ByteString.copyFrom(localConfig.getPublicKey()))
                .build();
    }
}
