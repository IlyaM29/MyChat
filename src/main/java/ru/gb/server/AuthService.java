package ru.gb.server;

public interface AuthService {
    String getIdAndNickByLoginAndPassword(String login, String password);
    void updateNick(int id, String newNick);
}
