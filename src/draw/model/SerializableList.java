package draw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Юлия on 09.06.2016.
 */
public class SerializableList implements Serializable {

    private List<PixelArray> list;

    public SerializableList(){
        list = new ArrayList<>();
    }

    public List<PixelArray> getList() {
        return list;
    }

    public void setList(List<PixelArray> list) {
        this.list = list;
    }
}
