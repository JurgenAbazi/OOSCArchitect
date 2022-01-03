package de.rwth_aachen.swc.oosc.architect.figures.furnitures;

import org.jhotdraw.draw.ImageFigure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.jhotdraw.draw.AttributeKeys.*;

public abstract class FurnitureFigure extends ImageFigure {
    private BufferedImage furnitureIcon;

    protected FurnitureFigure() {
        super();
        set(FILL_COLOR, null);
        setAttributeEnabled(FILL_COLOR, false);
        set(STROKE_COLOR, null);
        setAttributeEnabled(STROKE_COLOR, false);
        set(STROKE_WIDTH, 0d);
        setBufferedImage(getImageIcon());
    }

    protected BufferedImage getImageIcon() {
        if (getFurnitureIcon() == null) {
            File file = new File(getIconPath());
            try {
                setFurnitureIcon(ImageIO.read(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFurnitureIcon();
    }

    protected BufferedImage getFurnitureIcon() {
        return furnitureIcon;
    }

    protected void setFurnitureIcon(BufferedImage furnitureIcon) {
        this.furnitureIcon = furnitureIcon;
    }

    protected abstract String getIconPath();

}
