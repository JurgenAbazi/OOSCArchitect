package de.rwth_aachen.swc.oosc.architect.figures.floor;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.LineFigure;

import static org.jhotdraw.draw.AttributeKeys.STROKE_TYPE;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;

public class WindowFigure extends LineFigure {
    /**
     * Default Constructor.
     * Creates a double stroke black line.
     */
    public WindowFigure() {
        set(STROKE_WIDTH, 4d);
        set(STROKE_TYPE, AttributeKeys.StrokeType.DOUBLE);
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
