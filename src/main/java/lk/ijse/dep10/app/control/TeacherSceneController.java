package lk.ijse.dep10.app.control;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import lk.ijse.dep10.app.db.DBConnection;
import lk.ijse.dep10.app.model.Student;
import lk.ijse.dep10.app.model.Teacher;
import lk.ijse.dep10.app.util.Gender;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;

public class TeacherSceneController {

    @FXML
    public Button btnAdd;

    @FXML
    public Button btnBrowse;

    @FXML
    public Button btnClear;

    @FXML
    public Button btnDelete;

    @FXML
    public Button btnNewTeacher;

    @FXML
    public Button btnRemove;

    @FXML
    public Button btnSave;

    @FXML
    public ImageView imgView;

    @FXML
    public RadioButton rdoFemale;

    @FXML
    public RadioButton rdoG10;

    @FXML
    public RadioButton rdoG11;

    @FXML
    public RadioButton rdoG6;

    @FXML
    public RadioButton rdoG7;

    @FXML
    public RadioButton rdoG9;

    @FXML
    public RadioButton rdoMale;

    @FXML
    public TableView<?> tblContact;

    @FXML
    public TableView<Teacher> tblDetails;

    @FXML
    public TextField txtName;

    @FXML
    public TextField txtxCpntact;
    public RadioButton rdoG8;
    public TextField txtGender;
    public ComboBox cmbbox;
    private ToggleGroup TglStatus;


    public void initialize() {
        Image image2 = new Image("/image/noimg.jpg");
        imgView.setImage(image2);
        ObservableList<String> items = FXCollections.observableArrayList("MALE", "FEMALE");
        cmbbox.setItems(items);

        cmbbox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtGender.setText((String) newValue);
            }
        });

        tblDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("teacher_name"));
        tblDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("imgview"));
        tblDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("teacher_contact"));
        tblDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("gender"));


        loadAllTeachers();
        tblDetails.getSelectionModel().selectedItemProperty().addListener((ov, prev, current) -> {
            btnDelete.setDisable(current == null);
            if (current == null) return;


            txtName.setText(current.getTeacher_name());
            txtxCpntact.setText(current.getTeacher_contact());
            txtGender.setText(current.getGender());

            Blob picture = current.getTeacher_picture();
            if (picture != null) {
                try {
                    Image image = new Image(current.getTeacher_picture().getBinaryStream());
                    imgView.setImage(image);
                    btnClear.setDisable(false);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                btnClear.fire();
            }

        });

        Platform.runLater(btnNewTeacher::fire);
    }

    private void loadAllTeachers() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Teacher");
            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM Teacher WHERE teacher_name =?");
            while (rst.next()) {

                String name = rst.getString("teacher_name");
                String contact = rst.getString("teacher_contact");
                String gender = rst.getString("gender");

                Blob picture = null;

                stm2.setString(1, name);
                // stm2.setString(3, contact);
                ResultSet rstPicture = stm2.executeQuery();
                if (rstPicture.next()) {
                    picture = rstPicture.getBlob("teacher_picture");
                }
                Teacher teacher = new Teacher(name, picture, contact, gender);
                tblDetails.getItems().add(teacher);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load students").showAndWait();
            System.exit(1);

        }
    }

    @FXML
    public void btnAddOnAction(ActionEvent event) {

    }

    @FXML
    public void btnBrowseOnAction(ActionEvent event) throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the teacher picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("image files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        if (file != null) {
            Image image = new Image(file.toURI().toURL().toString());
            imgView.setImage(image);
            btnClear.setDisable(false);
        }
    }

    @FXML
    public void btnClearOnAction(ActionEvent event) {
        Image image = new Image("/image/noimg.jpg");
        imgView.setImage(image);
        btnClear.setDisable(true);
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        Teacher selectedTeacher = tblDetails.getSelectionModel().getSelectedItem();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement stmPicture = connection.prepareStatement("DELETE FROM Teacher WHERE teacher_name=?");
            PreparedStatement stmTeacher = connection.prepareStatement("DELETE FROM Teacher WHERE teacher_name=?");
            stmPicture.setString(1, selectedTeacher.getTeacher_name());
            stmPicture.executeUpdate();
            stmTeacher.setString(1, selectedTeacher.getTeacher_name());
            stmTeacher.executeUpdate();

            connection.commit();
            tblDetails.getItems().remove(selectedTeacher);
            if (tblDetails.getItems().isEmpty()) btnNewTeacher.fire();
        } catch (Throwable e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed");
        }


    }

    @FXML
    public void btnNewTeacherOnAction(ActionEvent event) {
        txtName.clear();
        btnClear.fire();
        txtxCpntact.clear();
        txtGender.clear();
        tblDetails.getSelectionModel().clearSelection();
        txtName.requestFocus();

    }

    @FXML
    public void btnRemoveOnAction(ActionEvent event) {

    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {

        if (!isDataValid()) return;
        cmbbox.setOnAction(event2 -> {
            String selectedValue = (String) cmbbox.getValue();
            txtGender.setText(selectedValue);
        });

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Teacher(teacher_name,teacher_picture,teacher_contact,gender) VALUES (?,?,?,?)");

            stm.setString(3, txtxCpntact.getText());
            stm.setString(4, txtGender.getText());
            stm.setString(1, txtName.getText());



            WritableImage writableImage = imgView.snapshot(null, null);


            PixelReader pixelReader = writableImage.getPixelReader();
            int width = (int) writableImage.getWidth();
            int height = (int) writableImage.getHeight();
            WritablePixelFormat<ByteBuffer> format = PixelFormat.getByteBgraInstance();
            byte[] pixels = new byte[width * height * 4];
            pixelReader.getPixels(0, 0, width, height, format, pixels, 0, width * 4);
            Blob imageBlob = new javax.sql.rowset.serial.SerialBlob(pixels);

            System.out.println(imageBlob);
            String name = txtName.getText();
            Image image2 = imgView.getImage();
            Teacher newTeacher = new Teacher(name, imageBlob, txtxCpntact.getText(), txtGender.getText());




                Image image = imgView.getImage();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                ImageIO.write(bufferedImage, "png", bos);

                byte[] bytes = bos.toByteArray();
                Blob picture = new SerialBlob(bytes);
                stm.setBlob(2, picture);
                stm.executeUpdate();
                newTeacher.setTeacher_picture(picture);



            connection.commit();
            tblDetails.getItems().add(newTeacher);
            btnNewTeacher.fire();

        } catch (Throwable e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save").show();

        } finally {
            try {
                connection.setAutoCommit(true);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    public void rdoFemaleOnAction(ActionEvent event) {

    }

    @FXML
    public void rdoG10OnAction(ActionEvent event) {

    }

    @FXML
    public void rdoG11OnAction(ActionEvent event) {

    }

    @FXML
    public void rdoG6OnAction(ActionEvent event) {

    }

    @FXML
    public void rdoG7OnAction(ActionEvent event) {

    }

    @FXML
    public void rdoG8OnAction(ActionEvent event) {

    }

    @FXML
    public void rdoG9OnACtion(ActionEvent event) {

    }

    @FXML
    public void rdoMaleOnAction(ActionEvent event) {

    }
    private boolean isDataValid() {
        boolean dataValid = true;
        String name = txtName.getText().strip();
        String gender =txtGender.getText().strip().toUpperCase();
        System.out.println(gender);
        String contact = txtxCpntact.getText().strip();

        cmbbox.setOnAction(event -> {
            String selectedValue = (String) cmbbox.getValue();
            txtGender.setText(selectedValue);
        });

        txtName.getStyleClass().remove("invalid");
        txtGender.getStyleClass().remove("invalid");
        txtxCpntact.getStyleClass().remove("invalid");

        if (!name.matches("[A-Za-z ]+")) {
            txtName.requestFocus();
            txtName.selectAll();
            dataValid = false;
            txtName.getStyleClass().add("invalid");

        }

        if (!gender.matches("^(MALE|FEMALE)$")) {
            txtGender.requestFocus();
            txtGender.selectAll();
            dataValid = false;
            txtGender.getStyleClass().add("invalid");
        }

        if (!contact.matches("^\\d{3}-\\d{7}$")) {
            txtxCpntact.requestFocus();
            txtxCpntact.selectAll();
            dataValid = false;
            txtxCpntact.getStyleClass().add("invalid");
        }


        return dataValid;


    }


    public void cmbboxOnAction(ActionEvent actionEvent) {


    }
}
