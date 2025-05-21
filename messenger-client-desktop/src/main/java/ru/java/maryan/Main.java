package ru.java.maryan;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.java.maryan.configs.MessengerAppConfig;
import ru.java.maryan.login_register.LoginController;
import ru.java.maryan.login_register.RegistrationController;

import java.util.concurrent.CountDownLatch;

import static javafx.application.Application.launch;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static ConfigurableApplicationContext context;
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        // Сначала инициализируем Spring
        context = new AnnotationConfigApplicationContext(MessengerAppConfig.class);

        // Затем запускаем JavaFX
        new Thread(() -> Application.launch(JavaFXApp.class)).start();
    }

    public static class JavaFXApp extends Application {
        @Override
        public void init() {
            latch.countDown(); // Разрешаем продолжение после инициализации Spring
        }

        @Override
        public void start(Stage primaryStage) {
            try {
                latch.await(); // Ждем инициализации Spring
                RegistrationController controller = context.getBean(RegistrationController.class);
                controller.show(new Stage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to initialize application", e);
            }
        }
    }
}