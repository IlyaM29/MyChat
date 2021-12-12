package ru.gb.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final ChatWindow chatWindow;
    private boolean authok = false;
    private boolean connectok = true;
    private String login;

    public ChatClient(ChatWindow chatWindow) {
        this.chatWindow = chatWindow;
        openConnection();
    }

    private void openConnection() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new  DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    Thread.sleep(120000);
                    if (!authok) {
                        try {
                            out.writeUTF("/end");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        connectok = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    while (true) {
                        final String msgAuth;
                        if (in != null) {
                            msgAuth = in.readUTF();
                        } else {
                            break;
                        }
                        if (msgAuth.startsWith("/")) {
                            if (msgAuth.equals("/end")) break;
                            if (msgAuth.startsWith("/authok")) {
                                authok = true;
                                final String[] split = msgAuth.split("\\s");
                                final String id = split[1];
                                login = split[2];
                                final String nick = split[3];

                                printHistory(login);

                                chatWindow.addMessage("Успешная авторизация под ником " + nick);

                                chatWindow.setTitle("Чат (id: " + id + ")");
                                break;
                            }
                        } else {
                            chatWindow.addMessage(msgAuth);
                        }
                    }
                    while (true) {
                        if (!connectok) break;
                        final String message;
                        if (in != null) {
                            message = in.readUTF();
                        } else {
                            break;
                        }
                        if (message.startsWith("/")) {
                            if (message.startsWith("/clients")) {
                                String[] tokens = message.replace("/clients", "").split(" ");
                                List<String> clients = Arrays.asList(tokens);
                                chatWindow.updateClientList(clients);
                            }
                            if ("/end".equals(message)) {
                                break;
                            }
                        } else {
                            chatWindow.addMessage(message);
                            writeHistory(login, message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printHistory(String login) {
        File file = new File("history_" + login + ".txt");
        try {
            System.out.println(file.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> historyMsg = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("history_" + login + ".txt"))) {
            String str;
            for (int i = 0; i < 100; i++) {
                if ((str = reader.readLine()) != null) {
                    historyMsg.add(str);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(historyMsg);
        for (String msg : historyMsg) {
            chatWindow.addMessage(msg);
        }
    }

    private void writeHistory(String login, String message) {
        File file = new File("history_" + login + ".txt");
        List<String> temp = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str;
            while ((str = reader.readLine()) != null) {
                temp.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(message + "\n");
            for (String m : temp) {
                writer.write(m + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
