package de.rwth_aachen.swc.oosc.group13.figures.floor;

import org.jhotdraw.draw.LineFigure;

import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;

public class WallFigure extends LineFigure {
    /**
     * Default Constructor.
     * Creates a thick black line.
     */
    public WallFigure() {
        set(STROKE_WIDTH, 12d);
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
