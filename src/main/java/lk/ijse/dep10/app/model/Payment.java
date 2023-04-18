package lk.ijse.dep10.app.model;

import lk.ijse.dep10.app.util.Month;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment  implements Serializable {
     private String clas;
     private BigDecimal fee;
     private Month lastPayment;
     private LocalDate lastDateOfPayment;
     private String dueMonths;

     public Payment() {
     }

     public Payment(String clas, BigDecimal fee, Month lastPayment, LocalDate lastDateOfPayment) {
          this.clas = clas;
          this.fee = fee;
          this.lastPayment = lastPayment;
          this.lastDateOfPayment = lastDateOfPayment;
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

     public java.time.Month getLastPayment() {
          LocalDate month = LocalDate.of(2020, java.time.Month.valueOf(lastPayment.name()),10);

          return month.getMonth();
     }

     public void setLastPayment(Month lastPayment) {
          this.lastPayment = lastPayment;
     }

     public LocalDate getLastDateOfPayment() {
          return lastDateOfPayment;
     }

     public void setLastDateOfPayment(LocalDate lastDateOfPayment) {
          this.lastDateOfPayment = lastDateOfPayment;
     }

     public String getDueMonths() {
          if (java.time.Month.of(getLastPayment().getValue()).compareTo(LocalDate.now().getMonth())<0){
               return  String.format( "%s-%s",java.time.Month.of(getLastPayment().getValue()+1),LocalDate.now().getMonth());

          }
          return  "paid";     }

     public void setDueMonths(String dueMonths) {
          this.dueMonths = dueMonths;
     }

     @Override
     public String toString() {
          return "Payment{" +
                  "clas='" + clas + '\'' +
                  ", fee=" + fee +
                  ", lastPayment=" + lastPayment +
                  ", lastDateOfPayment=" + lastDateOfPayment +
                  ", dueMonths='" + dueMonths + '\'' +
                  '}';
     }
}
