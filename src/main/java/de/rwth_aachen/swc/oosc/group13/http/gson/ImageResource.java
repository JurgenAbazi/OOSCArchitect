package de.rwth_aachen.swc.oosc.group13.http.gson;

/**
 * Class representing an image sent for being published.
 */
public class ImageResource {
    private String fileName;
    private byte[] imageData;

    public ImageResource() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public String toString() {
        return "ImageResource{" +
                "fileName='" + fileName + '\'' +
                '}';
    }

}
