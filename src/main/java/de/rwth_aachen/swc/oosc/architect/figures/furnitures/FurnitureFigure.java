package de.rwth_aachen.swc.oosc.architect.figures.furnitures;

import org.jhotdraw.draw.ImageFigure;
import org.jhotdraw.draw.handle.Handle;

import java.awt.image.BufferedImage;
import java.util.Collection;

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
            furnitureImage = ImageUtils.getBufferedImageFromPath(getImagePath());
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

    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        Collection<Handle> handles = super.createHandles(detailLevel);
        if (detailLevel == 0) {
            handles.add(new ImageFigureOrientationHandle(this));
        }
        return handles;
    }
}
