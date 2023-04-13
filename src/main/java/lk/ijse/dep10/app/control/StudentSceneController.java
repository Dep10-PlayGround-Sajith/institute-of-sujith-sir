package lk.ijse.dep10.app.control;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import lk.ijse.dep10.app.db.DBConnection;
import lk.ijse.dep10.app.model.Student;
import lk.ijse.dep10.app.util.Gender;
import lk.ijse.dep10.app.util.Grade;
import lk.ijse.dep10.app.util.Medium;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.*;

public class StudentSceneController {

    public RadioButton rdoFemale;
    public ComboBox<Medium> cmbMedium;
    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewStudent;

    @FXML
    private Button btnSave;

    @FXML
    private ImageView imgPictureView;

    @FXML
    private ToggleGroup rdoGender;

    @FXML
    private RadioButton rdoMale;

    @FXML
    private TableView<Student> tblStudent;

    @FXML
    private ComboBox<Grade> txtGrade;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSearch;
    public Student selectedStudent;

    public void initialize(){
        btnClear.setDisable( true );
        btnDelete.setDisable( true );
        tblStudent.getColumns().get( 0 ).setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        tblStudent.getColumns().get( 1 ).setCellValueFactory( new PropertyValueFactory<>( "name" ) );
        tblStudent.getColumns().get( 2 ).setCellValueFactory( new PropertyValueFactory<>( "grade" ) );
        tblStudent.getColumns().get( 3 ).setCellValueFactory( new PropertyValueFactory<>( "gender" ) );
        tblStudent.getColumns().get( 4 ).setCellValueFactory( new PropertyValueFactory<>( "medium" ) );
        tblStudent.getColumns().get( 5 ).setCellValueFactory( new PropertyValueFactory<>( "imagePicture" ) );

        txtGrade.getItems().addAll(Grade.ONE, Grade.TWO,Grade.THREE,Grade.FOUR,Grade.FIVE,
                Grade.SIX,Grade.SEVEN,Grade.EIGHT,Grade.NINE,Grade.TEN,Grade.ELEVEN);
        cmbMedium.getItems().addAll( Medium.SINHALA,Medium.ENGLISH );


        loadAllStudent();

        tblStudent.getSelectionModel().selectedItemProperty().addListener( (value, old, current) -> {

            selectedStudent=current;
            if (current==null) return;
            if(current!=null){
                txtGrade.setDisable( true );
                cmbMedium.setDisable( true );
                btnDelete.setDisable( false );
                String id = current.getId();
                String name = current.getName();
                Grade grade = current.getGrade();
                Gender gender = current.getGender();
                Medium medium = current.getMedium();

                txtId.setText( id );
                txtName.setText( name );
                txtGrade.setValue( grade );
                cmbMedium.setValue( medium );
                if (gender == Gender.MALE) {
                    rdoGender.selectToggle( rdoMale );
                } else {
                    rdoGender.selectToggle( rdoFemale );
                }


            }
            if (current.getPicture() != null) {
                try {
                    /* Blob -> Java FX Image */
                    InputStream is = current.getPicture().getBinaryStream();
                    Image studentPicture = new Image( is );
                    imgPictureView.setImage( studentPicture );
                } catch (SQLException e) {
                    throw new RuntimeException( e );
                }
            }else {
                btnClear.fire();
            }

        });

        txtSearch.textProperty().addListener( (value, old , current) -> {
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                Statement stm = connection.createStatement();
                String sql = "SELECT * FROM Student " +
                        "WHERE student_id LIKE '%1$s' OR name LIKE '%1$s' OR gender LIKE '%1$s' OR grade LIKE '%1$s' OR medium LIKE '%1$s'";
                // sql = "SELECT * FROM Student WHERE first_name LIKE '%a%'";
                sql = String.format(sql, "%" + current + "%");
                ResultSet rst = stm.executeQuery(sql);
                ObservableList<Student> studentList = tblStudent.getItems();
                studentList.clear();
                while (rst.next()) {
                    String id = rst.getString( "student_id" );
                    String name = rst.getString( "name" );
                    Grade grade = Grade.valueOf( rst.getString( "grade" ) );
                    Medium medium = Medium.valueOf( rst.getString( "medium" ) );
                    Gender gender = Gender.valueOf( rst.getString( "gender" ) );

                    Statement stm2 = connection.createStatement();
                    String sql2 = String.format( "SELECT *FROM Picture WHERE student_id='%s'", id);
                    ResultSet rst1 = stm2.executeQuery(sql2);
                    rst1.next();
                    Blob picture = rst1.getBlob( "picture" );
                    InputStream is = picture.getBinaryStream();
                    Image image = new Image( is );
                    ImageView imageView = new ImageView( image );
                    imageView.setFitHeight( 50 );
                    imageView.setFitWidth( 50 );
                    Student student = new Student( id, name, picture, grade,medium,gender,imageView );
                    studentList.add( student );

                }


            } catch (SQLException e) {
                throw new RuntimeException( e );
            }

        });

        Platform.runLater( btnNewStudent::fire );
    }

    public void loadAllStudent() {
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList<Student> studentList = tblStudent.getItems();
        try {
            Statement stm = connection.createStatement();
            String sql = "SELECT * FROM Student";
            ResultSet rst = stm.executeQuery( sql );

            PreparedStatement stmPicture = connection.prepareStatement( "SELECT * FROM Picture WHERE student_id=?" );
            while (rst.next()){
                String id = rst.getString( "student_id" );
                String name = rst.getString( "name" );
                Grade grade = Grade.valueOf( rst.getString( "grade" ) );
                Medium medium = Medium.valueOf( rst.getString( "medium" ) );
                Gender gender = Gender.valueOf( rst.getString( "gender" ) );

                stmPicture.setString( 1,id );
                ResultSet rstPicture = stmPicture.executeQuery();

                Student student = new Student( id, name, null,grade,medium,gender,null);

                if (rstPicture.next()){
                    Blob picture = rstPicture.getBlob( "picture" );
                    student.setPicture( picture );

                    InputStream is = picture.getBinaryStream();
                    Image image = new Image( is );
                    ImageView imageview = new ImageView( image );
                    student.setImageView(imageview );
                    imageview.setFitHeight( 50 );
                    imageview.setFitWidth( 50 );

                }
                studentList.add( student );

            }
        } catch (SQLException e) {
            new Alert( Alert.AlertType.ERROR,"Student load is failed, try again !" ).showAndWait();
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }



    @FXML
    void btnBrowseOnAction(ActionEvent event) throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Select the Student picture" );
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( "Image Files",
                "*.bmp", "*.jpeg", "*.jpg", "*.gif", "*.png" ) );
        File file = fileChooser.showOpenDialog( btnBrowse.getScene().getWindow() );
        if (file != null) {
            btnClear.setDisable( false );
            Image image = new Image( file.toURI().toURL().toString() );
            imgPictureView.setImage( image );

        }

    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        Image image = new Image( "/image/noImage.png" );
        imgPictureView.setImage( image);
        btnClear.setDisable( true );

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Student selectedStudent = tblStudent.getSelectionModel().getSelectedItem();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement stmPicture = connection
                    .prepareStatement("DELETE FROM Picture WHERE student_id = ?");
            PreparedStatement stmStudent = connection
                    .prepareStatement("DELETE FROM Student WHERE student_id = ?");

            stmPicture.setString(1, selectedStudent.getId());
            stmPicture.executeUpdate();
            stmStudent.setString(1, selectedStudent.getId());
            stmStudent.executeUpdate();

            connection.commit();
            tblStudent.getItems().remove(selectedStudent);
            if (tblStudent.getItems().isEmpty()) btnNewStudent.fire();
        } catch (Throwable e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the student").show();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }



    }

    @FXML
    void btnNewStudentOnAction(ActionEvent event) {
        txtName.getStyleClass().remove("invalid");
        txtGrade.getStyleClass().remove("invalid");
        cmbMedium.getStyleClass().remove("invalid");
        rdoMale.getStyleClass().remove( "invalid" );

        txtId.setDisable( false );
        txtName.setDisable( false );
        txtGrade.setDisable( false );
        cmbMedium.setDisable( false );
        rdoMale.setDisable( false );
        rdoFemale.setDisable( false );
        btnBrowse.setDisable( false );
        btnSave.setDisable( false );

        txtGrade.getSelectionModel().clearSelection();
        cmbMedium.getSelectionModel().clearSelection();
        txtGrade.setPromptText( "Select" );
        cmbMedium.setPromptText( "Select" );
        txtSearch.clear();
        rdoGender.selectToggle( null );
        txtName.clear();
        txtId.setText( "generateID" );
        btnClearOnAction( event );
        txtName.requestFocus();
        tblStudent.getSelectionModel().clearSelection();
        btnDelete.setDisable( true );

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!isDataValid()) return;
        if (selectedStudent == null) {

            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement stmStudent = connection.prepareStatement
                        ( "INSERT INTO Student(student_id, name, grade,medium, gender) VALUES (?,?,?,?,?)" );
                PreparedStatement stmStudentPicture = connection.prepareStatement
                        ( "INSERT INTO Picture(student_id,picture) VALUES (?,?)" );

                connection.setAutoCommit( false );


                txtId.setText( generateID() );
                stmStudent.setString( 1, txtId.getText() );
                stmStudent.setString( 2, txtName.getText() );
                stmStudent.setString( 3, txtGrade.getSelectionModel().getSelectedItem().name() );
                stmStudent.setString( 4, cmbMedium.getSelectionModel().getSelectedItem().name() );
                stmStudent.setString( 5, (rdoGender.getSelectedToggle() == rdoMale ? Gender.MALE : Gender.FEMALE).toString() );
                stmStudent.executeUpdate();

                Blob studentPicture = null;


                Image image = imgPictureView.getImage();
                if (image != null) {
                    /* JavaFX Image -> Blob */
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage( image, null );
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write( bufferedImage, "png", baos );
                    byte[] bytes = baos.toByteArray();
                    studentPicture = new SerialBlob( bytes );


                    stmStudentPicture.setString( 1, txtId.getText() );
                    stmStudentPicture.setBlob( 2, studentPicture );
                    stmStudentPicture.executeUpdate();
                }

                connection.commit();
                ImageView imageView = new ImageView( image );
                imageView.setFitWidth( 50 );
                imageView.setFitHeight( 50 );
                Gender gender =rdoGender.getSelectedToggle() == rdoMale ? Gender.MALE : Gender.FEMALE;
                Student newstudent = new Student( txtId.getText(), txtName.getText(), studentPicture,
                        txtGrade.getSelectionModel().getSelectedItem(),
                        cmbMedium.getSelectionModel().getSelectedItem(),gender,imageView );
                tblStudent.getItems().add( newstudent );
                btnNewStudent.fire();
            } catch (Throwable e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException( ex );
                }
                new Alert( Alert.AlertType.ERROR, "Failed to save the Student" ).show();
                e.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit( true );
                } catch (SQLException e) {
                    throw new RuntimeException( e );
                }
            }
        } else {
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement stmStudent = connection.prepareStatement
                        ( "UPDATE Student SET name=?,grade=?,medium=?,gender=? WHERE student_id=?" );
                PreparedStatement stmPicture = connection.prepareStatement
                        ( "UPDATE Picture SET picture=? WHERE student_id=?" );

                connection.setAutoCommit( false );

                stmStudent.setString( 1, txtName.getText() );
                stmStudent.setString( 5, txtId.getText() );
                stmStudent.setString( 2, txtGrade.getSelectionModel().getSelectedItem().name() );
                stmStudent.setString( 3, cmbMedium.getSelectionModel().getSelectedItem().name() );
                stmStudent.setString( 4, (rdoGender.getSelectedToggle() == rdoMale ? Gender.MALE : Gender.FEMALE).toString());
                stmStudent.executeUpdate();

                Blob studentPicture = null;


                Image image = imgPictureView.getImage();
                if (image != null) {
                    /* JavaFX Image -> Blob */
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage( image, null );
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write( bufferedImage, "png", baos );
                    byte[] bytes = baos.toByteArray();
                    studentPicture = new SerialBlob( bytes );


                    stmPicture.setBlob( 1, studentPicture );
                    stmPicture.setString( 2, txtId.getText() );
                    stmPicture.executeUpdate();
                }

                connection.commit();
                ImageView imageView = new ImageView( image );
                imageView.setFitWidth( 50 );
                imageView.setFitHeight( 50 );
                Student updatedStudent = new Student( txtId.getText(), txtName.getText(), studentPicture,
                        txtGrade.getSelectionModel().getSelectedItem(),
                        cmbMedium.getSelectionModel().getSelectedItem(),(rdoGender.getSelectedToggle() == rdoMale ? Gender.MALE : Gender.FEMALE),
                        imageView );
                ObservableList<Student> studentList = tblStudent.getItems();
                int selectedStudentIndex = studentList.indexOf( selectedStudent );
                studentList.set( selectedStudentIndex, updatedStudent );
                tblStudent.refresh();
                btnNewStudent.fire();
            } catch (Throwable e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException( ex );
                }
                new Alert( Alert.AlertType.ERROR, "Failed to update the Student" ).show();
                e.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit( true );
                } catch (SQLException e) {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    public void tblStudentOnKeyReleased(KeyEvent event) {
        if(event.getCode()== KeyCode.DELETE) btnDelete.fire();

    }
    public boolean isDataValid() {
        boolean dataValid = true;
        if (rdoGender.getSelectedToggle()==null) {
            rdoMale.requestFocus();
            rdoMale.getStyleClass().add( "invalid" );
            dataValid = false;
        }

        if (cmbMedium.getSelectionModel().isEmpty()) {
            cmbMedium.requestFocus();
            cmbMedium.getStyleClass().add( "invalid" );
            dataValid = false;
        }

        if (txtGrade.getSelectionModel().isEmpty()) {
            txtGrade.requestFocus();
            txtGrade.getStyleClass().add( "invalid" );
            dataValid = false;
        }
        String name = txtName.getText().strip();
        if (!name.matches("[A-Za-z[.] ]+")) {
            txtName.requestFocus();
            txtName.selectAll();
            txtName.getStyleClass().add( "invalid" );
            dataValid = false;
        }

        return dataValid;
    }
    public String generateID(){
        String id="Sin-10/S-001";
        String medium="";
        int grade;

        if(tblStudent.getItems().isEmpty()) {
            String[] split = id.split( "-" );
            medium=split[0];
            switch (cmbMedium.getSelectionModel().getSelectedItem()){
                case SINHALA:
                    medium="Sin";
                    break;
                case ENGLISH:
                    medium="Eng";
                    break;
            }


            String[] split1= split[1].split("/");
            grade = Integer.parseInt( split1[0] );
            switch (txtGrade.getSelectionModel().getSelectedItem()){
                case ONE:
                    grade=1;
                    break;
                case TWO:
                    grade=2;
                    break;
                case THREE:
                    grade=3;
                    break;
                case FOUR:
                    grade=4;
                    break;
                case FIVE:
                    grade=5;
                    break;
                case SIX:
                    grade=6;
                    break;
                case SEVEN:
                    grade=7;
                    break;
                case EIGHT:
                    grade=8;
                    break;
                case NINE:
                    grade=9;
                    break;
                case TEN:
                    grade=10;
                    break;
                case ELEVEN:
                    grade=11;
            }
            String newId = String.format( "%s-%02d/S-%03d", medium,grade,1);
            return newId;
        }
        ObservableList<Student> studentList = tblStudent.getItems();

        for (Student student : studentList) {
            id = student.getId();
        }
        String[] split = id.split( "-" );
        medium=split[0];
        switch (cmbMedium.getSelectionModel().getSelectedItem()){
            case SINHALA:
                medium="Sin";
                break;
            case ENGLISH:
                medium="Eng";
                break;
        }


        String[] split1= split[1].split("/");
        grade = Integer.parseInt( split1[0] );
        switch (txtGrade.getSelectionModel().getSelectedItem()){
            case ONE:
                grade=1;
                break;
            case TWO:
                grade=2;
                break;
            case THREE:
                grade=3;
                break;
            case FOUR:
                grade=4;
                break;
            case FIVE:
                grade=5;
                break;
            case SIX:
                grade=6;
                break;
            case SEVEN:
                grade=7;
                break;
            case EIGHT:
                grade=8;
                break;
            case NINE:
                grade=9;
                break;
            case TEN:
                grade=10;
                break;
            case ELEVEN:
                grade=11;
        }
        int i = Integer.parseInt( split[2] );
        String newId = String.format( "%s-%02d/S-%03d", medium,grade,i+1);
        return newId;

    }


}