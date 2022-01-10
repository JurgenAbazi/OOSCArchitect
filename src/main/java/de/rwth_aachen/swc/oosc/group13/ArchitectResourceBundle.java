package de.rwth_aachen.swc.oosc.group13;

import org.jhotdraw.util.ResourceBundleUtil;

public class ArchitectResourceBundle {
    private static final String PATH = "de.rwth_aachen.swc.oosc.architect.bundle";

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
}
