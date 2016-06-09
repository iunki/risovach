package draw.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Юлия on 05.06.2016.
 */
public class DialogNewImageController {

    @FXML
    private TextField width;
    @FXML
    private TextField height;

    private MainController mainController;

    private Stage dialogStage;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(MainController mainController){
        this.mainController = mainController;
    }

    @FXML
    public void buttonOk(){
        mainController.refresh();
        mainController.newImage(Integer.valueOf(width.getText()), Integer.valueOf(height.getText()));
        dialogStage.close();
    }

    @FXML
    public void buttonCancel(){
        dialogStage.close();
    }


}
