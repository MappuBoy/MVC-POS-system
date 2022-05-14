package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.Customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCustomerFormController {
    public AnchorPane AddCustomerRoot;
    public JFXTextField txtTitle;
    public JFXTextField txtProvince;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXButton addBtn;
    public JFXTextField txtId;
    public JFXTextField txtPostalCode;
    public ImageView addImgBtn;
    public JFXTextField txtCity;

    public void addICustomerOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        Customer customer = new Customer(txtTitle.getText(),txtTitle.getText(),txtName.getText(),txtAddress.getText(),
                        txtCity.getText(), txtProvince.getText(),txtPostalCode.getText());
        Connection connection= DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement= connection.
                prepareStatement("INSERT INTO customer VALUES(?,?,?,?,?,?,?)");
        preparedStatement.setObject(1,customer.getCust_id());
        preparedStatement.setObject(2,customer.getCust_Title());
        preparedStatement.setObject(3,customer.getCust_name());
        preparedStatement.setObject(4,customer.getCust_address());
        preparedStatement.setObject(5,customer.getCity());
        preparedStatement.setObject(6,customer.getProvince());
        preparedStatement.setObject(7,customer.getPostalCode());
        int add = preparedStatement.executeUpdate();

        if (add > 0){
            new Alert(Alert.AlertType.CONFIRMATION, "Saved!", ButtonType.OK).show();
        }else {
            new Alert(Alert.AlertType.WARNING, "Try Again!", ButtonType.OK).show();
        }
        clearTextOnAction();

    }

    public void btnBackButton(ActionEvent actionEvent) throws IOException {
        setUi("DashBoard");
    }

    public void idOnAction(ActionEvent actionEvent) {
        txtTitle.requestFocus();
    }

    public void titleOnAction(ActionEvent actionEvent) {
        txtName.requestFocus();
    }

    public void nameOnAction(ActionEvent actionEvent) {
        txtAddress.requestFocus();
    }

    public void addressOnAction(ActionEvent actionEvent) {
        txtCity.requestFocus();
    }

    public void cityOnAction(ActionEvent actionEvent) {
        txtProvince.requestFocus();
    }

    public void provinceOnAction(ActionEvent actionEvent) {
        txtPostalCode.requestFocus();
    }

    public void codeOnAction(ActionEvent actionEvent) {
        addBtn.requestFocus();
    }
    public void clearTextOnAction() {
        txtId.clear();
        txtTitle.clear();
        txtName.clear();
        txtAddress.clear();
        txtCity.clear();
        txtProvince.clear();
        txtPostalCode.clear();
    }
    private void setUi(String location) throws IOException {
        AddCustomerRoot.getChildren().clear();
        AddCustomerRoot.getChildren().add(FXMLLoader.load(this.getClass().getResource("/view/" + location + ".fxml")));
    }
}
