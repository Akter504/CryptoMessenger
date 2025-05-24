package ru.java.maryan.messenger;


import com.securechat.proto.RoomConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.java.maryan.file_storage.models.Chat;
import ru.java.maryan.file_storage.models.RoomRequest;
import ru.java.maryan.services.ChatFxService;
import ru.java.maryan.token_handler.TokenStorage;

import java.io.IOException;

@Component
public class MessengerController {
    private final ChatFxService chatService;
    private final ApplicationContext context;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ListView<Chat> chatListView;

    @Autowired
    public MessengerController(ChatFxService chatService, ApplicationContext context) {
        this.chatService = chatService;
        this.context = context;
        chatService.addNewChatListener(this::handleNewChat);
    }

    public void show(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Crypto Messenger");
        primaryStage.setOnCloseRequest(e -> {
            chatService.removeChatListener(this::handleNewChat);
        });
        initRootLayout();
        showChatList();
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        fileMenu.getItems().add(exitItem);
        menuBar.getMenus().add(fileMenu);

        rootLayout.setTop(menuBar);

        Button createChatButton = new Button("Create Chat");
        createChatButton.setOnAction(e -> showCreateChatDialog());

        HBox bottomBar = new HBox(createChatButton);
        bottomBar.setPadding(new Insets(10));
        rootLayout.setBottom(bottomBar);

        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showChatList() {
        chatListView = new ListView<>();
        try {
            chatListView.setItems(chatService.getAvailableChats());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        chatListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Chat chat, boolean empty) {
                super.updateItem(chat, empty);
                if (empty || chat == null) {
                    setText(null);
                } else {
                    setText(chat.getName());
                }
            }
        });

        chatListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Chat selected = chatListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openChat(selected);
                }
            }
        });

        rootLayout.setCenter(chatListView);
    }

    private void showCreateChatDialog() {
        Stage dialogStage = new Stage();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        TextField receiverField = new TextField();
        ComboBox<String> algorithmCombo = new ComboBox<>(FXCollections.observableArrayList(
                "TwoFish", "Camelia"
        ));

        ComboBox<String> cipherCombo = new ComboBox<>(FXCollections.observableArrayList(
                "ECB", "CBC", "PCBC", "CFB", "OFB", "CTR", "RD"
        ));

        ComboBox<String> paddingCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Zeros", "ANSI X.923", "ISO 10126", "PKCS7"
        ));

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            RoomRequest request = new RoomRequest(
                    TokenStorage.getInstance().getToken(),
                    receiverField.getText(),
                    algorithmCombo.getValue(),
                    paddingCombo.getValue(),
                    cipherCombo.getValue()
            );

            chatService.createChat(request).thenAccept(response -> {
                Platform.runLater(() -> {
                    dialogStage.close();
                    if (response.getAccepted()) {
                        showChatList();
                    } else {
                        String reason = response.getMessage() != null ?
                                "Chat was rejected. Reason: " + response.getMessage() :
                                "Chat was rejected by the other user";
                        new Alert(Alert.AlertType.WARNING, reason).showAndWait();
                    }
                });
            }).exceptionally(ex -> {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR,
                            "Error creating chat: " + ex.getMessage()).showAndWait();
                });
                return null;
            });
        });

        grid.add(new Label("Receiver:"), 0, 0);
        grid.add(receiverField, 1, 0);
        grid.add(new Label("Algorithm:"), 0, 1);
        grid.add(algorithmCombo, 1, 1);
        grid.add(new Label("Padding:"), 0, 2);
        grid.add(paddingCombo, 1, 2);
        grid.add(new Label("Cipher Mode:"), 0, 3);
        grid.add(cipherCombo, 1, 3);
        grid.add(createButton, 1, 4);

        Scene scene = new Scene(grid, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private void openChat(Chat chat) {
        ChatController chatController = context.getBean(ChatController.class);
        chatController.show(new Stage(), chat);
    }

    private void handleNewChat(RoomConfig config) {
        Platform.runLater(() -> {
            try {
                Chat newChat = new Chat(config.getRoomId(), config.getReceiverLogin());
                ObservableList<Chat> chats = chatListView.getItems();
                if (chats.stream().noneMatch(c -> c.getRoomId().equals(newChat.getRoomId()))) {
                    chats.add(newChat);
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed to add new chat").showAndWait();
            }
        });
    }
}
