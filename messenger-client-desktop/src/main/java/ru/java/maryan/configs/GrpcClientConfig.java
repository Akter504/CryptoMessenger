package ru.java.maryan.configs;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientConfig {

    @Bean
    public GrpcChannelConfigurer keepAliveClientConfigurer() {
        return (channelBuilder, name) -> {
            if (channelBuilder instanceof ManagedChannelBuilder) {
                ((ManagedChannelBuilder<?>) channelBuilder)
                        .keepAliveTime(30, TimeUnit.SECONDS)
                        .keepAliveTimeout(5, TimeUnit.SECONDS);
            }
        };
    }

    @Bean(name = "chat-service")
    public Channel chatServiceChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
    }
}