package de.rwth_aachen.swc.oosc.architect.figures.floor;

import org.jhotdraw.draw.ImageFigure;

public class ImportedFloorPlanFigure extends ImageFigure {
    /**
     * Returns the layer index.
     * Set to be the smallest index in the application.
     *
     * @return -1.
     */
    @Override
    public int getLayer() {
        return -1;
    }
}
