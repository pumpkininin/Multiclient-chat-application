package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                chatController.initData(tfUsername.getText());
                stage.setOnCloseRequest((WindowEvent e) -> {
                    Platform.exit();
                    System.exit(0);
                });
                this.scene = new Scene(root);
                stage.centerOnScreen();
                ClientCore clientCore = new ClientCore(username,port, this, chatController);
                new Thread(clientCore).start();
                stage.setScene(this.scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
