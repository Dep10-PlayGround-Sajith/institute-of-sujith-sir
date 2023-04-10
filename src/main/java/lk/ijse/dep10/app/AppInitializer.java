package lk.ijse.dep10.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.dep10.app.db.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        generateSchemaIfNotExist();
        try {
            System.out.println("working");
            primaryStage.setScene(new Scene( FXMLLoader.load(getClass().getResource("/view/MainScene.fxml"))));
            primaryStage.setTitle("Main Window");
            primaryStage.show();
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void generateSchemaIfNotExist() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            List<String> tableArray = new ArrayList<>();
            Statement stm1 = connection.createStatement();
            ResultSet rst = stm1.executeQuery("SHOW TABLES");

            Set tableNameSet = new HashSet<>();

            while (rst.next()) {
                tableNameSet.add(rst.getString(1));
                System.out.println(rst.getString(1));
            }
            boolean tableExists = tableNameSet.containsAll(Set.of("Attendance", "Student", "Teacher", "User"));
            if (!tableExists)stm1.execute(readDBScript());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String readDBScript() {
        System.out.println("readScript Is working");
        StringBuilder dbScript = new StringBuilder();
        try {
            System.out.println("Schema is working on");

            InputStream is = getClass().getResourceAsStream("/schema.sql");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                dbScript.append(line).append("\n");
            }
            br.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dbScript.toString();
    }
}
