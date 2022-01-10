package de.rwth_aachen.swc.oosc.architect;

import de.rwth_aachen.swc.oosc.architect.figures.floor.FloorFigure;
import de.rwth_aachen.swc.oosc.architect.figures.floor.ImportedFloorPlanFigure;
import de.rwth_aachen.swc.oosc.architect.figures.floor.WallFigure;
import de.rwth_aachen.swc.oosc.architect.figures.floor.WindowFigure;
import de.rwth_aachen.swc.oosc.architect.figures.furnitures.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.*;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.xml.DefaultDOMFactory;

/**
 * Factory that is used to construct a drawing from a given tag hierarchy. This is used for loading drawings from XML
 * files.
 * <p>
 * This class defines a mapping between XML tags and drawing classes.
 */
public class FigureFactory extends DefaultDOMFactory {

    private final static Object[][] CLASS_TAG_ARRAY = {
            {DefaultDrawing.class, "useCaseDiagramDrawing"},
            {QuadTreeDrawing.class, "drawing"},

            {ImportedFloorPlanFigure.class, "importedFloorPlan"},
            {FloorFigure.class, "floor"},
            {WallFigure.class, "wall"},
            {WindowFigure.class, "window"},
            {FurnitureFigure.class, "furniture"},
            {BathtubFigure.class, "bathtub"},
            {BedFigure.class, "bed"},
            {ChairFigure.class, "chair"},
            {PlantFigure.class, "plant"},
            {TableFigure.class, "table"},

            {EllipseFigure.class, "ellipse"},
            {TextFigure.class, "text"},
            {TextAreaFigure.class, "textArea"},
            {LineFigure.class, "line"},
            {RectangleFigure.class, "rectangle"},
            {DiamondFigure.class, "diamond"},
            {TriangleFigure.class, "triangle"},
            {BezierFigure.class, "bezier"},
            {RoundRectangleFigure.class, "roundRectangle"},
            {ImageFigure.class, "imageFigure"},
            {ListFigure.class, "list"},
            {Insets2D.Double.class, "insets"},
            {LineConnectionFigure.class, "lineConnectionFigure"},
            {GroupFigure.class, "groupFigure"},

            {LocatorConnector.class, "locatorConnector"},
            {RelativeLocator.class, "relativeLocator"},
            {ArrowTip.class, "arrowTip"},
            {ChopRectangleConnector.class, "rConnector"},
            {ChopEllipseConnector.class, "ellipseConnector"},
            {ChopRoundRectangleConnector.class, "rrConnector"},
            {ChopTriangleConnector.class, "triangleConnector"},
            {ChopDiamondConnector.class, "diamondConnector"},
            {ChopBezierConnector.class, "bezierConnector"},

            {ElbowLiner.class, "elbowLiner"},
            {CurvedLiner.class, "curvedLiner"},
    };

    private static final Object[][] ENUM_TAG_ARRAY = {
            {AttributeKeys.Alignment.class, "alignment"},
            {AttributeKeys.Orientation.class, "orientation"},
            {AttributeKeys.StrokePlacement.class, "strokePlacement"},
            {AttributeKeys.StrokeType.class, "strokeType"},
    };

    /**
     * Creates a new instance.
     */
    @SuppressWarnings("rawtypes")
    public FigureFactory() {
        for (Object[] objects : CLASS_TAG_ARRAY) {
            addStorableClass(((String) objects[1]), ((Class) objects[0]));
        }
        for (Object[] objects : ENUM_TAG_ARRAY) {
            addEnumClass((String) objects[1], (Class) objects[0]);
        }
    }
}
