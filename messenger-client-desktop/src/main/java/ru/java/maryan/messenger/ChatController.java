package ru.java.maryan.messenger;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import ru.java.maryan.file_storage.models.Chat;

@Component
public class ChatController {

    public void show(Stage stage, Chat chat) {
        stage.setTitle("Chat with " + chat.getName());

        BorderPane root = new BorderPane();

        ListView<String> messagesView = new ListView<>();
        TextField input = new TextField();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(e -> {
            String message = input.getText().trim();
            if (!message.isEmpty()) {
                messagesView.getItems().add("Me: " + message);
                // TODO: Отправка через gRPC
                input.clear();
            }
        });

        VBox inputBox = new VBox(5, input, sendButton);
        inputBox.setPadding(new Insets(10));
        root.setCenter(messagesView);
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
}

