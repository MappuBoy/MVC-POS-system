package controller;

import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.Customer;
import view.tm.CustomerTM;
import view.tm.Ordertm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomerFormController implements Initializable {
    public JFXTextField txtcityCode;
    public JFXTextField txtProvince;
    public JFXTextField txtCustId;
    public JFXTextField txtTitle;
    public JFXTextField txtCity;
    public JFXTextField txtCustAddress;
    public JFXTextField txtCustFirstName;
    public AnchorPane customerRoot;
    public TableView<CustomerTM> tbCustomerOrderDetail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //initzializ the table customer
        tbCustomerOrderDetail.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("Cust_id"));
        tbCustomerOrderDetail.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("Cust_Title"));
        tbCustomerOrderDetail.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("Cust_name"));
        tbCustomerOrderDetail.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("Cust_address"));
        tbCustomerOrderDetail.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("City"));
        tbCustomerOrderDetail.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("Province"));
        tbCustomerOrderDetail.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("PostalCode"));

        //get selected row to textField
        tbCustomerOrderDetail.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            txtCustId.setText(newValue.getCust_id());
            txtTitle.setText(newValue.getCust_Title());
            txtCustFirstName.setText(newValue.getCust_name());
            txtCustAddress.setText(newValue.getCust_address());
            txtCity.setText(newValue.getCity());
            txtProvince.setText(newValue.getProvince());
            txtcityCode.setText(newValue.getPostalCode());
        });

        //getALLCustomer details
        getAllCustomerDetails();

    }

    private void getAllCustomerDetails() {

        ArrayList<Customer> customers = new ArrayList<>();
        ObservableList<CustomerTM> tmObservableList = FXCollections.observableArrayList();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customer");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                customers.add(new Customer(resultSet.getString(1),resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),
                        resultSet.getString(5),resultSet.getString(6),
                        resultSet.getString(7)));
            }
            for (Customer cust : customers) {
                tmObservableList.add(new CustomerTM(cust.getCust_id(), cust.getCust_Title(), cust.getCust_name(),
                        cust.getCust_address(), cust.getCity(), cust.getProvince(), cust.getPostalCode()));
            }
            tbCustomerOrderDetail.setItems(tmObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void IdOnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT * FROM customer WHERE Cust_id=?");
            preparedStatement.setObject(1,txtCustId.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                txtTitle.setText(resultSet.getString(2));
                txtCustFirstName.setText(resultSet.getString(3));
                txtCustAddress.setText(resultSet.getString(4));
                txtCity.setText(resultSet.getString(5));
                txtProvince.setText(resultSet.getString(6));
                txtcityCode.setText(resultSet.getString(7));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomerOnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("DELETE FROM customer WHERE Cust_id=?");
            preparedStatement.setObject(1,txtCustId.getText());
            int executeUpdate = preparedStatement.executeUpdate();
            if (executeUpdate > 0){
                new Alert(Alert.AlertType.CONFIRMATION, "Supplier Delete Successfully.....!", ButtonType.OK).
                        show();
                txtCustId.requestFocus();
            }else {
                new Alert(Alert.AlertType.WARNING, "Supplier Delete Unsuccessfully.....", ButtonType.OK).
                        show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void btnBackButton(ActionEvent actionEvent) throws IOException {
            customerRoot.getChildren().clear();
            customerRoot.getChildren().add(FXMLLoader.load(this.getClass().getResource("/view/ManagerDashBoardForm.fxml")));
        }



}



