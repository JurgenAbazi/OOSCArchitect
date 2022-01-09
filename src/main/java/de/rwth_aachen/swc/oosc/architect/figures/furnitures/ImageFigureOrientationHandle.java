package de.rwth_aachen.swc.oosc.architect.figures.furnitures;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.ImageFigure;
import org.jhotdraw.draw.event.AttributeChangeEdit;
import org.jhotdraw.draw.handle.AbstractHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.HandleAttributeKeys;
import org.jhotdraw.geom.Geom;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Custom Handle for rotating Image Figures.
 * Allows the changing of the orientation by multiples of 90 degree.
 */
public class ImageFigureOrientationHandle extends AbstractHandle {

    private Rectangle centerBox;
    private AttributeKeys.Orientation oldValue;
    private AttributeKeys.Orientation newValue;

    public ImageFigureOrientationHandle(ImageFigure owner) {
        super(owner);
    }

    private Point2D.Double getLocation() {
        Figure owner = getOwner();
        java.awt.geom.Rectangle2D.Double r = owner.getBounds();
        double offset = getHandlesize();
        Point2D.Double p;
        switch (owner.get(AttributeKeys.ORIENTATION)) {
            case WEST:
                p = new Point2D.Double(r.x + offset, r.y + r.height / 2.0D);
                break;
            case EAST:
                p = new Point2D.Double(r.x + r.width - offset, r.y + r.height / 2.0D);
                break;
            case SOUTH:
                p = new Point2D.Double(r.x + r.width / 2.0D, r.y + r.height - offset);
                break;
            default:
                p = new Point2D.Double(r.x + r.width / 2.0D, r.y + offset);
                break;
        }

        return p;
    }

    private void rotateFigureFromOrientationChange(AttributeKeys.Orientation newValue,
                                                   AttributeKeys.Orientation oldValue) {
        double angle = 45 * (newValue.ordinal() - oldValue.ordinal());

        ImageFigure owner = (ImageFigure) getOwner();
        Rectangle2D.Double oldBounds = owner.getBounds();
        Rectangle2D.Double newBounds = angle % 180 == 0
                ? new Rectangle2D.Double(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height)
                : new Rectangle2D.Double(oldBounds.x, oldBounds.y, oldBounds.height, oldBounds.width);
        owner.setBounds(newBounds);
        BufferedImage rotate = ImageUtils.rotateImageByDegrees(owner.getBufferedImage(), angle);
        owner.setBufferedImage(rotate);
    }

    @Override
    public boolean isCombinableWith(Handle h) {
        return false;
    }

    @Override
    protected Rectangle basicGetBounds() {
        Point p = view.drawingToView(getLocation());
        Rectangle r = new Rectangle(p);
        int h = getHandlesize();
        r.x -= h / 2;
        r.y -= h / 2;
        r.width = r.height = h;
        return r;
    }

    @Override
    public void trackStart(Point anchor, int modifiersEx) {
        oldValue = getOwner().get(AttributeKeys.ORIENTATION);
        centerBox = view.drawingToView(getOwner().getBounds());
        centerBox.grow(centerBox.width / -3, centerBox.height / -3);
    }

    @Override
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Rectangle leadRect = new Rectangle(lead);
        switch (Geom.outcode(centerBox, leadRect)) {
            case 1:
                newValue = AttributeKeys.Orientation.WEST;
                break;
            case 4:
                newValue = AttributeKeys.Orientation.EAST;
                break;
            case 8:
                newValue = AttributeKeys.Orientation.SOUTH;
                break;
            default:
                newValue = AttributeKeys.Orientation.NORTH;
                break;
        }

        getOwner().willChange();
        getOwner().set(AttributeKeys.ORIENTATION, newValue);
        getOwner().changed();
        updateBounds();
    }

    @Override
    public void draw(Graphics2D g) {
        drawDiamond(g, getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_FILL_COLOR),
                getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_STROKE_COLOR));
    }

    @Override
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        if (newValue != oldValue) {
            getOwner().willChange();
            rotateFigureFromOrientationChange(newValue, oldValue);
            AttributeChangeEdit<AttributeKeys.Orientation> edit = new AttributeChangeEdit<>(
                    getOwner(), AttributeKeys.ORIENTATION, oldValue, newValue);
            fireUndoableEditHappened(edit);
            getOwner().changed();
        }
    }

}
