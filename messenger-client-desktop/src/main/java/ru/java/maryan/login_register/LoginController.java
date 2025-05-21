package ru.java.maryan.login_register;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.java.maryan.messenger.MessengerController;
import ru.java.maryan.services.AuthFxService;
import ru.java.maryan.services.ChatFxService;
import ru.java.maryan.token_handler.TokenStorage;

@Component
public class LoginController {

    private final AuthFxService authService;
    private final ApplicationContext context;

    @Autowired
    public LoginController(AuthFxService authService, ApplicationContext context) {
        this.authService = authService;
        this.context = context;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Login");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label emailLabel = new Label("Email:");
        GridPane.setConstraints(emailLabel, 0, 0);
        TextField emailInput = new TextField();
        GridPane.setConstraints(emailInput, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        PasswordField passwordInput = new PasswordField();
        GridPane.setConstraints(passwordInput, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setOnAction(e -> {
            String token = authService.login(emailInput.getText(), passwordInput.getText());
            if (token != null) {
                TokenStorage.getInstance().setToken(token);
                showAlert("Authorization successful!", Alert.AlertType.INFORMATION);
                primaryStage.close();

                ChatFxService chatFxService = context.getBean(ChatFxService.class);
                chatFxService.subscribeToInvites();

                navigateToMessenger();
            } else {
                showAlert("Authorization failed!");
            }
        });

        Button registerButton = new Button("Register");
        GridPane.setConstraints(registerButton, 1, 3);
        registerButton.setOnAction(e -> {
            primaryStage.close();
            navigateToRegister();
        });

        grid.getChildren().addAll(emailLabel, emailInput, passwordLabel, passwordInput, loginButton, registerButton);

        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String message) {
        showAlert(message, Alert.AlertType.ERROR);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    private void navigateToRegister() {
        RegistrationController registrationController = context.getBean(RegistrationController.class);
        registrationController.show(new Stage());
    }

    private void navigateToMessenger() {
        MessengerController messengerController = context.getBean(MessengerController.class);
        messengerController.show(new Stage());
    }
}

