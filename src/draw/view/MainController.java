package draw.view;

import draw.MainApp;
import draw.model.Layer;
import draw.model.PixelArray;
import draw.model.SerializableList;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by Юлия on 02.06.2016.
 */
public class MainController {

    @FXML
    private AnchorPane root;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Slider widthSlider;
    @FXML
    private Label widthLabel;
    @FXML
    private Slider opacitySlider;
    @FXML
    private Label opacityLabel;
    @FXML
    private Slider blurSlider;
    @FXML
    private Label blurLabel;
    @FXML
    private Canvas canvasPreview;
    @FXML
    private Pane pane;
    @FXML
    private TableView<Layer> layerTable;
    @FXML
    private TableColumn<Layer, String> layerColumn;
    @FXML
    private TableColumn<Layer, Boolean> visibleColumn;
    @FXML
    private ToggleButton tgBrush;
    @FXML
    private ToggleButton tgEraser;

    private ObservableList<Layer> layersList = FXCollections.observableArrayList();

    private GraphicsContext gcPreview;

    private GraphicsContext curGC;

    private GaussianBlur blur;
    private Double width;
    private Double height;
    private int curIndex;

    private int i = 1;

    MainApp mainApp;
    RootLayoutController rootController;

    public MainController() {
    }

    public void setImage(File file) {//фотка
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            width = image.getWidth();
            height = image.getHeight();
            Canvas canvas = new Canvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            curGC = gc;
            gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
            layersList.add(new Layer(canvas, "layer " + (i++), gc));
            pane.getChildren().add(canvas);
            setPaneSize();

            scrollPane.resize(width, height);
            orderLayers();
            setContext(gc);
        } catch (IOException e) {
            System.out.println("AAAAAAAAAAAA");
            e.printStackTrace();
        }
    }

    public void newImage(int width, int height) {
        this.width = (double) width;
        this.height = (double) height;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        curGC = gc;
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
        layersList.add(new Layer(canvas, "layer " + (i++), gc));
        pane.getChildren().add(canvas);
        orderLayers();
        setContext(gc);
        setPaneSize();
    }  //новый рисунок

    private void setPaneSize() {
        pane.setMinSize(width, height);
        pane.setMaxSize(width, height);
        pane.setPrefSize(width, height);
    }

    @FXML
    private void initialize() {
        ToggleGroup group = new ToggleGroup();
        tgBrush.setToggleGroup(group);
        tgEraser.setToggleGroup(group);
        tgBrush.setSelected(true);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        gcPreview = canvasPreview.getGraphicsContext2D();
        gcPreview.setLineCap(StrokeLineCap.ROUND);
        pane.setMaxSize(0, 0);
        layerTable.setItems(layersList);
        layerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            curIndex = layersList.indexOf(newValue);
            curGC = newValue.getGc();
            setContext(curGC);
        });
        pane.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                curGC.beginPath();
                curGC.moveTo(event.getX(), event.getY());
                curGC.stroke();
            }
        });
        pane.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                if (tgBrush.isSelected()) {
                    curGC.lineTo(event.getX(), event.getY());
                    curGC.stroke();
                } else {
                    curGC.setEffect(null);
                    curGC.clearRect(event.getX() - widthSlider.getValue() / 2, event.getY() - widthSlider.getValue() / 2, widthSlider.getValue(), widthSlider.getValue());
                }
            }
        });
        pane.setOnMouseReleased(event -> {
            if (tgEraser.isSelected())
                curGC.setEffect(blur);
        });
        layerColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        visibleColumn.setCellValueFactory(cellData -> cellData.getValue().visibleProperty());
        colorPicker.setValue(Color.BLACK);
        drawPreview();
        blur = new GaussianBlur(0);
    }


    public SerializableList getSerializableLayerList() {
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        SerializableList list = new SerializableList();
        for (int i = 0; i < layersList.size(); i++) {
            Layer layer = layersList.get(i);
            PixelArray pixels = new PixelArray(layer.getName(), layer.getVisible(), height.intValue(), width.intValue());
            WritableImage wim = new WritableImage(width.intValue(), height.intValue());
            layer.getCanvas().snapshot(sp, wim);
            PixelReader pixelReader = wim.getPixelReader();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = pixelReader.getColor(x, y);
                    pixels.getRed()[x][y] = color.getRed();
                    pixels.getGreen()[x][y] = color.getGreen();
                    pixels.getBlue()[x][y] = color.getBlue();
                    pixels.getOpacity()[x][y] = color.getOpacity();
                }
            }
            list.getList().add(pixels);
        }
        return list;
    }

    public void setSerializableLayerList(SerializableList list) {
        refresh();
        int height = list.getList().get(0).getHeight();
        int width = list.getList().get(0).getWidth();
        this.width = (double) width;
        this.height = (double) height;
        setPaneSize();

        for (int i = 0; i < list.getList().size(); i++) {
            PixelArray pixels = list.getList().get(i);
            WritableImage wim = new WritableImage(width, height);
            PixelWriter pixelWriter = wim.getPixelWriter();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(pixels.getRed()[x][y], pixels.getGreen()[x][y], pixels.getBlue()[x][y], pixels.getOpacity()[x][y]);
                    pixelWriter.setColor(x, y, color);
                }
            }
            Canvas canvas = new Canvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(wim, 0, 0, width, height);
            Layer layer = new Layer(canvas, list.getList().get(i).getName(), gc);
            layer.setVisible(list.getList().get(i).isVisible());
            canvas.setVisible(layer.getVisible());
            layersList.add(layer);
            pane.getChildren().add(canvas);
        }
        orderLayers();
        curIndex = 0;
        curGC = layersList.get(0).getGc();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setContext(GraphicsContext gc) {
        gc.setLineCap(StrokeLineCap.ROUND);

        widthSlider.valueProperty().addListener((ov, old_val, new_val) -> {
            gc.setLineWidth(new_val.doubleValue());
            int v = (int) new_val.doubleValue();
            widthLabel.setText(v + "");
            drawPreview();
        });

        opacitySlider.valueProperty().addListener((ov, old_val, new_val) -> {
            gc.setGlobalAlpha(new_val.doubleValue() / 100);
            int v = (int) new_val.doubleValue();
            opacityLabel.setText(v + "%");
            drawPreview();
        });

        blurSlider.valueProperty().addListener((ov, old_val, new_val) -> {
            blur.setRadius(new_val.doubleValue());
            gc.setEffect(blur);
            blurLabel.setText((int) new_val.doubleValue() + "");
            drawPreview();
        });
        gc.setStroke(colorPicker.getValue());
        gc.setLineWidth(widthSlider.getValue());
        gc.setGlobalAlpha(opacitySlider.getValue() / 100);
        gc.setEffect(blur);
    }

    @FXML
    public void addLayer() {
        Canvas canvas = new Canvas(width, height);
        Layer layer = new Layer(canvas, "layer " + (i++), canvas.getGraphicsContext2D());
        layersList.add(curIndex, layer);
        pane.getChildren().add(0, canvas);
        orderLayers();
        setContext(curGC);
    }

    public void orderLayers() {
        for (int i = layersList.size() - 1; i >= 0; i--) {
            layersList.get(i).getCanvas().toFront();
        }
    }

    public void resize() {
        if (height != null) {
            mainApp.getPrimaryStage().setWidth(width + 304);
            mainApp.getPrimaryStage().setHeight(height + 67);
        } else {
            root.setMinHeight(height);
            mainApp.getPrimaryStage().setWidth(950);
            mainApp.getPrimaryStage().setHeight(700);
        }
    }

    @FXML
    public void setVisible() {
        boolean b = layersList.get(curIndex).getVisible();
        if (b) {
            layersList.get(curIndex).setVisible(false);
            layersList.get(curIndex).getCanvas().setVisible(false);

        } else {
            layersList.get(curIndex).setVisible(true);
            layersList.get(curIndex).getCanvas().setVisible(true);
        }
    }

    public void saveImage(File file) {
        WritableImage wim = new WritableImage(width.intValue(), height.intValue());
        pane.snapshot(null, wim);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
        } catch (IOException e) {
            System.out.println("loshara blat'");
        }
    }

    public void refresh() {
        layersList.clear();
        pane.getChildren().removeAll();
        i = 1;
    }

    @FXML
    public void down() {
        Collections.swap(layersList, curIndex, curIndex + 1);
        orderLayers();
    }

    @FXML
    public void up() {
        Collections.swap(layersList, curIndex, curIndex - 1);
        orderLayers();
    }

    @FXML
    public void deleteLayer() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (layersList.size() == 1) {
            alert.setTitle("Так нельзя");
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setContentText("Please add one more layer before delete.");
            alert.showAndWait();
        } else {
            pane.getChildren().remove(layersList.get(curIndex));
            layersList.remove(curIndex);
            orderLayers();
            setContext(curGC);
        }
    }

    @FXML
    public void colorPick() {
        curGC.setStroke(colorPicker.getValue());
        drawPreview();
    }

    public void drawPreview() {
        gcPreview.clearRect(0, 0, canvasPreview.getWidth(), canvasPreview.getHeight());
        gcPreview.setStroke(colorPicker.getValue());
        gcPreview.setLineWidth(widthSlider.getValue());
        gcPreview.setGlobalAlpha(opacitySlider.getValue() / 100);


        double y = canvasPreview.getHeight() / 2;
        gcPreview.strokeLine(15, y, canvasPreview.getWidth() - 15, y);
        gcPreview.applyEffect(blur);
    }

    public void mouseReleased(MouseEvent event) {
        //отпущена
    }

    public void setRootController(RootLayoutController rootController) {
        this.rootController = rootController;
    }

    public ObservableList<Layer> getLayersList() {
        return layersList;
    }

    public void setLayersList(ObservableList<Layer> layersList) {
        this.layersList = layersList;
    }
}
