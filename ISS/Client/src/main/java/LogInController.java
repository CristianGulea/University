import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.io.IOException;

public class LogInController {
    @FXML
    private TextField usernameTFLogInW;
    @FXML
    private TextField passwordTFLogInW;
    @FXML
    private Button logInButton;

    private IService service;

    public void setService(IService service) {this.service = service;}

    public IService getService() {return service;}

    public void handleLogInButton() throws AppException {
        boolean isValid = service.logInEmployee(usernameTFLogInW.getText(), passwordTFLogInW.getText());
                    if (isValid){
                        Employee employee = service.findEmployeeByUsername(usernameTFLogInW.getText());
                        if (! employee.getIsAdmin()) {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/manageOrders.fxml"));
                            AnchorPane panel = null;
                            try {
                                panel = fxmlLoader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                }
                ManageOrderController manageOrderController = fxmlLoader.getController();
                manageOrderController.setService(service);
                Stage menuWindowStage = new Stage();
                assert panel != null;
                Scene scene = new Scene(panel, 699, 513);
                menuWindowStage.setScene(scene);
                menuWindowStage.show();
                Stage stage = (Stage) logInButton.getScene().getWindow();
                stage.close();
                service.addObserver(manageOrderController);
            }
            else{
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/manageCatalogWindow.fxml"));
                AnchorPane panel = null;
                try {panel = fxmlLoader.load();} catch (IOException e) {e.printStackTrace();}
                ManageCatalogController manageCatalogController = fxmlLoader.getController();
                manageCatalogController.setService(service);
                Stage manageWindowStage = new Stage();
                assert panel != null;
                Scene scene = new Scene(panel, 600, 400);
                manageWindowStage.setScene(scene);
                manageWindowStage.show();
                Stage stage = (Stage) logInButton.getScene().getWindow();
                stage.close();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Message Here...");
            alert.setHeaderText("Look, an error");
            alert.setContentText("Failed Login Attempt");
            alert.show();
        }
    }
}
