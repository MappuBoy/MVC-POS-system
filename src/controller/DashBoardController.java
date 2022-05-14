package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import view.tm.CartTm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;

public class DashBoardController implements Initializable {
    public AnchorPane mainRoot;
    public TextField ldCurrenttime;
    public TextField lbCurrentDate;
    public JFXButton btnBack;
    public Label lbOrder;
    public JFXTextField txtCustmerId;
    public JFXTextField txtCustmerName;
    public JFXTextField txtCustmerTitle;
    public JFXTextField txtCustmerAddress;
    public JFXTextField txtCustmerCity;
    public JFXTextField txtCustmerProvince;
    public JFXTextField txtCustmerCode;
    public ImageView addCustomerButton;
    public AnchorPane managerRoot;
    public JFXTextField txtAddQty;
    public JFXTextField txtDiscription;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtPackSize;
    public JFXTextField txtId;
    public JFXTextField txtItemType;
    public TextField txtOrderTotal;
    public TextField txtOrderCash;
    public TextField txtOrderBalance;
    public TableView<CartTm> tblOrderDetail;
    public TableColumn tborder;
    public TableColumn tbqty;
    public TableColumn tbprice;
    public Label lbltime;
    public Label lbldate;
    ObservableList<CartTm> obList = FXCollections.observableArrayList();
    int cartSelectedRowForRemove = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tborder.setCellValueFactory(new PropertyValueFactory<>("Item_Code"));
        tbqty.setCellValueFactory(new PropertyValueFactory<>("OrderQty"));
        tbprice.setCellValueFactory(new PropertyValueFactory<>("Discount"));

        generateOrderId();
        loadDateAndTime();

        tblOrderDetail.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            cartSelectedRowForRemove = (int) newValue;
        });


    }





    public void qtyOnAction(ActionEvent actionEvent) {
    }



    public void addOrderDetailOnAction(ActionEvent actionEvent) {
    }

    public void removeOrderDetailOnAction(ActionEvent actionEvent) {
        if (cartSelectedRowForRemove == -1) {
            new Alert(Alert.AlertType.WARNING, "please select a row").show();
        } else {
            obList.remove(cartSelectedRowForRemove);
            tblOrderDetail.refresh();
        }
    }


    private int isExists(CartTm tm) {
        for (int i = 0; i < obList.size(); i++) {
            if (tm.getItem_Code().equals(obList.get(i).getItem_Code())) {
                return i;
            }
        }
        return -1;
    }


    public void calculateBalance(ActionEvent actionEvent) {
        if (!txtOrderCash.getText().isEmpty()){
            double orderTotal = Double.parseDouble(txtOrderTotal.getText());
            double custCash = Double.parseDouble(txtOrderCash.getText());
            double cashBalance = custCash - orderTotal;

            cashBalance = cashBalance*100;
            cashBalance = (double) ((int)cashBalance);
            cashBalance = cashBalance/100;

            txtOrderBalance.setText(Double.toString(cashBalance));
        }else {
            new Alert(Alert.AlertType.INFORMATION,"Please Enter Amount to calculate Balance").show();
        }
    }


    public void placeOrderOnAction(ActionEvent actionEvent) {
    }

    public void printOnAction(ActionEvent actionEvent) {
    }

    private void generateOrderId() {

        String lastOrderId = null;

        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().
                    prepareStatement("SELECT OrderId FROM orders ORDER BY OrderId DESC LIMIT 1");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                lastOrderId = resultSet.getString(1);
            }

            if (lastOrderId != null) {
                lastOrderId = lastOrderId.split("[A-Z]")[1];
                if (Integer.parseInt(lastOrderId) < 9) {
                    lastOrderId = "O00" + (Integer.parseInt(lastOrderId) + 1);
                    lbOrder.setText(lastOrderId);
                } else if (Integer.parseInt(lastOrderId) < 100) {
                    lastOrderId = "O0" + (Integer.parseInt(lastOrderId) + 1);
                    lbOrder.setText(lastOrderId);
                }
            } else {
                lbOrder.setText("O001");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void backOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }


    public void managerLoginOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ManagerLoginForm");
    }

    public void addCustomerOnAction(ActionEvent actionEvent) throws IOException {
        setUi("AddCustomerForm");
    }
    private void setUi(String location) throws IOException {
        mainRoot.getChildren().clear();
        mainRoot.getChildren().add(FXMLLoader.load(this.getClass().getResource("/view/" + location + ".fxml")));
    }

    public void searchCustomerByIdOnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT * FROM customer WHERE Cust_id=?");
            preparedStatement.setObject(1,txtCustmerCode.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                txtCustmerTitle.setText(resultSet.getString(2));
                txtCustmerName.setText(resultSet.getString(3));
                txtCustmerAddress.setText(resultSet.getString(4));
                txtCustmerCity.setText(resultSet.getString(5));
                txtCustmerProvince.setText(resultSet.getString(6));
                txtCustmerCode.setText(resultSet.getString(7));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void searchCustomerbyName(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT * FROM customer WHERE Cust_name=?");
            preparedStatement.setObject(1,txtCustmerName.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                txtCustmerCode.setText(resultSet.getString(1));
                txtCustmerTitle.setText(resultSet.getString(2));
                txtCustmerAddress.setText(resultSet.getString(4));
                txtCustmerCity.setText(resultSet.getString(5));
                txtCustmerProvince.setText(resultSet.getString(6));
                txtCustmerCode.setText(resultSet.getString(7));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void searchItembyOnDiscription(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT * FROM item WHERE description=?");
            preparedStatement.setObject(1,txtDiscription.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                txtId.setText(resultSet.getString(1));
                txtPackSize.setText(resultSet.getString(3));
                txtQtyOnHand.setText(resultSet.getString(4));
                txtUnitPrice.setText(resultSet.getString(5));
                txtItemType.setText(resultSet.getString(6));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void SearchItembyIdOnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT * FROM item WHERE Item_Code=?");
            preparedStatement.setObject(1,txtId.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                txtDiscription.setText(resultSet.getString(2));
                txtPackSize.setText(resultSet.getString(3));
                txtQtyOnHand.setText(resultSet.getString(4));
                txtUnitPrice.setText(resultSet.getString(5));
                txtItemType.setText(resultSet.getString(6));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void loadDateAndTime() {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        lbldate.setText(f.format(date));

        Timeline time = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            lbltime.setText(currentTime.getHour() + " : " + currentTime.getMinute() + " : " + currentTime.getSecond());
        }),
                new KeyFrame(Duration.seconds(1)));
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }
}
