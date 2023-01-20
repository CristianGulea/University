import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.awt.*;
import java.io.IOException;

public class ManageCatalogController implements IObserver{

    public javafx.scene.control.Button addButtonManageCatalog;

    public javafx.scene.control.Button deleteButtonManageCatalog;

    public Button updateButtonManageCatalog;

    private IService service;

    public void setService(IService service) {
        this.service = service;
    }

    public void handleAddProductButton() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/addProductWindow.fxml"));
        AnchorPane panel = null;
        try {panel = fxmlLoader.load();} catch (IOException e) {e.printStackTrace();}
        AddProductController addProductController = fxmlLoader.getController();
        addProductController.setService(service);
        Stage addProductWindowStage = new Stage();
        assert panel != null;
        Scene scene = new Scene(panel, 352, 359);
        addProductWindowStage.setScene(scene);
        addProductWindowStage.show();
    }

    public void handleDeleteProductButton() throws AppException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/deleteProductWindow.fxml"));
        AnchorPane panel = null;
        try {panel = fxmlLoader.load();} catch (IOException e) {e.printStackTrace();}
        DeleteProductController deleteProductController = fxmlLoader.getController();
        deleteProductController.setService(service);
        Stage deleteProductWindowStage = new Stage();
        assert panel != null;
        Scene scene = new Scene(panel, 320, 400);
        deleteProductWindowStage.setScene(scene);
        deleteProductWindowStage.show();
        service.addObserver(deleteProductController);
    }

    public void handleUpdateProductButton() throws AppException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/updateProductWindow.fxml"));
        AnchorPane panel = null;
        try {panel = fxmlLoader.load();} catch (IOException e) {e.printStackTrace();}
        UpdateProductController updateProductController = fxmlLoader.getController();
        updateProductController.setService(service);
        Stage updateProductWindowStage = new Stage();
        assert panel != null;
        Scene scene = new Scene(panel, 607, 400);
        updateProductWindowStage.setScene(scene);
        updateProductWindowStage.show();
        service.addObserver(updateProductController);
    }

    @Override
    public void notifyApp() {

    }
}
