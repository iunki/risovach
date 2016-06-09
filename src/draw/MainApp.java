package draw; /**
 * Created by Юлия on 02.06.2016.
 */

import draw.view.MainController;
import draw.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootLayoutController rootController;
    private MainController mainController;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Рисовач ¯ \\ _ (ツ) _ / ¯");

        this.primaryStage.getIcons().add(new Image("file:src/resources/smiley.png"));

        initRootLayout();
        showMainView();
        setControllers();
    }

    public void initRootLayout(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            rootController = loader.getController();
            rootController.setMainApp(this);
            primaryStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void showMainView(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
            AnchorPane mainView = (AnchorPane)loader.load();
            rootLayout.setCenter(mainView);

            mainController = loader.getController();
            mainController.setMainApp(this);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setControllers(){
        rootController.setMainController(mainController);
        mainController.setRootController(rootController);
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
