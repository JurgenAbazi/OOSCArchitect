package de.rwth_aachen.swc.oosc.architect.figures.furnitures;

import org.jhotdraw.draw.ImageFigure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.jhotdraw.draw.AttributeKeys.*;

public class FurnitureFigure extends ImageFigure {
    private BufferedImage furnitureImage;
    private String imagePath = "";

    public FurnitureFigure() {
        super();
        set(FILL_COLOR, null);
        setAttributeEnabled(FILL_COLOR, false);
        set(STROKE_COLOR, null);
        setAttributeEnabled(STROKE_COLOR, false);
        set(STROKE_WIDTH, 0d);
        setBufferedImage(getFurnitureImage());
        setConnectable(false);
    }

    private BufferedImage getFurnitureImage() {
        if (furnitureImage == null) {
            File file = new File(getImagePath());
            try {
                furnitureImage = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return furnitureImage;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        furnitureImage = null;
        setBufferedImage(getFurnitureImage());
    }
}
