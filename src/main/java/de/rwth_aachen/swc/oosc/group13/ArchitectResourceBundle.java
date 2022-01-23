package de.rwth_aachen.swc.oosc.group13;

import org.jhotdraw.util.ResourceBundleUtil;

public class ArchitectResourceBundle {
    private static final String PATH = "de.rwth_aachen.swc.oosc.architect.bundle";
    private static final String DEFAULT_REST_PATH = "http://localhost:8080/api/v1/floorplans";

    /**
     * Private constructor.
     * Prevents instance creation.
     */
    private ArchitectResourceBundle() {
    }

    /**
     * @return The <code>ResourceBundleUtil</code> containing the labels.
     */
    public static ResourceBundleUtil getLabels() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle(PATH);
        labels.setBaseClass(ArchitectResourceBundle.class);
        return labels;
    }

    /**
     * Returns the path from the resources bundle. If the resource is not found,
     * it will return a default path. Note, that it compares with the key since
     * <code>getLabels().getString(key)</code> returns the key if not requested
     * resource is not found.
     *
     * @return The path of the floorplan publishing REST API.
     */
    public static String getFloorplanPublisherPath() {
        String key = "images.publisher.api.path";
        String path = getLabels().getString(key);
        return path.isBlank() || path.equals(key) ? DEFAULT_REST_PATH : path;
    }
}
