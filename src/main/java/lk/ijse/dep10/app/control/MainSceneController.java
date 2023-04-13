package lk.ijse.dep10.app.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MainSceneController {

    public Button btnAttendance;
    public Button btnPayments;
    public Button btnStudent;
    public Button btnTeacher;
    public void btnAttendanceOnAction(ActionEvent event) {
        try {
           Stage stage= (Stage)btnAttendance.getScene().getWindow();
           stage.setScene(new Scene(new FXMLLoader().load(getClass().getResource("/view/AttendanceScene.fxml"))));
           stage.setTitle("Attendance");
           stage.show();
           stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void btnPaymentsOnActon(ActionEvent event) {
        try {
            Stage stage= (Stage)btnAttendance.getScene().getWindow();
            stage.setScene(new Scene( new FXMLLoader().load(getClass().getResource("/view/PaymentScene.fxml"))));
            stage.setTitle("Payment");
            stage.show();
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void btnStudentOnAction(ActionEvent event){
        try {
            Stage stage = (Stage)btnAttendance.getScene().getWindow();
            stage.setScene(new Scene(new FXMLLoader().load(getClass().getResource("/view/StudentScene.fxml"))));
            stage.setTitle("Student");
            stage.show();
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void btnTeacherOnAction(ActionEvent event) {
        try {
            Stage stage= (Stage)btnAttendance.getScene().getWindow();
            new FXMLLoader();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/TeacherScene.fxml"))));
            stage.setTitle("Teacher");
            stage.show();
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
