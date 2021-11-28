package ru.gb;

import java.sql.*;

public class App {

    private Connection connection;
    private Statement statement;

    public static void main( String[] args ) {
        final App app = new App();
        try {
            app.connect();
            app.createTable();
//            app.insert("Bob", 65);
//            app.insert("John", 78);
//            app.insert("Tom", 86);
//            app.insert("Bim", 90);
            app.select();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            app.disconnect();
        }
    }

    private void select() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("select * from students")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int score = rs.getInt(3);
                System.out.printf("%d - %s - %d\n", id, name, score);
            }
        }
    }

    private void insert(String name, int score) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "insert into students(name, score) values (?, ?)")) {
            ps.setString(1, name);
            ps.setInt(2, score);
            ps.executeUpdate();
        }
    }

    private void createTable() throws SQLException {
        statement.executeUpdate("" +
                "create table if not exists students(" +
                "  id integer primary key autoincrement," +
                "  name text," +
                "  score integer)");
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:students.db");
        statement = connection.createStatement();
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
