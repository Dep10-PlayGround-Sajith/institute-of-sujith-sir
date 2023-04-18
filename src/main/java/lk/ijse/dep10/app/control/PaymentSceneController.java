package lk.ijse.dep10.app.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lk.ijse.dep10.app.db.DBConnection;
import lk.ijse.dep10.app.model.ClassFeePayment;
import lk.ijse.dep10.app.model.Payment;
import lk.ijse.dep10.app.util.Month;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class PaymentSceneController {

    public Button btnAdd;
    public Button btnCancel;
    public Button btnPay;
    public Button btnRemove;
    public ComboBox<String> cmbClass;
    public ImageView imgProfPic;
    public Label label;
    public TableView<ClassFeePayment> tblClassFeePay;
    public TableView<Payment> tblPaymentDetails;
    public TextField txtAdmission;
    public TextField txtID;
    public TableColumn colFromMonth;
    public TableColumn colToMonth;
    public TableColumn colTotalFee;
    public TextField txtTotal;
    public TableColumn colForMonth;
    public TableColumn colDate;
    public TableView tblTotal;
    public TableColumn colTotal;

    public void initialize() {
        tblPaymentDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("clas"));
        tblPaymentDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("fee"));
        tblPaymentDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("dueMonths"));
        colForMonth.setCellValueFactory(new PropertyValueFactory<>("lastPayment"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("lastDateOfPayment"));

        tblClassFeePay.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("clas"));
        tblClassFeePay.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("fee"));
        colFromMonth.setCellValueFactory(new PropertyValueFactory<>("from"));
        colToMonth.setCellValueFactory(new PropertyValueFactory<>("to"));

        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tblClassFeePay.getSelectionModel().selectedItemProperty().addListener((value, prev, current) -> {
            ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
            if (current == null) return;
            btnRemove.setDisable(false);
            tblTotal.getItems().clear();
            ObservableList<ClassFeePayment> payments = tblTotal.getItems();
            for (ClassFeePayment i : items) {
                payments.add(i);
            }
            ObservableList<ClassFeePayment> fees = tblTotal.getItems();
            BigDecimal sum = BigDecimal.ZERO;
            for (ClassFeePayment s : fees) {
                sum = sum.add(s.getTotal());
            }
            BigDecimal admission = txtAdmission.getText().equals("Paid") ? BigDecimal.ZERO : BigDecimal.valueOf(Integer.parseInt(txtAdmission.getText()));
            System.out.println(admission);
            txtTotal.setText("" + (sum.add(admission)));

        });


    }

    public void btnAddOnAction(ActionEvent event) {
        if (txtID.getText().startsWith("S-")) {
            new Alert(Alert.AlertType.INFORMATION, "Enter a Student Number like S-XXX").showAndWait();
        }
        String selectedItem = cmbClass.getSelectionModel().getSelectedItem();
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList<ClassFeePayment> paymentDetails = tblClassFeePay.getItems();
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Class WHERE class_name=? && student_id=? ");
            PreparedStatement stm1 = connection.prepareStatement("SELECT * FROM Payment WHERE class_name=? && student_id=? ");

            stm.setString(1, selectedItem);
            stm.setString(2, txtID.getText());
            stm1.setString(1, selectedItem);
            stm1.setString(2, txtID.getText());
            System.out.println("Hello");
            ResultSet rst = stm.executeQuery();
            ResultSet rst1 = stm1.executeQuery();
            boolean next = rst.next();
            boolean next1 = rst1.next();

            if (!next1 && next) {
                String clas = rst.getString("class_name");
                BigDecimal fee = rst.getBigDecimal("class_fee");


                Month monthLastPaid = Month.valueOf(LocalDate.now().getMonth().name());
                LocalDate month = LocalDate.of(2020, java.time.Month.valueOf(monthLastPaid.name()), 10);
                ObservableList<String> itemto = FXCollections.observableArrayList();
                itemto.addAll("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
                if (month.getMonthValue() - 1 >= 0) {
                    itemto.remove(0, month.getMonthValue() - 1);
                }
                ObservableList<String> items = itemto;

                ComboBox<String> cmbfrm = new ComboBox<>(items);
                ComboBox<String> cmbt = new ComboBox<>(cmbfrm.getItems());


                ClassFeePayment payments = new ClassFeePayment(clas, fee, cmbfrm, cmbt);

                paymentDetails.add(payments);
//                cmbClass.getSelectionModel().clearSelection();
//                cmbClass.getItems().remove(clas);
            }
            if (next && next1) {
                System.out.println("found student");
                String clas = rst.getString("class_name");
                BigDecimal fee = rst.getBigDecimal("class_fee");

                Month monthLastPaid = Month.valueOf(rst1.getString("month"));
                LocalDate month = LocalDate.of(2020, java.time.Month.valueOf(monthLastPaid.name()), 10);
                ObservableList<String> itemto = FXCollections.observableArrayList();
                itemto.addAll("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
                itemto.remove(0, month.getMonthValue());
                ObservableList<String> items = itemto;

                ComboBox<String> cmbfrm = new ComboBox<>(items);
                ComboBox<String> cmbt = new ComboBox<>(cmbfrm.getItems());


                ClassFeePayment payments = new ClassFeePayment(clas, fee, cmbfrm, cmbt);
                System.out.println("this is the print --> " + payments.getFrom().getItems());
                System.out.println("Payment created");
                paymentDetails.add(payments);
                cmbClass.getSelectionModel().clearSelection();
                cmbClass.getItems().remove(clas);
                cmbClass.getSelectionModel().select(0);


            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    public void btnCancelOnAction(ActionEvent event) {
        tblRefresh();
    }

    public void btnPayOnAction(ActionEvent event) {
        if (txtTotal.getText().isEmpty()) return;

        Connection connection = DBConnection.getInstance().getConnection();


        ObservableList<Payment> payments = tblPaymentDetails.getItems();
        ObservableList<ClassFeePayment> feePayment = tblClassFeePay.getItems();

        try {
            connection.setAutoCommit(false);

            PreparedStatement stmAdmission = connection.
                    prepareStatement("SELECT*FROM Admission WHERE student_id=?");

            PreparedStatement stmPaymentCheck = connection.
                    prepareStatement("SELECT*FROM Payment WHERE student_id=? && class_name=?");

            PreparedStatement stmAddAdmission = connection.
                    prepareStatement("INSERT INTO Admission(student_id, admission, paid_date) VALUES (?,?,?)");

            PreparedStatement stmPayment = connection
                    .prepareStatement("INSERT INTO Payment (student_id, month, last_payment, fee, class_name) VALUES (?, ?,?,?,?)");

            PreparedStatement stmUpdatePayment = connection
                    .prepareStatement("UPDATE Payment SET  month=? ,last_payment=? ,fee=? WHERE class_name=? && student_id=?");

            stmAdmission.setString(1, txtID.getText());

            stmPayment.setString(1, txtID.getText());
//            stmPayment.setString(2,txtID.getText());

            ResultSet rstAdmission = stmAdmission.executeQuery();
            if (!rstAdmission.next()) {
                if (txtAdmission.getText().isBlank()) {
                    new Alert(Alert.AlertType.WARNING, "Please AdmissionFee must pay before enter the Institute").showAndWait();
                    return;
                }
                stmAddAdmission.setString(1, txtID.getText());
                stmAddAdmission.setBigDecimal(2, BigDecimal.valueOf(Double.parseDouble(txtAdmission.getText())));
                stmAddAdmission.setDate(3, Date.valueOf(LocalDate.now()));

                stmAddAdmission.executeUpdate();
                if (feePayment.size() != 0) {
                    for (ClassFeePayment p : tblClassFeePay.getItems()) {
                        stmPayment.setString(2, p.getTo().getSelectionModel().getSelectedItem().toString());
                        stmPayment.setDate(3, Date.valueOf(LocalDate.now()));
                        stmPayment.setBigDecimal(4, p.getFee());
                        stmPayment.setString(5, p.getClas());

                        Payment newClassPayment = new Payment(p.getClas(), p.getFee(), Month.valueOf(p.getTo().getSelectionModel().getSelectedItem().toString()), LocalDate.now());
                        payments.add(newClassPayment);
                        stmPayment.executeUpdate();
                    }
                }
            } else if (tblClassFeePay.getItems().size() != 0) {

                int count = 0;
                for (ClassFeePayment p : feePayment) {
                    stmPaymentCheck.setString(1, txtID.getText());
                    stmPaymentCheck.setString(2, p.getClas());
                    ResultSet rstPaidSubjects = stmPaymentCheck.executeQuery();
                    if (rstPaidSubjects.next()) {
                        stmUpdatePayment.setString(1, p.getTo().getSelectionModel().getSelectedItem().toString());

                        stmUpdatePayment.setDate(2, Date.valueOf(LocalDate.now()));
                        stmUpdatePayment.setBigDecimal(3, p.getFee());
                        stmUpdatePayment.setString(4, p.getClas());
                        stmUpdatePayment.setString(5, txtID.getText());
                        stmUpdatePayment.executeUpdate();
                    } else {
                        stmPayment.setString(2, p.getTo().getSelectionModel().getSelectedItem().toString());
                        stmPayment.setDate(3, Date.valueOf(LocalDate.now()));
                        stmPayment.setBigDecimal(4, p.getFee());
                        stmPayment.setString(5, p.getClas());

                        stmPayment.executeUpdate();
                    }

                }

            }
            connection.commit();
            tblPaymentDetails.refresh();
        } catch (SQLException e) {
            throw new RuntimeException();
        } catch (Throwable e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the Payment").show();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public void btnRemoveOnAction(ActionEvent event) {
        tblClassFeePay.getItems().remove(tblClassFeePay.getSelectionModel().getSelectedItem());

    }

    private void tblRefresh() {
        if (txtID.getText().substring(0, 1).equals("S-")) {
            new Alert(Alert.AlertType.INFORMATION, "Enter a Student Number like S-XXX").showAndWait();
            return;
        }
        tblTotal.getItems().clear();
        tblClassFeePay.getItems().clear();
        tblPaymentDetails.getItems().clear();
        cmbClass.getItems().clear();
        System.out.println("Im working");
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList<Payment> paymentDetails = tblPaymentDetails.getItems();

        try {
            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM Class WHERE student_id=?");

            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Payment WHERE student_id=?");
            PreparedStatement stm3 = connection.prepareStatement("SELECT * FROM Admission WHERE student_id=?");
            stm3.setString(1, txtID.getText());

            txtAdmission.setText(!stm3.executeQuery().next() ? "1000.00" : "Paid");
            stm.setString(1, txtID.getText());
            stm2.setString(1, txtID.getText());
            System.out.println("Hi");
            ResultSet rst = stm.executeQuery();
            ResultSet rst2 = stm2.executeQuery();
            while (rst2.next()) {
                cmbClass.getItems().add(rst2.getString(2));
            }
            if (cmbClass.getItems().size() == 0) {
                new Alert(Alert.AlertType.INFORMATION, String.format("There is No such Student %S Please Add The Student first or enter Valid Student Number", txtID.getText())).showAndWait();
            }
            cmbClass.getSelectionModel().select(0);

            while (rst.next()) {
                System.out.println("found student");
                String clas = rst.getString("class_name");
                Month lastPayment = Month.valueOf(rst.getString("month"));
                LocalDate lastDateOfPayment = rst.getDate("last_payment").toLocalDate();

//                cmbClass.getItems().add(clas);
                PreparedStatement stm1 = connection.prepareStatement("SELECT * FROM Class WHERE class_name=? && student_id=?");
                Payment payment = new Payment(clas, null, lastPayment, lastDateOfPayment);
                stm1.setString(1, clas);
                stm1.setString(2, txtID.getText());
                ResultSet rst1 = stm1.executeQuery();
                rst1.next();
                BigDecimal fee = rst1.getBigDecimal("class_fee");
                System.out.println(fee);
                payment.setFee(fee);
                System.out.println(payment);
                System.out.println("Payment created");
                paymentDetails.add(payment);
                tblPaymentDetails.refresh();
                System.out.println(tblPaymentDetails.getItems());


            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void txtIDOnKeyReleased(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER) {

            tblRefresh();
        }

    }

    public void colToMonthOnEditCommit(TableColumn.CellEditEvent cellEditEvent) {
        ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
        tblTotal.getItems().clear();
        ObservableList<ClassFeePayment> payments = tblTotal.getItems();
        for (ClassFeePayment i : items) {
            payments.add(i);
        }
        ObservableList<ClassFeePayment> fees = tblTotal.getItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (ClassFeePayment s : fees) {
            sum = sum.add(s.getTotal());
        }
        txtTotal.setText(sum.toString());
    }

    public void colFromMonthOnEditommit(TableColumn.CellEditEvent cellEditEvent) {
        ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
        tblTotal.getItems().clear();
        ObservableList<ClassFeePayment> payments = tblTotal.getItems();
        for (ClassFeePayment i : items) {
            payments.add(i);
        }
        ObservableList<ClassFeePayment> fees = tblTotal.getItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (ClassFeePayment s : fees) {
            sum = sum.add(s.getTotal());
        }
        txtTotal.setText(sum.toString());
    }

    public void tblClassFeePayOnMouseReleased(MouseEvent event) {
        ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
        tblTotal.getItems().clear();
        ObservableList<ClassFeePayment> payments = tblTotal.getItems();
        for (ClassFeePayment i : items) {
            payments.add(i);
        }
        ObservableList<ClassFeePayment> fees = tblTotal.getItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (ClassFeePayment s : fees) {
            sum = sum.add(s.getTotal());
        }
        txtTotal.setText(sum.toString());
    }

    public void tblClassFeePayOnMouseMoved(MouseEvent event) {
        ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
        tblTotal.getItems().clear();
        ObservableList<ClassFeePayment> payments = tblTotal.getItems();
        for (ClassFeePayment i : items) {
            payments.add(i);
        }
        ObservableList<ClassFeePayment> fees = tblTotal.getItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (ClassFeePayment s : fees) {
            sum = sum.add(s.getTotal());
        }
        txtTotal.setText(sum.toString());
    }

    public void tblClassFeePayOnContextMenueRequested(ContextMenuEvent contextMenuEvent) {
        ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
        tblTotal.getItems().clear();
        ObservableList<ClassFeePayment> payments = tblTotal.getItems();
        for (ClassFeePayment i : items) {
            payments.add(i);
        }
        ObservableList<ClassFeePayment> fees = tblTotal.getItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (ClassFeePayment s : fees) {
            sum = sum.add(s.getTotal());
        }
        txtTotal.setText(sum.toString());
    }

    public void mousePressed(MouseEvent event) {
        ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
        tblTotal.getItems().clear();
        ObservableList<ClassFeePayment> payments = tblTotal.getItems();
        for (ClassFeePayment i : items) {
            payments.add(i);
        }
        ObservableList<ClassFeePayment> fees = tblTotal.getItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (ClassFeePayment s : fees) {
            sum = sum.add(s.getTotal());
        }
        txtTotal.setText(sum.toString());
    }

    public void mouseliClked(MouseEvent event) {
        ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
        tblTotal.getItems().clear();
        ObservableList<ClassFeePayment> payments = tblTotal.getItems();
        for (ClassFeePayment i : items) {
            payments.add(i);
        }
        ObservableList<ClassFeePayment> fees = tblTotal.getItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (ClassFeePayment s : fees) {
            sum = sum.add(s.getTotal());
        }
        txtTotal.setText(sum.toString());
    }

    public void methodtexthanged(MouseEvent event) {
        ObservableList<ClassFeePayment> items = tblClassFeePay.getItems();
        tblTotal.getItems().clear();
        ObservableList<ClassFeePayment> payments = tblTotal.getItems();
        for (ClassFeePayment i : items) {
            payments.add(i);
        }
        ObservableList<ClassFeePayment> fees = tblTotal.getItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (ClassFeePayment s : fees) {
            sum = sum.add(s.getTotal());
        }
        txtTotal.setText(sum.toString());
    }
}
