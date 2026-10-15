package ni.edu.uam.proyectocrudg3.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConecction {
    private static final String URL = "jdbc:postgresql://localhost:5432/bdventas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Alvaro12@";

    private DatabaseConecction() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
