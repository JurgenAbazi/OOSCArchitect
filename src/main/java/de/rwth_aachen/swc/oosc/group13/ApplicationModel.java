package de.rwth_aachen.swc.oosc.group13;

import com.google.gson.Gson;
import de.rwth_aachen.swc.oosc.group13.figures.floor.*;
import de.rwth_aachen.swc.oosc.group13.figures.furnitures.*;
import de.rwth_aachen.swc.oosc.group13.figures.furnitures.builder.CustomFurnitureFigureFluentBuilder;
import de.rwth_aachen.swc.oosc.group13.http.CircuitBreaker;
import de.rwth_aachen.swc.oosc.group13.http.ImagePublishRunnable;
import de.rwth_aachen.swc.oosc.group13.http.gson.GsonHelper;
import de.rwth_aachen.swc.oosc.group13.http.gson.ImageResource;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.View;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.io.ImageOutputFormat;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.ImageTool;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationModel extends DefaultApplicationModel {

    private DefaultDrawingEditor sharedEditor;

    /**
     * File extension used for opening & saving our drawings.
     */
    private final ExtensionFileFilter fileFilterForDrawingFiles;

    /**
     * Default Constructor
     * Sets the default file filter to xml.
     */
    public ApplicationModel() {
        fileFilterForDrawingFiles = new ExtensionFileFilter(
                "Drawing (.xml)", "xml");
    }

    @Override
    public void initView(Application a, View p) {
        if (a.isSharingToolsAmongViews()) {
            ((ArchitectView) p).setEditor(getSharedEditor());
        }
    }

    /**
     * Set up the application's toolbars.
     */
    @Override
    public List<JToolBar> createToolBars(Application a, @Nullable View view) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        ResourceBundleUtil architectLabels = ArchitectResourceBundle.getLabels();
        ArchitectView architectView = (ArchitectView) view;
        DrawingEditor editor = architectView == null ? getSharedEditor() : architectView.getEditor();

        LinkedList<JToolBar> list = new LinkedList<>();
        JToolBar tb = new JToolBar();
        addDefaultFloorPlanButtonsTo(tb, editor);
        tb.setName(architectLabels.getString("window.floorPlanToolBar.title"));
        list.add(tb);

        tb = new JToolBar();
        addFurnitureButtonsTo(tb, editor);
        tb.setName(architectLabels.getString("window.furnitureToolBar.title"));
        list.add(tb);

        tb = new JToolBar();
        ButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(labels.getString("window.alignmentToolBar.title"));
        list.add(tb);

        return list;
    }

    private void addDefaultFloorPlanButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultFloorPlanButtonsTo(tb, editor,
                ButtonFactory.createDrawingActions(editor),
                ButtonFactory.createSelectionActions(editor));
    }

    private void addFurnitureButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultFurnitureButtonsTo(tb, editor,
                ButtonFactory.createDrawingActions(editor),
                ButtonFactory.createSelectionActions(editor));
    }

    public void addDefaultFloorPlanButtonsTo(JToolBar tb, final DrawingEditor editor,
                                             Collection<Action> drawingActions,
                                             Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ArchitectResourceBundle.getLabels();
        ButtonFactory.addSelectionToolTo(tb, editor, drawingActions, selectionActions);
        tb.addSeparator();

        ButtonFactory.addToolTo(tb, editor, new ImageTool(new ImportedFloorPlanFigure()),
                "edit.fp.importFloorPlan", labels);

        tb.addSeparator();

        ButtonGroup group = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
        JToggleButton publishFloorPlanButton = new JToggleButton();
        labels.configureToolBarButton(publishFloorPlanButton, "edit.publishFloorplan");
        publishFloorPlanButton.addActionListener(e -> publishFloorplan(editor));
        publishFloorPlanButton.setFocusable(false);
        group.add(publishFloorPlanButton);
        tb.add(publishFloorPlanButton);

        tb.addSeparator();

        JToggleButton exportDrawingButton = new JToggleButton();
        labels.configureToolBarButton(exportDrawingButton, "edit.exportDrawing");
        exportDrawingButton.addActionListener(e -> exportDrawingAsImage(editor));
        exportDrawingButton.setFocusable(false);
        group.add(exportDrawingButton);
        tb.add(exportDrawingButton);
        tb.addSeparator();

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new FloorFigure()),
                "edit.fp.createFloor", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new WallFigure()),
                "edit.fp.createWall", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new WindowFigure()),
                "edit.fp.createWindow", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new DoorFigure()),
                "edit.fp.createDoor", labels);

    }

    public void addDefaultFurnitureButtonsTo(JToolBar tb,
                                             final DrawingEditor editor,
                                             Collection<Action> drawingActions,
                                             Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ArchitectResourceBundle.getLabels();
        JToggleButton selectionButton = ButtonFactory
                .addSelectionToolTo(tb, editor, drawingActions, selectionActions);
        selectionButton.setVisible(false);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new BedFigure()),
                "edit.createBed", labels);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new TableFigure()),
                "edit.createTable", labels);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new ChairFigure()),
                "edit.createChair", labels);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new PlantFigure()),
                "edit.createPlant", labels);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new BathtubFigure()),
                "edit.createBathtub", labels);

        tb.addSeparator();

        ButtonGroup group = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
        JToggleButton addFurnitureButton = new JToggleButton();
        labels.configureToolBarButton(addFurnitureButton, "edit.addFurniture");
        addFurnitureButton.addActionListener(e -> addCustomFurnitureAction(tb, editor, labels));
        addFurnitureButton.setFocusable(false);
        group.add(addFurnitureButton);
        tb.add(addFurnitureButton);
    }

    private void addCustomFurnitureAction(JToolBar toolBar,
                                          DrawingEditor editor,
                                          ResourceBundleUtil resourceBundle) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter(
                "Images(.jpg, .png)", "jpg", "png");
        fileChooser.addChoosableFileFilter(fileNameExtensionFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            createFurnitureButtonFromFile(fileChooser.getSelectedFile(), toolBar, editor, resourceBundle);
        }
    }

    private void createFurnitureButtonFromFile(File file,
                                               JToolBar toolBar,
                                               DrawingEditor editor,
                                               ResourceBundleUtil resourceBundle) {
        FurnitureFigure furnitureFigure = CustomFurnitureFigureFluentBuilder.getInstance()
                .setImagePath(file.getPath())
                .build();
        JToggleButton jToggleButton = ButtonFactory.addToolTo(
                toolBar, editor, new CreationTool(furnitureFigure), "", resourceBundle);
        String fileName = file.getName().substring(0, file.getName().indexOf("."));
        jToggleButton.setText(fileName);
        jToggleButton.setToolTipText("Add " + fileName);

        JPopupMenu jPopupMenu = new JPopupMenu();
        JMenuItem removeFurnitureMenuItem = new JMenuItem("Remove from toolbar");
        removeFurnitureMenuItem.addActionListener(e -> {
            toolBar.remove(jToggleButton);
            toolBar.revalidate();
            toolBar.repaint();
        });
        jPopupMenu.add(removeFurnitureMenuItem);
        jToggleButton.setComponentPopupMenu(jPopupMenu);
    }

    /**
     * Uses the <code>ImageOutputFormat</code> class of JHotDraw to write the current
     * drawing on the <code>OutputStream</code> which is passed as an argument to the
     * method.
     *
     * @param editor The editor containing the drawing to be exported.
     * @param stream The output stream being written on.
     * @throws IOException Error writing on the stream. To be handled by the caller.
     */
    public void writeImageOutputFormatOnOutputStream(final DrawingEditor editor,
                                                     OutputStream stream) throws IOException {
        // CANVAS_WIDTH and CANVAS_HEIGHT are null. This causes an exception to be thrown internally
        // in the ImageOutputFormat class when attempting to convert the drawings to an image.
        // To solve this problem we use a hacky solution, where we explicitly set them to the width
        // and height of the drawing area and later restore them to null (so that the canvas size in
        // the application does not change).
        Drawing drawing = editor.getActiveView().getDrawing();
        Rectangle2D.Double drawingArea = drawing.getDrawingArea();
        drawing.set(AttributeKeys.CANVAS_WIDTH, drawingArea.getWidth() + (drawingArea.getX() * 2));
        drawing.set(AttributeKeys.CANVAS_HEIGHT, drawingArea.getHeight() + (drawingArea.getY() * 2));

        ImageOutputFormat imageOutputFormat = new ImageOutputFormat();
        imageOutputFormat.write(stream, drawing);

        drawing.set(AttributeKeys.CANVAS_WIDTH, null);
        drawing.set(AttributeKeys.CANVAS_HEIGHT, null);
    }

    /**
     * Export the drawing of the editor as a PNG.
     * Shows a <code>JFileChooser</code> to select the directory where the image will be saved.
     *
     * @param editor The editor containing the drawing to be exported.
     */
    private void exportDrawingAsImage(final DrawingEditor editor) {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String name = "\\" + ZonedDateTime.now().toInstant().toEpochMilli() + ".png";
                File file = new File(fc.getSelectedFile().getAbsolutePath() + name);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                writeImageOutputFormatOnOutputStream(editor, bos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sends the current drawing to the Floorplan publishing web app. It uses <code>CircuitBreaker</code>
     * to ensure resiliency and <code>ImagePublishRunnable</code> to perform 3 tries with 5-second delays
     * in between tries.
     *
     * @param editor The editor containing the drawing to be published.
     */
    public void publishFloorplan(final DrawingEditor editor) {
        try {
            String floorplanName = JOptionPane.showInputDialog(
                    null, "Enter floorplan file name: ");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            writeImageOutputFormatOnOutputStream(editor, bos);
            byte[] data = bos.toByteArray();

            ImageResource imageResource = new ImageResource();
            imageResource.setImageData(data);
            imageResource.setFileName(floorplanName);

            CircuitBreaker publishImageCircuitBreaker = new CircuitBreaker(
                    () -> sendPublishImageRequestAndGetResponse(imageResource),
                    5_000,
                    2,
                    60_000);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new ImagePublishRunnable(publishImageCircuitBreaker));
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Uses a <code>DefaultHttpClient</code> to send a POST request to the floorplan API, and returns
     * the answer from the service as a String.
     *
     * @param imageResource The image resource being published.
     * @return The response message.
     * @throws IOException Exceptions are to be handed by the caller.
     */
    private String sendPublishImageRequestAndGetResponse(ImageResource imageResource) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost postRequest = new HttpPost(ArchitectResourceBundle.getFloorplanPublisherPath());
            Gson imageResourceSerializer = GsonHelper.getImageResourceGson();

            StringEntity body = new StringEntity(imageResourceSerializer.toJson(imageResource));
            body.setContentType("application/json");
            postRequest.setEntity(body);

            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Failed : HTTP error code != 200");
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            InputStream content = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(content));
            return br.readLine();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * Set up file open dialog
     */
    @Override
    public URIChooser createOpenChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(fileFilterForDrawingFiles);
        c.setFileFilter(fileFilterForDrawingFiles);
        return c;
    }

    /**
     * Set up file save dialog
     */
    @Override
    public URIChooser createSaveChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(fileFilterForDrawingFiles);
        c.setFileFilter(fileFilterForDrawingFiles);
        return c;
    }

    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }

}
