package ru.gb.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ClientHandler {

    private static final Logger logger = LogManager.getLogger(ChatServer.class);
    private final Socket socket;
    private final ChatServer server;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String nick;
    private int id;
    private boolean connectok = true;


    public ClientHandler(Socket socket, ChatServer server, ExecutorService executorService) {
        try {
            this.nick = "";
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            executorService.execute(() -> {
                try {
                    authenticate();
                    readMessages();
                } finally {
                    closeConnection();
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            server.unsubscribe(this);
            server.broadcast(nick + " вышел из чата");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final String str = in.readUTF();
                if (str.equals("/end")) {
                    connectok = false;
                    out.writeUTF("/end");
                    break;
                }
                if (str.startsWith("/auth")) {
                    final String[] split = str.split("\\s");
                    final String idAndNick = server.getAuthService().getIdAndNickByLoginAndPassword(split[1], split[2]);
                    if (idAndNick != null) {
                        String[] id_nick = idAndNick.split("\\s");
                        if (!server.isNickBusy(id_nick[1])) {
                            this.id = Integer.parseInt(id_nick[0]);
                            this.nick = id_nick[1];
                            sendMessage("/authok " + id + " " + split[1] + " " + nick);
                            logger.info("Client (id: {}, nick: {}) connected", id, nick);
                            server.subscribe(this);
                            server.broadcast(this.nick + " зашел в чат");
                            break;
                        } else {
                            sendMessage("Учетная запись уже используется");
                            logger.info("Account is already in use");
                        }
                    } else {
                        sendMessage("Неверные логин и пароль");
                        logger.info("Wrong login or password");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    private void readMessages() {
        while (true) {
            try {
                if (!connectok) {
                    break;
                }
                final String msg = in.readUTF();
                if (msg.startsWith("/w")) {
                    logger.info("{} sent command /w", nick);
                    final String[] split = msg.split("\\s");
                    String substring = msg.substring(4 + split[1].length());
                    if (server.isNickBusy(split[1])) {
                        server.privateMsg(nick, "ЛС для " + split[1] + ": " + substring);
                        server.privateMsg(split[1], "ЛС от " + nick + ": " + substring);
                    } else {
                        server.privateMsg(nick, "Участник " + split[1] + " отсутствует в чате");
                    }
                } else if (msg.startsWith("/un")) { //un = update nick
                    logger.info("{} sent command /un", nick);
                    final String[] split = msg.split("\\s");
                    String newNick = split[1];
                    server.getAuthService().updateNick(id, newNick);
                    logger.info("Changing nickname from {} to {}", nick, newNick);
                    server.broadcast(this.nick + " изменил ник на: " + newNick);
                    this.nick = newNick;
                    server.updateClients();
                } else if (msg.equals("/end")) {
                    server.privateMsg(nick, "/end");
                    logger.info("{} sent command /end", nick);
                    break;
                } else {
                    server.broadcast(nick + ": " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Ошибка при чтении сообщения", e);
            }
        }
    }

    public String getNick() {
        return nick;
    }
}
