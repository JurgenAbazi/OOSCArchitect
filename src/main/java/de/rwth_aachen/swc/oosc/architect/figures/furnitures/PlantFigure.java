package de.rwth_aachen.swc.oosc.architect.figures.furnitures;

public class PlantFigure extends FurnitureFigure {
    private static final String PLANT_ICON_PATH_NAME = "src\\main\\resources\\images\\plantIcon.png";

    @Override
    protected String getIconPath() {
        return PLANT_ICON_PATH_NAME;
    }
}
