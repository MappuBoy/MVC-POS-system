package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.Orders;
import view.tm.Ordertm;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ManagerDashBoardFormController implements Initializable {
    public AnchorPane adminPage;
    public Label lblItemCount;
    public Label lblCustomerCount;
    public Label lblOrderCount;
    public TableView<Ordertm> tbOrder;
    public TableColumn tborderid;
    public TableColumn tbcustomerid;
    public TableColumn tbdate;
    public TableColumn tbcost;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
// initialize table
        tbOrder.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("OrderId"));
        tbOrder.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("Cust_id"));
        tbOrder.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("Order_Date"));
        tbOrder.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("Cost"));

        //get all count from the item table
        getItemCount();

        //get all count from the customer table
        getCoustomerCount();

        //get all count from the order table
        getOrderCount();

        getAllOrderDetailss();

    }
    private void getAllOrderDetailss() {

        ArrayList<Orders> orderArrayList = new ArrayList<>();
        ObservableList<Ordertm> tmObservableList = FXCollections.observableArrayList();

        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection()
                    .prepareStatement("SELECT * FROM orders");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orderArrayList.add(new Orders(resultSet.getString(1),
                        resultSet.getString(2), resultSet.getString(3),
                        resultSet.getDouble(4)));
            }
            for (Orders orders : orderArrayList) {
                tmObservableList.add(new Ordertm(orders.getOrderId(),
                        orders.getCust_id(),orders.getOrder_Date(), orders.getCost()));
            }
            tbOrder.setItems(tmObservableList);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void getOrderCount() {

        String lblOrderCountTex = null;
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(OrderId) FROM orders");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int orderCount = resultSet.getInt(1);
                lblOrderCountTex = String.valueOf(orderCount);
            }
            if (lblOrderCountTex != null) {
                lblOrderCount.setText(lblOrderCountTex);
            } else {
                lblOrderCount.setText("0");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getCoustomerCount() {

        String lblRegCustCountText = null;

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(Cust_id) FROM customer");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int regCustCount = resultSet.getInt(1);
                lblRegCustCountText = String.valueOf(regCustCount);
            }
            if (lblRegCustCountText != null) {
                lblCustomerCount.setText(lblRegCustCountText);
            } else {
                lblCustomerCount.setText("0");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getItemCount() {

        String lblItemCountText = null;

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(Item_Code) FROM item");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int regCustCount = resultSet.getInt(1);
                lblItemCountText = String.valueOf(regCustCount);
            }
            if (lblItemCountText != null) {
                lblItemCount.setText(lblItemCountText);
            } else {
                lblItemCount.setText("0");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
