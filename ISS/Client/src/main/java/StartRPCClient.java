import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class StartRPCClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        String defaultServer = "localhost";
        int defaultChatPort = 55555;
        IService service = new RPCProxy(defaultServer, defaultChatPort);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("logInWindow.fxml"));
        AnchorPane panel = fxmlLoader.load();
        LogInController controller = fxmlLoader.getController();
        controller.setService(service);
        Scene scene = new Scene(panel, 580, 460);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch();
    }
}
