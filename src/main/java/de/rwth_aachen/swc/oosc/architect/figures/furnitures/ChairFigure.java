package de.rwth_aachen.swc.oosc.architect.figures.furnitures;

public class ChairFigure extends FurnitureFigure {
    private static final String CHAIR_ICON_PATH_NAME = "src\\main\\resources\\images\\chairIcon.png";

    @Override
    protected String getIconPath() {
        return CHAIR_ICON_PATH_NAME;
    }
}
