import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import java.util.List;

public class DeleteProductController implements IObserver{

    @FXML
    private TableView tableDeleteW;

    @FXML
    private TextField idTextFieldDeleteW;

    @FXML
    private Button deleteButtonDeleteW;

    private IService service;

    public void setService(IService service) throws AppException {
        this.service = service;
        initTable();
        idTextFieldDeleteW.setDisable(true);
    }

    public void handleDeleteButton(MouseEvent mouseEvent) throws AppException {
        if (!idTextFieldDeleteW.getText().isEmpty()) {
            service.deleteProduct(idTextFieldDeleteW.getText());
            Stage stage = (Stage) idTextFieldDeleteW.getScene().getWindow();
            stage.close();
        }
    }

    public void initTable() throws AppException {
        List<Product> productList = service.findAllProducts();
        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn quantityColumn = new TableColumn("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn pricePerUnitColumn = new TableColumn("PricePerUnit");
        pricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        tableDeleteW.getColumns().addAll(idColumn, nameColumn, quantityColumn, pricePerUnitColumn);

        for(Product product:productList){
            tableDeleteW.getItems().add(product);
        }
    }

    public void selectItem() {
        String[] firstSplit = tableDeleteW.getSelectionModel().getSelectedItem().toString().split(",");
        String[] secondSplit = firstSplit[0].split("=");
        idTextFieldDeleteW.setText(String.valueOf(secondSplit[1]));
    }

    @Override
    public void notifyApp(){
        Platform.runLater(()->{
            try {
                initTable();
            } catch (AppException e) {
                e.printStackTrace();
            }
        });
    }
}
