package ru.gb.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ChatClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final ChatWindow chatWindow;
    private boolean authok = false;
    private boolean connectok = true;

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
                        final String msgAuth = in.readUTF();
                        if (msgAuth.equals("/end")) break;
                        if (msgAuth.startsWith("/authok")) {
                            authok = true;
                            final String[] split = msgAuth.split("\\s");
                            final String nick = split[1];
                            chatWindow.addMessage("Успешная авторизация под ником " + nick);
                            chatWindow.setTitle("Чат (" + nick + ")");
                            break;
                        } else {
                            chatWindow.addMessage(msgAuth);
                        }
                    }
                    while (true) {
                        if (!connectok) break;
                        final String message = in.readUTF();
                        if (message.startsWith("/")) {
                            if (message.startsWith("/clients")) {
                                String[] tokens = message.replace("/clients", "").split(" ");
                                List<String> clients = Arrays.asList(tokens);
                                chatWindow.updateClientList(clients);
                            }
                            if ("/end".equals(message)) {
                                break;
                            }
                        }
                        chatWindow.addMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
//                    System.exit(0);
                }
            }).start();
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
