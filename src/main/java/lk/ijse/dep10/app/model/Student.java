package lk.ijse.dep10.app.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lk.ijse.dep10.app.util.Gender;
import lk.ijse.dep10.app.util.Grade;
import lk.ijse.dep10.app.util.Medium;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
@Data
@AllArgsConstructor
public class Student {
    private String id;
    private String name;
    private Blob picture;
    private Grade grade;
    private Medium medium;
    private Gender gender;
    private ImageView imageView;

    public ImageView getImagePicture() {
        ImageView imageview;
        if (picture == null) {
            imageview = new ImageView( "/image/noImage.png" );
            imageview.setFitWidth( 30 );
            imageview.setFitHeight( 30 );
            return imageview;
        }

        try {
            InputStream is = this.picture.getBinaryStream();
            Image image = new Image( is );
            imageview = new ImageView( image );
            imageview.setFitWidth( 30 );
            imageview.setFitHeight( 30 );

        } catch (SQLException e) {
            throw new RuntimeException( e );
        }
        return imageview;
    }
}