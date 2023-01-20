
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AddProductController {

    @FXML
    private TextField nameTextFieldAddW;

    @FXML
    private TextField quantityTextFieldAddW;

    @FXML
    private TextField pricePerUnitTextFieldAddW;

    @FXML
    private Button addButtonAddW;

    private IService service;

    public void setService(IService service) {
        this.service = service;
    }

    public void handlerAddButton() throws AppException {
        boolean isValid = service.addProduct(nameTextFieldAddW.getText(), quantityTextFieldAddW.getText(), pricePerUnitTextFieldAddW.getText());
        if (isValid){
            Stage stage = (Stage) pricePerUnitTextFieldAddW.getScene().getWindow();
            stage.close();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Message Here...");
            alert.setHeaderText("Look, an error");
            alert.setContentText("Failed Add Product");
            alert.show();
        }
    }
}
