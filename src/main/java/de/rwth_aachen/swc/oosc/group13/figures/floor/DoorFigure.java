package de.rwth_aachen.swc.oosc.group13.figures.floor;

import org.jhotdraw.draw.LineFigure;

import static org.jhotdraw.draw.AttributeKeys.STROKE_DASHES;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;

public class DoorFigure extends LineFigure {
    /**
     * Default Constructor.
     * Creates a dashed line.
     */
    public DoorFigure() {
        set(STROKE_WIDTH, 4d);
        set(STROKE_DASHES, new double[]{2, 2});
    }

    /**
     * Returns the layer index.
     * Set to be higher than the index of the <code>ImportedFloorPlanFigure</code>
     * so that it is always drawn above it.
     *
     * @return The integer 1.
     */
    @Override
    public int getLayer() {
        return 1;
    }
}
