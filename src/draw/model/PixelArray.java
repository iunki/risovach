package draw.model;

import java.io.Serializable;

/**
 * Created by Юлия on 09.06.2016.
 */
public class PixelArray implements Serializable {

    private int height;
    private int width;
    private String name;
    private boolean visible;
    private double[][] red;
    private double[][] green;
    private double[][] blue;
    private double[][] opacity;

    public PixelArray(String name, boolean visible, int height, int width){
        this.visible = visible;
        this.name = name;
        this.height = height;
        this.width = width;
        red = new double[width][height];
        blue = new double[width][height];
        green = new double[width][height];
        opacity = new double[width][height];


    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[][] getRed() {
        return red;
    }

    public void setRed(double[][] red) {
        this.red = red;
    }

    public double[][] getGreen() {
        return green;
    }

    public void setGreen(double[][] green) {
        this.green = green;
    }

    public double[][] getBlue() {
        return blue;
    }

    public void setBlue(double[][] blue) {
        this.blue = blue;
    }

    public double[][] getOpacity() {
        return opacity;
    }

    public void setOpacity(double[][] opacity) {
        this.opacity = opacity;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
