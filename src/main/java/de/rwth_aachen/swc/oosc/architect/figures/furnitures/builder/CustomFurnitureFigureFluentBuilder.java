package de.rwth_aachen.swc.oosc.architect.figures.furnitures.builder;

import de.rwth_aachen.swc.oosc.architect.figures.furnitures.FurnitureFigure;

/**
 * Class used for creating custom furniture figures.
 * Uses the Fluent Builder design pattern.
 * Ensures that the path of the image of the figure must be set before the object can be built.
 */
public class CustomFurnitureFigureFluentBuilder implements IFurnitureFigureImagePathBuilder, IFurnitureFigureBuilder {

    private final FurnitureFigure furniture;

    private CustomFurnitureFigureFluentBuilder() {
        this.furniture = new FurnitureFigure();
    }

    /**
     * Getting the instance method.
     *
     * @return new instance of the builder.
     */
    public static IFurnitureFigureImagePathBuilder getInstance() {
        return new CustomFurnitureFigureFluentBuilder();
    }

    @Override
    public IFurnitureFigureBuilder setImagePath(String imagePath) {
        furniture.setImagePath(imagePath);
        return this;
    }

    @Override
    public FurnitureFigure build() {
        return furniture;
    }
}
