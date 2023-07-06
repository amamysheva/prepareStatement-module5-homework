package database.service;

import database.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitService {
    private static final String INIT_DB_FILE = "sql/init_db.sql";

    public void initDatabase() {
        Connection connection = Database.getInstance().getConnection();
        try (Statement statement = connection.createStatement()){
            String sql = Files.readString(Path.of(INIT_DB_FILE));
            statement.executeUpdate(sql);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
