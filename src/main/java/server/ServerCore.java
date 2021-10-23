package server;


import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerCore implements Runnable{
    private static HashMap<String, ObjectOutputStream> osHashMap;// store os for each client
    private static List<String> clients;//list store name of the connected client
    private static ServerSocket serverSocket;
    private ExecutorService executorService;//thread pool
    private int port;
    private ServerController serverController;
    public ServerCore(int port, ServerController serverController) throws IOException, BindException {
        this.port = port;
        this.serverController = serverController;
        serverSocket = new ServerSocket(port);
        executorService = Executors.newCachedThreadPool();
        osHashMap = new HashMap<>();
        clients = new ArrayList<>();
    }
    @Override
    public void run() {
        System.out.println("Waiting for client...");
        while(true){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                    } catch (IOException e) {
                e.printStackTrace();
            }
            ServerService clientThread = new ServerService(clientSocket);
            executorService.execute(clientThread);
        }
    }

    class ServerService extends Thread{//handler request from client
        private Socket clientSocket;
        private ObjectOutputStream os;
        private ObjectInputStream is;

        ServerService(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try{
                os = new ObjectOutputStream(clientSocket.getOutputStream());
                is = new ObjectInputStream(clientSocket.getInputStream());
                while(true){
                    Message msg = (Message) is.readObject();
                    if(msg !=null){
                        switch (msg.getType()){
                            case LOGIN:
                                osHashMap.put(msg.getSender(), os);
                                clients.add(msg.getSender());
                                sendActiveClients(clients);
                                break;
                            case MSG:
                                System.out.println("Message to "+ msg.getReceiver());
                                sendMsg(msg);
                                break;

                        }

                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


        private void sendMsg(Message msg) throws IOException {
            ObjectOutputStream osReceiver = osHashMap.get(msg.getReceiver());
            osReceiver.writeObject(msg);
            osReceiver.flush();
            osReceiver.reset();
        }
        private void sendActiveClients(List<String> clients){
            Message msg = new Message();
            msg.setSender("SERVER");
            msg.setActiveUsers(clients);
            msg.setType(Message.Type.LOGIN);
            System.out.println(clients);
            osHashMap.forEach((key, value) ->{
                try {
                    System.out.println(key);
                    msg.setReceiver(key);
                    System.out.println(value);
                    value.writeObject(msg);
                    value.flush();
                    value.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } );

        }
    }

}
