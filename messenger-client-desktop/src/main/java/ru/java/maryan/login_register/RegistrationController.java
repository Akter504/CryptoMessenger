package ru.java.maryan.login_register;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.java.maryan.services.AuthFxService;
import ru.java.maryan.token_handler.TokenStorage;

@Component
public class RegistrationController {
    private final AuthFxService authService;
    private final ApplicationContext context;

    @Autowired
    public RegistrationController(AuthFxService authService, ApplicationContext context) {
        this.authService = authService;
        this.context = context;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Registration");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label emailLabel = new Label("Email:");
        GridPane.setConstraints(emailLabel, 0, 0);
        TextField emailInput = new TextField();
        GridPane.setConstraints(emailInput, 1, 0);

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 1);
        TextField usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 1, 1);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 2);
        PasswordField passwordInput = new PasswordField();
        GridPane.setConstraints(passwordInput, 1, 2);

        Label confirmPasswordLabel = new Label("Confirm Password:");
        GridPane.setConstraints(confirmPasswordLabel, 0, 3);
        PasswordField confirmPasswordInput = new PasswordField();
        GridPane.setConstraints(confirmPasswordInput, 1, 3);

        Button registerButton = new Button("Register");
        GridPane.setConstraints(registerButton, 1, 4);
        registerButton.setOnAction(e -> {
            if (!passwordInput.getText().equals(confirmPasswordInput.getText())) {
                showAlert("Passwords don't match!");
                return;
            }

            String token = authService.register(
                    emailInput.getText(),
                    usernameInput.getText(),
                    passwordInput.getText()
            );

            if (token != null) {
                TokenStorage.getInstance().setToken(token);
                showAlert("Registration successful!", Alert.AlertType.INFORMATION);
                primaryStage.close();
            } else {
                showAlert("Registration failed!");
            }
        });

        Button backToLoginButton = new Button("Back to Login");
        GridPane.setConstraints(backToLoginButton, 1, 5);
        backToLoginButton.setOnAction(e -> {
            primaryStage.close();
            navigateToLogin();
        });

        grid.getChildren().addAll(emailLabel, emailInput, usernameLabel, usernameInput, passwordLabel, passwordInput,
                confirmPasswordLabel, confirmPasswordInput, registerButton, backToLoginButton);

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

    private void navigateToLogin() {
        LoginController loginController = context.getBean(LoginController.class);
        loginController.show(new Stage());
    }
}
