package server;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.Serializable;

public class ServerController implements Serializable {
    @FXML private Button startBtn;
    @FXML private AnchorPane serverView;
    @FXML private TextField tfPort;
    @FXML private TextFlow tfStatus;
    private ServerCore serverCore;

    public void startServer() throws IOException, InterruptedException {
        String port = tfPort.getText();
        updateStatus("Server started at port: " + port);
        serverCore = new  ServerCore(Integer.parseInt(port));
        serverCore.startServer();
        startBtn.setDisable(true);
    }
    public void stopServer(){
        serverCore.stopServer();
    }
    public void updateStatus(String content){
        Text text = new Text(10, 20, content);
        text.setFont(Font.font("System", 20));
        text.setFill(Color.GREEN);
        tfStatus.getChildren().add(text);
    }
}
