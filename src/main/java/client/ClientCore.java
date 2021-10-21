package client;



import exception.DuplicatedUsernameException;
import server.Message;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ClientCore implements Runnable{
    private static String clientName;
    private Socket clientSocket;
    private static ObjectOutputStream os;
    private static ObjectInputStream is;
    private BufferedReader br;
    private LoginController loginController;
    private ChatController chatController;
    public ClientCore(String clientName,int port) throws IOException, DuplicatedUsernameException {
        this.clientName = clientName;
        clientSocket = new Socket("localhost", port);
    }

    @Override
    public void run(){
        try {
            os = new ObjectOutputStream(clientSocket.getOutputStream());
            is = new ObjectInputStream(clientSocket.getInputStream());
            br = new BufferedReader(new InputStreamReader(System.in));
            Message firstMessage = new Message();
            firstMessage.setSender(clientName);
            firstMessage.setReceiver("Server");
            firstMessage.setType(Message.Type.LOGIN);
            os.writeObject(firstMessage);
            os.flush();
            while (clientSocket.isConnected()){
                Message message = null;
                message = (Message) is.readObject();
                if(message != null){
                    switch (message.getType()){
                        case MSG:
                            System.out.println("Message form: "+ message.getSender() + " to " + message.getReceiver());
                            chatController.appendMsg(message);
                            break;
                        case LOGIN:
                            List<String> activeClients = message.getActiveUsers();
                            Iterator iterator = activeClients.iterator();
                            while (iterator.hasNext()){
                                String client = (String) iterator.next();
                                if(client.equals(clientName)){
                                    iterator.remove();
                                }
                            }
                            chatController.updateUserList(activeClients);
                            break;
                    }

                }
            }
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void sendMsg(String msg, String receiver) throws IOException {
        Message newMsg = new Message();
        newMsg.setContent(msg);
        newMsg.setSender(clientName);
        newMsg.setReceiver(receiver);
        newMsg.setType(Message.Type.MSG);
        os.writeObject(newMsg);
        os.flush();
    }
}

