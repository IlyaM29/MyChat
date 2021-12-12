package ru.gb.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final Logger logger = LogManager.getLogger(ChatServer.class);

    public static final int PORT = 8189;

    private final AuthService authService;
    private final List<ClientHandler> clients;
    private Boolean active = true;

    public ChatServer() {

        this.authService = new SimpleAuthService();
        this.clients = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            logger.info("Server started");
            new Thread(() -> {
                Scanner in = new Scanner(System.in);
                if (in.nextLine().equals("/end")) active = false;
            }).start();
            ExecutorService executorService = Executors.newCachedThreadPool();
            while (active) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, this, executorService);
                logger.info("Client connected");
            }
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
        logger.info("Server stopped");
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
        logger.info("{} subscribe", client.getNick());
        broadcastClientsList();
    }

    public synchronized void updateClients() {
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
        logger.info("{} unsubscribe", client.getNick());
        broadcastClientsList();
    }

    public void broadcastClientsList() {
        StringBuilder clientsCommand = new StringBuilder("/clients ");
        for (ClientHandler client : clients) {
            clientsCommand.append(client.getNick()).append(" ");
        }
        broadcast(clientsCommand.toString());
    }

    public synchronized void broadcast(String msg) {
        for (ClientHandler client : clients) {
            client.sendMessage(msg);
        }
    }

    public synchronized void privateMsg(String nick, String msg) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick)) {
                client.sendMessage(msg);
            }
        }
    }
}
