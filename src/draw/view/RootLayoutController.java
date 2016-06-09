package draw.view;

import draw.MainApp;
import draw.model.SerializableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;

/**
 * Created by Юлия on 05.06.2016.
 */
public class RootLayoutController {

    private MainApp mainApp;

    private MainController mainController;

    private String curPath;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public RootLayoutController() {
    }

    @FXML
    private void initialize() {

    }

    @FXML
    public void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("WTF", "*.wtf"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            mainController.refresh();
            curPath = file.getAbsolutePath();
            if (fileChooser.getSelectedExtensionFilter().equals(fileChooser.getExtensionFilters().get(0))) {
                try {
                    FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                    ObjectInputStream oin = new ObjectInputStream(fis);
                    SerializableList list = (SerializableList) oin.readObject();
                    mainController.setSerializableLayerList(list);
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mainController.setImage(file);
            }
        }
    }

    @FXML
    public void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("WTF", "*.wtf"),
                new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (file != null) {
            if (fileChooser.getSelectedExtensionFilter().equals(fileChooser.getExtensionFilters().get(0))) {
                write(file);
            } else {
                mainController.saveImage(file);
            }
        }
    }

    @FXML
    public void save() {
        if (curPath == null) {
            saveAs();
        } else {
            File file = new File(curPath);
            write(file);
        }
    }

    @FXML
    public void newImage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/DialogNewImage.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            DialogNewImageController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainController(mainController);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void resize() {
        mainController.resize();
    }

    public void write(File file) {
        SerializableList list = mainController.getSerializableLayerList();
        System.out.println("SIZE: " + mainController.getLayersList().size());
        try {
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
