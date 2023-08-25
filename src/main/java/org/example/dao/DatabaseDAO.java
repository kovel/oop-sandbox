package org.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseDAO implements AutoCloseable {
    private final Connection connection;
    public DatabaseDAO() throws SQLException {
        String url = "jdbc:postgresql://localhost/bsu?ssl=false";
        this.connection = DriverManager.getConnection(url);
        this.testConnection();
    }

    private void testConnection() throws SQLException {
        var startedAt = System.currentTimeMillis();
        var ps = this.connection.prepareStatement("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");
        try (var resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                var tableName = resultSet.getString(1);
                System.out.println(tableName);
            }
        }
        System.out.printf("%d ms%n", System.currentTimeMillis() - startedAt);
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
