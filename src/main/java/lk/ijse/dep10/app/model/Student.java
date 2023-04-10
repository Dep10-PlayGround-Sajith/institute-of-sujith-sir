package lk.ijse.dep10.app.model;

import javafx.scene.image.ImageView;
import lk.ijse.dep10.app.util.Grade;

import java.sql.Blob;

public class Student {
    private String id;
    private String name;
    private Blob picture;
    private Grade grade;
    private ImageView imagePicture;
}
