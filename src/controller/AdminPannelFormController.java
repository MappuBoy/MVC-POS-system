package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPannelFormController {
    public TextField txtCurrentDate;
    public TextField txtAdminCurrentDate;
    public JFXButton btnBack;
    public AnchorPane managerRoot;

    public void dashBoardOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ManagerDashBoardForm");
    }

    public void storeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("StoreForm");
    }

    public void customerOnAction(ActionEvent actionEvent) throws IOException {
        setUi("CustomerForm");
    }

    public void backOnAction(ActionEvent actionEvent) throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }
    private void setUi(String location) throws IOException {
        managerRoot.getChildren().clear();
        managerRoot.getChildren().add(FXMLLoader.load(this.getClass().getResource("/view/" + location + ".fxml")));
    }
}
