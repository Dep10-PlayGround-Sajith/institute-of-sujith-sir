package lk.ijse.dep10.app.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lk.ijse.dep10.app.util.Gender;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;


public class Teacher implements Serializable {

    private String teacher_name;
    private Blob teacher_picture;

    private ImageView imgview;

    private String teacher_contact;
    private String gender;


    public Teacher(String teacher_name, Blob teacher_picture, String teacher_contact, String gender) {
        this.teacher_name = teacher_name;
        this.teacher_picture = teacher_picture;
        this.imgview = getimgview();
        this.teacher_contact = teacher_contact;
        this.gender = gender;
    }

    public Teacher() {
    }

    public Teacher(String teacher_name, Blob teacher_picture) {
        this.teacher_name = teacher_name;
        this.teacher_picture = teacher_picture;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ImageView getimgview() {
        if (teacher_picture == null) {
            ImageView image = new ImageView("/image/noimg.jpg");
            image.setFitHeight(40);
            image.setFitWidth(40);
            return image;
        }
        ImageView image;
        try {
            InputStream is = this.teacher_picture.getBinaryStream();
            Image im = new Image(is);
            image = new ImageView(im);
            image.setFitHeight(40);
            image.setFitWidth(40);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    public String getTeacher_contact() {
        return teacher_contact;
    }

    public void setTeacher_contact(String teacher_contact) {
        this.teacher_contact = teacher_contact;
    }


    public ImageView getImgview() {
        return imgview;
    }

    public void setImgview(ImageView imgview) {
        this.imgview = imgview;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public Blob getTeacher_picture() {
        return teacher_picture;
    }

    public void setTeacher_picture(Blob teacher_picture) {
        this.teacher_picture = teacher_picture;
    }


}
