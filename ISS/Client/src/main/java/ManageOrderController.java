import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ManageOrderController implements IObserver {
    @FXML
    private TableView tableManageOrdersMOW;

    @FXML
    private RadioButton radioButtonDelivered;

    @FXML
    private RadioButton radioButtonPending;

    @FXML
    private TableView tableConsultCatalogMOW;

    @FXML
    private Button takeNewOrderButtonMOW;

    @FXML
    private Button deliverButton;

    private IService service;

    public void setService(IService service) throws AppException {
        this.service = service;
        initConsultTable();
        initManageOrdersTable();
    }

    private void initConsultTable() throws AppException {
        tableConsultCatalogMOW.getColumns().clear();
        List<Product> productList = this.service.findAllProducts();
        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn quantityColumn = new TableColumn("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn pricePerUnitColumn = new TableColumn("PricePerUnit");
        pricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        tableConsultCatalogMOW.getColumns().addAll(idColumn, nameColumn, quantityColumn, pricePerUnitColumn);

        for ( int i = 0; i<tableConsultCatalogMOW.getItems().size(); i++) {
            tableConsultCatalogMOW.getItems().clear();
        }

        for(Product product:productList){
            tableConsultCatalogMOW.getItems().add(product);
        }
    }

    private void initManageOrdersTable() throws AppException {
        tableManageOrdersMOW.getColumns().clear();
        List<Torder> productList = this.service.findAllOrders();
        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn quantityColumn = new TableColumn("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn priceColumn = new TableColumn("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn productId = new TableColumn("ProductId");
        productId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        TableColumn companyId = new TableColumn("CompanyId");
        companyId.setCellValueFactory(new PropertyValueFactory<>("companyId"));
        tableManageOrdersMOW.getColumns().addAll(idColumn, productId, quantityColumn, priceColumn, companyId);

        for ( int i = 0; i<tableManageOrdersMOW.getItems().size(); i++) {
            tableManageOrdersMOW.getItems().clear();
        }

        for(Torder torder:productList){
            tableManageOrdersMOW.getItems().add(torder);
        }
    }

    public void setRadioButton() {
        String selectedOrder = tableManageOrdersMOW.getSelectionModel().getSelectedItem().toString();
        if (selectedOrder.contains("pending")) {
            radioButtonPending.setSelected(true);
            deliverButton.setDisable(false);
        }
        else
        {
            radioButtonDelivered.setSelected(true);
            deliverButton.setDisable(true);
        }

    }

    public void handlerDeliverButton() throws AppException {
        if (tableManageOrdersMOW.getSelectionModel().getSelectedItem() != null) {
            String[] firstSplit = tableManageOrdersMOW.getSelectionModel().getSelectedItem().toString().split(",");
            String[] id = firstSplit[0].split("id=");
            Torder order = this.service.findByOrderId(Integer.parseInt(id[1]));
            this.service.updateOrderStatus(order);
            initManageOrdersTable();
        }
    }

    public void handleTakeNewOrderButtonMOW() throws AppException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/takeNewOrder.fxml"));
        AnchorPane panel = null;
        try {panel = fxmlLoader.load();} catch (IOException e) {e.printStackTrace();}
        TakeNewOrderController takeNewOrderController = fxmlLoader.getController();
        takeNewOrderController.setService(service);
        Stage manageWindowStage = new Stage();
        assert panel != null;
        Scene scene = new Scene(panel, 608, 566);
        manageWindowStage.setScene(scene);
        manageWindowStage.show();
        service.addObserver(takeNewOrderController);
    }

    @Override
    public void notifyApp() {
        Platform.runLater(() -> {
            try {
                initManageOrdersTable();
                initConsultTable();
            } catch (AppException e) {
                e.printStackTrace();
            }
        });
    }
}
