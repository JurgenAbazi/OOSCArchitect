package de.rwth_aachen.swc.oosc.group13.figures.furnitures;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility class.
 * Contains static methods for performing operations on BufferedImages.
 */
public class ImageUtils {
    /**
     * Private constructor.
     * Prevents instance creation.
     */
    private ImageUtils() {
    }

    /**
     * Creates a <code>BufferedImage</code> from the path.
     * If the path is null, it uses an empty string as path.
     *
     * @param path The path as String.
     * @return A <code>BufferedImage</code> of the picture in the path.
     */
    public static BufferedImage getBufferedImageFromPath(String path) {
        BufferedImage bufferedImage = null;
        String pathOfFile = path == null ? "" : path;

        File file = new File(pathOfFile);
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    /**
     * Returns a rotated <code>BufferedImage</code> based on the angle.
     * It performs the necessary calculations for the new width and height of the
     * rotated image and creates the image. Not that it does not change the image
     * passed as argument, but instead creates a new one.
     *
     * @param img   The image to be rotated.
     * @param angle The angle of the rotation.
     * @return A rotated image.
     */
    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));

        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();

        AffineTransform transform = new AffineTransform();
        transform.translate((newWidth - w) >> 1, (newHeight - h) >> 1);
        transform.rotate(rads, w >> 1, h >> 1);

        g2d.setTransform(transform);
        g2d.drawImage(img, null, 0, 0);
        g2d.dispose();

        return rotated;
    }
}
