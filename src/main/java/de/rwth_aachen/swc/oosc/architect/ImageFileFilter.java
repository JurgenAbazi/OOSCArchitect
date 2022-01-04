package de.rwth_aachen.swc.oosc.architect;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFileFilter extends FileFilter {
    public final static String GIF = "gif";
    public final static String JPEG = "jpeg";
    public final static String JPG = "jpg";
    public final static String PNG = "png";
    public final static String TIFF = "tiff";
    public final static String TIF = "tif";

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension == null) {
            return false;
        }

        return extension.equals(GIF)
                || extension.equals(JPEG)
                || extension.equals(JPG)
                || extension.equals(PNG)
                || extension.equals(TIF)
                || extension.equals(TIFF);
    }

    @Override
    public String getDescription() {
        return "Image Only";
    }

    private String getExtension(File file) {
        String extension = null;
        String name = file.getName();

        int index = name.lastIndexOf('.');
        if (index > 0 && index < name.length() - 1) {
            extension = name.substring(index + 1).toLowerCase();
        }

        return extension;
    }
}
