package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {

    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("../sample.fxml"));
        controller = loader.getController();
        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root, 350, 300));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try {
                controller.dispose();
                Platform.exit();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
