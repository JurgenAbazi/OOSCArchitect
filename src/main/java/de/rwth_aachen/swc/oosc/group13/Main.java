package de.rwth_aachen.swc.oosc.group13;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.SDIApplication;
import org.jhotdraw.util.ResourceBundleUtil;

public class Main {
    /**
     * Application entry point (creates a new application using JHotDraw)
     */
    public static void main(String[] args) {
        ResourceBundleUtil.setVerbose(true);

        Application app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            app = new OSXApplication();
        } else if (os.startsWith("win")) {
            //app = new MDIApplication();
            app = new SDIApplication();
        } else {
            app = new SDIApplication();
        }

        ApplicationModel model = new ApplicationModel();
        model.setName("A JHotDraw application");
        model.setVersion(Main.class.getPackage().getImplementationVersion());
        model.setCopyright("-");
        model.setViewFactory(ArchitectView::new);
        app.setModel(model);
        app.launch(args);
    }
}
