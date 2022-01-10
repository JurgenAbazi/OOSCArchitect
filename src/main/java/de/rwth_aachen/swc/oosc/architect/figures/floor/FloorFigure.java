package de.rwth_aachen.swc.oosc.architect.figures.floor;

import org.jhotdraw.draw.RectangleFigure;

import java.awt.*;

import static org.jhotdraw.draw.AttributeKeys.*;

public class FloorFigure extends RectangleFigure {

    public FloorFigure() {
        set(FILL_COLOR, new Color(251, 251, 251));
        setAttributeEnabled(FILL_COLOR, false);
    }

    /**
     * Returns the layer index.
     * Set to be higher than the index of the <code>ImportedFloorPlanFigure</code>
     * so that it is always drawn above it. Additionally, it is smaller than the
     * index of the all other figures.
     * Therefore, the floor is always above the imported floor plan image and below
     * the other figures.
     *
     * @return The integer -1.
     */
    @Override
    public int getLayer() {
        return -1;
    }
}
