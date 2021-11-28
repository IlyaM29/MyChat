package ru.gb.server;

import java.sql.*;

public class SimpleAuthService implements AuthService {

    private Connection connection;

    public SimpleAuthService() {
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        try {
            connect();
            try (PreparedStatement ps = connection.prepareStatement("" +
                    "select nick from users where login = ? and password = ?")) {
                ps.setString(1, login);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return null;
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:auth.db");
    }

    public void disconnect() {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
