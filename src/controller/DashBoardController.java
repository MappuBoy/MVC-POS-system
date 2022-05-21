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
import model.OrderDetail;
import model.Orders;
import view.tm.CartTm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class DashBoardController implements Initializable {
    public AnchorPane mainRoot;
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
        txtOrderCash.requestFocus();
    }


    public void addOrderDetailOnAction(ActionEvent actionEvent) {
        String itemid = txtId.getText();
        double unitprice = Double.parseDouble(txtUnitPrice.getText());
        int qtyonhand = Integer.parseInt(txtQtyOnHand.getText());
        int addqty = Integer.parseInt(txtAddQty.getText());
        double total = addqty * unitprice;

        if (qtyonhand < addqty) {
            new Alert(Alert.AlertType.WARNING, "Invalid amount").show();
            return;
        }
        CartTm tm = new CartTm(itemid, addqty, total);
        int rownumber = isExists(tm);
        if (isExists(tm) == -1) {
            obList.add(tm);
        } else {
            CartTm temptm = obList.get(rownumber);
            CartTm newtm = new CartTm(temptm.getItem_Code(), temptm.getOrderQty(), temptm.getDiscount());
            if (qtyonhand < temptm.getOrderQty()) {
                new Alert(Alert.AlertType.WARNING, "Invalid amount").show();
                return;
            }
            obList.remove(rownumber);
            obList.add(newtm);
        }
        tblOrderDetail.setItems(obList);
        calCulateCost();
    }

    void calCulateCost() {
        double ttl = 0;
        for (CartTm tm : obList) {
            ttl += tm.getDiscount();
        }
        txtOrderTotal.setText(String.valueOf(ttl));
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
        if (!txtOrderCash.getText().isEmpty()) {
            double orderTotal = Double.parseDouble(txtOrderTotal.getText());
            double custCash = Double.parseDouble(txtOrderCash.getText());
            double cashBalance = custCash - orderTotal;

            cashBalance = cashBalance * 100;
            cashBalance = (int) cashBalance;
            cashBalance = cashBalance / 100;

            txtOrderBalance.setText(Double.toString(cashBalance));
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Please Enter Amount to calculate Balance").show();
        }
    }


    public boolean cashPayment() {
        double total = Double.parseDouble(txtOrderTotal.getText());
        double cash = Double.parseDouble(txtOrderCash.getText());
        return cash > total;
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


    }


    public void searchItembyOnDiscription(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT * FROM item WHERE description=?");
            preparedStatement.setObject(1, txtDiscription.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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
            preparedStatement.setObject(1, txtId.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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


    public void placeOrderOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Orders orders = new Orders(lbOrder.getText(), txtCustmerId.getText(), lbldate.getText(),
                Double.parseDouble(txtOrderTotal.getText()));
        ArrayList<OrderDetail> orderDetailArrayList = new ArrayList<>();
        for (CartTm cartTm : obList) {
            orderDetailArrayList.add(new OrderDetail(lbOrder.getText(), cartTm.getItem_Code(),
                    cartTm.getOrderQty(), cartTm.getDiscount()));
        }
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            if (cashPayment()) {
                boolean addOrder = addOrder(orders);
                if (addOrder) {
                    boolean addOrderDetails = addOrderDetails(orderDetailArrayList);
                    if (addOrderDetails) {
                        boolean updateItemQty = updateItemQty(orderDetailArrayList);
                        if (updateItemQty) {
                            connection.commit();
                            new Alert(Alert.AlertType.CONFIRMATION, "Order placed successfully", ButtonType.OK)
                                    .show();
                        }
                    }
                } else {
                    connection.rollback();
                    new Alert(Alert.AlertType.CONFIRMATION, "Order placed Unsuccessfully", ButtonType.OK).show();
                }
            } else {
                new Alert(Alert.AlertType.CONFIRMATION, "Your payment is not full", ButtonType.OK).show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
        }
        generateOrderId();
        clearAllTextFields();

    }

    public void clearAllTextFields() {
        txtCustmerId.clear();
        txtCustmerName.clear();
        txtCustmerTitle.clear();
        txtCustmerAddress.clear();
        txtCustmerCity.clear();
        txtCustmerProvince.clear();
        txtCustmerCode.clear();
        txtAddQty.clear();
        txtDiscription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        txtPackSize.clear();
        txtId.clear();
        txtItemType.clear();
        txtOrderTotal.clear();
        txtOrderCash.clear();
        txtOrderBalance.clear();
        for (int i = 0; i < tblOrderDetail.getItems().size(); i++) {
            tblOrderDetail.getItems().clear();
        }
    }
    //-----------------------------------------transaction part---------------------------------------------------------

    public static boolean addOrder(Orders orders) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement psmt = connection.prepareStatement("INSERT INTO orders VALUES (?,?,?,?)");
            psmt.setObject(1, orders.getOrderId());
            psmt.setObject(2, orders.getCust_id());
            psmt.setObject(3, orders.getOrder_Date());
            psmt.setObject(4, orders.getCost());
            return psmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addOrderDetails(OrderDetail orderDetail) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement("INSERT INTO order_detail VALUES (?,?,?,?)");
        pstm.setObject(1, orderDetail.getOrderId());
        pstm.setObject(2, orderDetail.getItem_Code());
        pstm.setObject(3, orderDetail.getOrderQty());
        pstm.setObject(4, orderDetail.getDiscount());
        return pstm.executeUpdate() > 0;
    }

    public static boolean addOrderDetails(ArrayList<OrderDetail> arrayList) throws SQLException, ClassNotFoundException {
        for (OrderDetail orderdetail : arrayList) {
            boolean addOrderDetails = addOrderDetails(orderdetail);
            if (!addOrderDetails) {
                return false;
            }
        }
        return true;
    }

    public static boolean updateItemQty(ArrayList<OrderDetail> arrayList) throws SQLException, ClassNotFoundException {
        for (OrderDetail orderDetail : arrayList) {
            boolean updateQty = updateItemQty(orderDetail);
            if (!updateQty) {
                return false;
            }
        }
        return true;
    }

    public static boolean updateItemQty(OrderDetail orderDetail) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement("UPDATE item SET qtyOnHand=qtyOnHand-? WHERE Item_Code=?");
        statement.setObject(1, orderDetail.getOrderQty());
        statement.setObject(2, orderDetail.getItem_Code());
        return statement.executeUpdate() > 0;
    }

    public void searchCustomerbyName(ActionEvent actionEvent) {
    }

    public void searchCustomer(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT * FROM customer WHERE Cust_id=?");
            preparedStatement.setObject(1, txtCustmerCode.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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
}
