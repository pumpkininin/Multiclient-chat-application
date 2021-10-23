package server;


import javafx.scene.paint.Color;
import sun.net.ConnectionResetException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerCore implements Runnable{
    private static HashMap<String, ObjectOutputStream> osHashMap;// store os for each client
    private static List<String> clients;//list store name of the connected client
    private static ServerSocket serverSocket;
    private ExecutorService executorService;//thread pool
    private int port;
    private ServerController serverController;
    private static AtomicBoolean isRunning = new AtomicBoolean(true);
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
        isRunning.set(true);
        while(isRunning.get()){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                    } catch (IOException e) {
                e.printStackTrace();
            }
            ServerService clientThread = new ServerService(clientSocket);
            executorService.execute(clientThread);
        }
        executorService.shutdownNow();
    }

    class ServerService extends Thread{//handler request from client
        private String clientName;
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
                                clientName = msg.getSender();
                                clients.add(msg.getSender());
                                sendNotify(clients, Message.Type.LOGIN);
                                break;
                            case MSG:
                                System.out.println("Message to "+ msg.getReceiver());
                                sendMsg(msg);
                                break;

                        }

                    }
                }
            } catch (SocketException e){
                osHashMap.remove(clientName);
                clients.remove(clientName);
                serverController.updateStatus(clientName + " has been logout", Color.RED);
                sendNotify(clients, Message.Type.LOGOUT);
                System.out.println(clientName + " has been logout");
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
        private void sendNotify(List<String> clients, Message.Type type){
            Message msg = new Message();
            msg.setSender("SERVER");
            msg.setActiveUsers(clients);
            msg.setType(type);
            System.out.println(clients);
            osHashMap.forEach((key, value) ->{
                try {
                    msg.setReceiver(key);
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
