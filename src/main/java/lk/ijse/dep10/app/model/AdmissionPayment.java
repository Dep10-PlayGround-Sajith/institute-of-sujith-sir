package lk.ijse.dep10.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AdmissionPayment implements Serializable {
    String studentID;
    BigDecimal admission;
    LocalDate date;

    public AdmissionPayment() {
    }

    public AdmissionPayment(String studentID, BigDecimal admission, LocalDate date) {
        this.studentID = studentID;
        this.admission = admission;
        this.date = date;
    }

    public AdmissionPayment(String studentID, BigDecimal admission) {
        this.studentID = studentID;
        this.admission = admission;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public BigDecimal getAdmission() {
        return admission;
    }

    public void setAdmission(BigDecimal admission) {
        this.admission = admission;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "AdmissionPayment{" +
                "studentID='" + studentID + '\'' +
                ", admission=" + admission +
                '}';
    }
}
