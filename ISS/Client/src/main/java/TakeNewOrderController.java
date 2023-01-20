import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.List;

public class TakeNewOrderController implements IObserver{
    @FXML
    public TableView productsTableViewNewOrder;

    @FXML
    public Button placeOrderButton;

    @FXML
    public TableView companyTableViewNewOrder;

    @FXML
    public TextField quantityTextFieldNewOrder;

    @FXML
    public TextField priceTextFieldNewOrder;

    @FXML
    public TextField nameTextFieldNewOrder;

    @FXML
    public TextField commentsTextFieldNewOrder;

    private IService service;

    private Company company = null;
    private Product product = null;

    public void setService(IService service) throws AppException {this.service = service; initProductTable(); initCompanyTable();}

    private void initProductTable() throws AppException {
        productsTableViewNewOrder.getColumns().clear();
        List<Product> productList = this.service.findAllProducts();
        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn quantityColumn = new TableColumn("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn pricePerUnitColumn = new TableColumn("PricePerUnit");
        pricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        productsTableViewNewOrder.getColumns().addAll(idColumn, nameColumn, quantityColumn, pricePerUnitColumn);

        for ( int i = 0; i<productsTableViewNewOrder.getItems().size(); i++) {
            productsTableViewNewOrder.getItems().clear();
        }

        for(Product product:productList){
            productsTableViewNewOrder.getItems().add(product);
        }
    }

    private void initCompanyTable() throws AppException {
        companyTableViewNewOrder.getColumns().clear();
        List<Company> companies = this.service.findAllCompanies();
        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn addressColumn = new TableColumn("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn telephoneColumn = new TableColumn("Telephone");
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        companyTableViewNewOrder.getColumns().addAll(idColumn, nameColumn, addressColumn, telephoneColumn);

        for ( int i = 0; i<companyTableViewNewOrder.getItems().size(); i++) {
            companyTableViewNewOrder.getItems().clear();
        }

        for(Company company:companies){
            companyTableViewNewOrder.getItems().add(company);
        }
    }


    public void selectProduct() {
        String[] firstSplit = productsTableViewNewOrder.getSelectionModel().getSelectedItem().toString().split(",");
        String[] price = firstSplit[2].split("=");
        String quantity = firstSplit[3].split("=")[1].substring(0, firstSplit[3].split("=").length-1);
        String[] name = firstSplit[1].split("'");
        String[] id = firstSplit[0].split("=");
        nameTextFieldNewOrder.setText(name[1]);
        quantityTextFieldNewOrder.setText(quantity);
        priceTextFieldNewOrder.setText(price[1]);
        product = new Product();
        product.setQuantity(Integer.parseInt(quantity));
        product.setName(name[1]);
        product.setPricePerUnit(Integer.parseInt(price[1]));
        product.setId(Integer.parseInt(id[1]));
    }


    public void selectCompany() {
        int id = Integer.parseInt(companyTableViewNewOrder.getSelectionModel().getSelectedItem().toString().split("id=")[1].split(",")[0]);
        String name = companyTableViewNewOrder.getSelectionModel().getSelectedItem().toString().split("name=")[1].split("'")[1];
        String address = companyTableViewNewOrder.getSelectionModel().getSelectedItem().toString().split("address=")[1].split("'")[1];
        String telephone = companyTableViewNewOrder.getSelectionModel().getSelectedItem().toString().split("telephone=")[1].split("'")[1];
        company = new Company();
        company.setTelephone(telephone);
        company.setAddress(address);
        company.setId(id);
        company.setName(name);
        System.out.println(company);
    }


    public void handlerPlaceOrderButton() throws AppException {
        if ((company != null) && (product != null)){
            String quantity = quantityTextFieldNewOrder.getText();
            int price = product.getPricePerUnit() * Integer.parseInt(quantity);
            boolean sem = this.service.addOrder(commentsTextFieldNewOrder.getText(), String.valueOf(price), "pending", String.valueOf(company.getId()), String.valueOf(product.getId()), quantity);
            Stage stage = (Stage) placeOrderButton.getScene().getWindow();
            stage.close();
            if (!sem){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Message Here...");
                alert.setHeaderText("Look, an error");
                alert.setContentText("Failed Take New Order Attempt");
                alert.show();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Message Here...");
            alert.setHeaderText("Look, an error");
            alert.setContentText("Failed Take New Order Attempt");
            alert.show();
        }
    }

    @Override
    public void notifyApp() {
        Platform.runLater(()->{
            try {
                initCompanyTable();
                initProductTable();
            } catch (AppException e) {
                e.printStackTrace();
            }
        });
    }
}
