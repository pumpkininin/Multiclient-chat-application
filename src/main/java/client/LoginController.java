package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class LoginController {
    @FXML private Button btnLogin;
    @FXML private TextField tfUsername;
    @FXML private TextField tfPort;

    private Scene scene;
    public void login() throws IOException {
        String username = tfUsername.getText();
        int port = Integer.parseInt(tfPort.getText());
        Platform.runLater(() -> {
            try{
                FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/views/ChatView.fxml"));
                Parent root = chatLoader.load();
                ChatController chatController = chatLoader.<ChatController>getController();
                chatController.initData(tfUsername.getText());
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setOnCloseRequest((WindowEvent e) -> {
                    Platform.exit();
                    System.exit(0);
                });
                ClientCore clientCore = new ClientCore(username,port, chatController);
                new Thread(clientCore).start();
                this.scene = new Scene(root);
                stage.centerOnScreen();
                stage.setScene(this.scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
