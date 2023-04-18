package lk.ijse.dep10.app.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.print.Printer;
import javafx.scene.control.ComboBox;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ClassFeePayment implements Serializable {
    private String clas;
    private BigDecimal fee;
    private ComboBox from;
    private ComboBox to;
    private BigDecimal total;

    public ClassFeePayment() {
    }

    public ClassFeePayment(String clas, BigDecimal fee,ComboBox from,ComboBox to) {
        this.clas = clas;
        this.fee = fee;
        this.from = from;
        this.to = to;
    }

    public String getClas() {
        return clas;
    }

    public void setClas(String clas) {
        this.clas = clas;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public ComboBox getFrom() {
        return from;
    }

    public void setFrom(ComboBox from) {
        this.from = from;
    }

    public ComboBox getTo() {
       return to;
    }

    public void setTo(ComboBox to) {
        this.to = to;
    }

    public BigDecimal getTotal() {
        if (from.getSelectionModel().getSelectedItem()==null)return BigDecimal.valueOf(0);
        if (to.getSelectionModel().getSelectedIndex() >= from.getSelectionModel().getSelectedIndex()) {
            int differnce =to.getSelectionModel().getSelectedIndex() - from.getSelectionModel().getSelectedIndex()+1;
            return BigDecimal.valueOf(differnce).multiply(fee);
        }
        return fee ;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ClassFeePayment{" +
                "clas='" + clas + '\'' +
                ", fee=" + fee +
                ", from=" + from +
                ", to=" + to +
                ", total=" + total +
                '}';
    }
}
