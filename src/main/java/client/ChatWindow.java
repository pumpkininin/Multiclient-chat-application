package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatWindow extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/ChatView.fxml"));
        primaryStage.setTitle("Client chat window");
        primaryStage.setScene(new Scene(root, 1200, 680));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

}
