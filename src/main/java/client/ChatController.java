package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import server.Message;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML private Button btnSend;
    @FXML private TextArea txMsg;
    @FXML private ListView listView;
    @FXML private ListView listMsg;
    @FXML private Label usernameLbl;
    private String receiver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(listView.getSelectionModel().getSelectedItem() != null) {
                    receiver = (String) listView.getSelectionModel().getSelectedItem();
                }

            }
        });

    }
    public void updateUserList(List<String> users) {
        ObservableList<String> observableList = FXCollections.observableList(users);
        System.out.println(observableList);
        listView.setItems(observableList);
        listView.setCellFactory(new ItemRenderer());
    }
    public void sendMsg() throws IOException {
        if(listView.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You need to choose an user first!");
            alert.showAndWait();
        }else{
            String msg = txMsg.getText();
            if(!txMsg.getText().isEmpty()) {;
                ClientCore.sendMsg(msg, receiver);
                Message message = new Message();
                message.setSender(usernameLbl.getText());
                message.setReceiver(receiver);
                message.setContent(msg);
                appendMsg(message);
            }
            txMsg.clear();
        }

    }

    public synchronized void appendMsg(Message message) {
        Task<VBox> msgFromReceiver = new Task<VBox>() {
            @Override
            protected VBox call() throws Exception {
                VBox vb = new VBox();
                Label senderLbl = new Label("From: "+ message.getSender());
                Label msgLbl = new Label(message.getContent());
                msgLbl.setFont(Font.font("System", 16));
                msgLbl.getStyleClass().add("chat-bubble");
                vb.getChildren().addAll(senderLbl,msgLbl);
                vb.setAlignment(Pos.TOP_LEFT);
                return vb;
            }
        };
        msgFromReceiver.setOnSucceeded(event -> {
            listMsg.getItems().add(msgFromReceiver.getValue());
        });
        Task<VBox> yourMsg = new Task<VBox>() {
            @Override
            protected VBox call() throws Exception {
                VBox vb = new VBox();
                Label senderLbl = new Label("To: "+ message.getReceiver());
                Label msgLbl = new Label(message.getContent());
                msgLbl.setFont(Font.font("System", 16));
                msgLbl.getStyleClass().add("chat-bubble");
                vb.getChildren().addAll(senderLbl,msgLbl);
                vb.setAlignment(Pos.TOP_RIGHT);
                return vb;
            }
        };
        yourMsg.setOnSucceeded(event -> {
            listMsg.getItems().add(yourMsg.getValue());
        });
        if(message.getSender().equals(usernameLbl.getText())){
            Thread thread = new Thread(yourMsg);
            thread.setDaemon(true);
            thread.start();
        }else{
            Thread thread = new Thread(msgFromReceiver);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void initData(String selectedItem) {
        usernameLbl.setText(selectedItem);
    }

}
