package lk.ijse.dep10.app.db;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static DBConnection dbConnection;
    private final Connection connection;

    private DBConnection() {
        Properties configurations = new Properties();
        File confiFile = new File("application.properties");
        String host = configurations.getProperty("sujith.db.host", "localhost");
        String port = configurations.getProperty("sujith.db.port", "3306");
        String database = configurations.getProperty("sujith.db.name","SujithDatabase");
        String username = configurations.getProperty("sujith.db.username", "root");
        String password = configurations.getProperty("sujith.db.password", "mysql");

        String queryString = "createDatabaseIfNotExist=true&allowMultiQueries=true";
        String url = String.format("jdbc:mysql://%s:%s/%s?%s", host, port, database, queryString);
        try {
            FileReader fr = new FileReader(confiFile);
            configurations.load(fr);
            connection = DriverManager.getConnection(url, username, password);

        } catch (FileNotFoundException e)  {
            new Alert(Alert.AlertType.ERROR, "Configuration file does not exists").showAndWait();
            System.exit(1);
            throw new RuntimeException(e);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "FAiled to obtained a database connection,try again.if the problem persist contact developer").showAndWait();
            throw new RuntimeException(e);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to read Configurations").showAndWait();
            throw new RuntimeException(e);
        }
    }
    public static DBConnection getInstance(){
        return (dbConnection==null)? dbConnection=new DBConnection():dbConnection;
    }
    public Connection getConnection(){return connection;}

}
