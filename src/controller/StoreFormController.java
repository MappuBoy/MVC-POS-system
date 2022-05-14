package controller;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.Item;
import view.tm.Itemtm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StoreFormController implements Initializable {
    public AnchorPane StoreRoot;
    public TableView<Itemtm> tbStore;
    public TableColumn tbcode;
    public TableColumn tbDiscription;
    public TableColumn tbpacksize;
    public TableColumn tbqty;
    public TableColumn tbunitprice;
    public TableColumn tbitemtype;
    public JFXTextField txtDiscrion;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtpacksize;
    public JFXTextField txtQtyOnHand;
    public JFXButton addBtn;
    public JFXTextField txtCode;
    public JFXTextField txtItemType;
    public ImageView deleteBtn;
    public ImageView updateBtn;
    public ImageView addImgBtn;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tbStore.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("Item_Code"));
        tbStore.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("description"));
        tbStore.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("packSize"));
        tbStore.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tbStore.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tbStore.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("Item_Type"));

        getAllCustomerDetails();
    }

    private void getAllCustomerDetails() {

        ArrayList<Item> items = new ArrayList<>();
        ObservableList<Itemtm> tmObservableList = FXCollections.observableArrayList();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                items.add(new Item(resultSet.getString(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getInt(4),
                        resultSet.getDouble(5), resultSet.getString(6)));
            }
            for (Item itm : items) {
                tmObservableList.add(new Itemtm(itm.getItem_Code(),itm.getDescription(),itm.getPackSize(),
                        itm.getQtyOnHand(),itm.getUnitPrice(),itm.getItem_Type()));
            }
            tbStore.setItems(tmObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void discriptionOnAction(ActionEvent actionEvent) {txtUnitPrice.requestFocus(); }
    public void unitPriceOnAction(ActionEvent actionEvent) {txtpacksize.requestFocus(); }
    public void packSizeOnAction(ActionEvent actionEvent) {txtItemType.requestFocus(); }
    public void itemTypeOnAction(ActionEvent actionEvent) {txtQtyOnHand.requestFocus(); }
    public void codeOnAction(ActionEvent actionEvent) {txtDiscrion.requestFocus(); }

    public void btnBackButton(ActionEvent actionEvent) throws IOException {
        StoreRoot.getChildren().clear();
        StoreRoot.getChildren().add(FXMLLoader.load(this.getClass().getResource("../view/DashBoard.fxml")));
    }

    public void addItemOnAction(ActionEvent actionEvent) {
        Item item = new Item(txtCode.getText(),txtDiscrion.getText(),txtpacksize.getText(),
                Integer.parseInt(txtQtyOnHand.getText()),Double.parseDouble(txtUnitPrice.getText()),
                txtItemType.getText());
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO item " +
                    "VALUES(?,?,?,?,?,?)");
            preparedStatement.setObject(1, item.getItem_Code());
            preparedStatement.setObject(2, item.getDescription());
            preparedStatement.setObject(3, item.getPackSize());
            preparedStatement.setObject(4, item.getQtyOnHand());
            preparedStatement.setObject(5, item.getUnitPrice());
            preparedStatement.setObject(6, item.getItem_Type());
            int add = preparedStatement.executeUpdate();
            if (add > 0) {
                new Alert(Alert.AlertType.CONFIRMATION, "Saved!", ButtonType.OK).show();
                txtCode.requestFocus();
            } else {
                new Alert(Alert.AlertType.WARNING, "Try Again!", ButtonType.OK).show();
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        clearTextOnAction();
    }


    public void updateOnAction(ActionEvent actionEvent) {
        Item item = new Item(txtCode.getText(),txtDiscrion.getText(),txtpacksize.getText(),
                Integer.parseInt(txtQtyOnHand.getText()),Double.parseDouble(txtUnitPrice.getText()),
                txtItemType.getText());

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE item SET " +
                    "description=?, packSize=?,qtyOnHand=?,unitPrice=?, Item_Type=? WHERE Item_Code=?");
            preparedStatement.setObject(1, item.getDescription());
            preparedStatement.setObject(2, item.getPackSize());
            preparedStatement.setObject(3, item.getQtyOnHand());
            preparedStatement.setObject(4, item.getUnitPrice());
            preparedStatement.setObject(5, item.getItem_Type());
            int add = preparedStatement.executeUpdate();
            if (add > 0) {
                new Alert(Alert.AlertType.CONFIRMATION, "Updated!", ButtonType.OK).show();
                txtCode.requestFocus();
            } else {
                new Alert(Alert.AlertType.WARNING, "Try Again!", ButtonType.OK).show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        clearTextOnAction();
    }

    private void clearTextOnAction() {
        txtCode.clear();
        txtDiscrion.clear();
        txtpacksize.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        txtItemType.clear();
    }


    public void deleteCustomerOnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("DELETE FROM item WHERE Item_Code=?");
            preparedStatement.setObject(1,txtCode.getText());
            int executeUpdate = preparedStatement.executeUpdate();
            if (executeUpdate > 0){
                new Alert(Alert.AlertType.CONFIRMATION, "Supplier Delete Successfully.....!", ButtonType.OK).
                        show();
                txtCode.requestFocus();
            }else {
                new Alert(Alert.AlertType.WARNING, "Supplier Delete Unsuccessfully.....", ButtonType.OK).
                        show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        clearTextOnAction();
    }

    public void searchCustomerOnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT * FROM item WHERE Item_Code=?");
            preparedStatement.setObject(1,txtCode.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                txtDiscrion.setText(resultSet.getString(2));
                txtpacksize.setText(resultSet.getString(3));
                txtQtyOnHand.setText(resultSet.getString(4));
                txtUnitPrice.setText(resultSet.getString(5));
                txtItemType.setText(resultSet.getString(6));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
