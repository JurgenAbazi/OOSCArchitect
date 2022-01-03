package de.rwth_aachen.swc.oosc.architect.figures.furnitures;

public class BedFigure extends FurnitureFigure {
    private static final String BED_ICON_PATH_NAME = "src\\main\\resources\\images\\bedIcon.png";

    @Override
    protected String getIconPath() {
        return BED_ICON_PATH_NAME;
    }
}
