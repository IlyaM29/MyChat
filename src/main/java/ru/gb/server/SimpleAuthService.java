package ru.gb.server;

import java.sql.*;

public class SimpleAuthService implements AuthService {

    private Connection connection;

    public SimpleAuthService() {
    }

    @Override
    public String getIdAndNickByLoginAndPassword(String login, String password) {
        try {
            connect();
            try (PreparedStatement ps = connection.prepareStatement("" +
                    "select id, nick from users where login = ? and password = ?")) {
                ps.setString(1, login);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt(1) + " " + rs.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return null;
    }

    @Override
    public void updateNick(int id, String newNick) {
        try {
            connect();
            try (PreparedStatement ps = connection.prepareStatement("" +
                    "update users set nick = ? where id = ?")) {
                ps.setString(1, newNick);
                ps.setInt(2, id);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
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
