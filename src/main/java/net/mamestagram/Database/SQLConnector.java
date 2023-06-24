package net.mamestagram.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnector {

    public static Connection connectToServer() throws SQLException {

        return DriverManager.getConnection(
                "",
                "",
                ""
        );
    }
}
