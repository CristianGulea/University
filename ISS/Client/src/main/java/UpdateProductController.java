import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import java.util.List;

public class UpdateProductController implements IObserver{
    @FXML
    private TextField idTextFieldUpdateW;

    @FXML
    private TextField nameTextFieldUpdateW;

    @FXML
    private TextField quantityTextFieldUpdateW;

    @FXML
    private TextField pricePerUnitTextFieldUpdateW;

    @FXML
    private Button updateButtonUpdateW;

    @FXML
    private TableView tableUpdateW;

    private IService service;

    public void setService(IService service) throws AppException {
        this.service = service;
        initTable();

    }


    public void handleUpdateButton() throws AppException {
        boolean isValid = service.updateProduct(nameTextFieldUpdateW.getText(), quantityTextFieldUpdateW.getText(), pricePerUnitTextFieldUpdateW.getText(), idTextFieldUpdateW.getText());
        if (isValid){
            clearCells();
            initTable();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Message Here...");
            alert.setHeaderText("Look, an error");
            alert.setContentText("Failed Update Product");
            alert.show();
        }
    }

    private void clearCells(){
        pricePerUnitTextFieldUpdateW.clear();
        quantityTextFieldUpdateW.clear();
        nameTextFieldUpdateW.clear();
        idTextFieldUpdateW.clear();
    }
    private void initTable() throws AppException {
        tableUpdateW.getColumns().clear();
        List<Product> productList = this.service.findAllProducts();
        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn quantityColumn = new TableColumn("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn pricePerUnitColumn = new TableColumn("PricePerUnit");
        pricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        tableUpdateW.getColumns().addAll(idColumn, nameColumn, quantityColumn, pricePerUnitColumn);

        for ( int i = 0; i<tableUpdateW.getItems().size(); i++) {
            tableUpdateW.getItems().clear();
        }

        for(Product product:productList){
            tableUpdateW.getItems().add(product);
        }
    }

    @FXML
    private void selectItem() {
        String[] firstSplit = tableUpdateW.getSelectionModel().getSelectedItem().toString().split(",");
        String[] price = firstSplit[2].split("=");
        String quantity = firstSplit[3].split("=")[1].substring(0, firstSplit[3].split("=").length-1);
        String[] name = firstSplit[1].split("'");
        String[] id = firstSplit[0].split("=");
        idTextFieldUpdateW.setText(String.valueOf(id[1]));
        nameTextFieldUpdateW.setText(name[1]);
        quantityTextFieldUpdateW.setText(quantity);
        pricePerUnitTextFieldUpdateW.setText(price[1]);
    }

    @Override
    public void notifyApp() {
        Platform.runLater(()->{
            try {
                initTable();
            } catch (AppException e) {
                e.printStackTrace();
            }
        });
    }
}
