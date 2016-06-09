package draw.model;

import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

/**
 * Created by Юлия on 05.06.2016.
 */
public class Layer implements Serializable{

    private transient ObjectProperty<Canvas> canvas;
    private StringProperty name;
    private transient ObjectProperty<GraphicsContext> gc;
    private BooleanProperty visible;

    public Layer(Canvas canvas, String name, GraphicsContext graphicsContext){
        this.canvas = new SimpleObjectProperty<Canvas>(canvas);
        this.name = new SimpleStringProperty(name);
        this.gc = new SimpleObjectProperty<GraphicsContext>(graphicsContext);
        visible = new SimpleBooleanProperty(true);
    }

    public Canvas getCanvas() {
        return canvas.get();
    }

    public ObjectProperty<Canvas> canvasProperty() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas.set(canvas);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public GraphicsContext getGc() {
        return gc.get();
    }

    public ObjectProperty<GraphicsContext> gcProperty() {
        return gc;
    }

    public void setGc(GraphicsContext gc) {
        this.gc.set(gc);
    }

    public boolean getVisible() {
        return visible.get();
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }
}
