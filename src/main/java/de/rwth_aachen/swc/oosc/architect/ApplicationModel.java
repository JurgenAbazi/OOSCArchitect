package de.rwth_aachen.swc.oosc.architect;

import de.rwth_aachen.swc.oosc.architect.figures.floor.DoorFigure;
import de.rwth_aachen.swc.oosc.architect.figures.floor.ImportedFloorPlanFigure;
import de.rwth_aachen.swc.oosc.architect.figures.floor.WallFigure;
import de.rwth_aachen.swc.oosc.architect.figures.floor.WindowFigure;
import de.rwth_aachen.swc.oosc.architect.figures.furnitures.*;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.View;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.event.ToolListener;
import org.jhotdraw.draw.io.ImageOutputFormat;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.tool.*;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;

public class ApplicationModel extends DefaultApplicationModel {

    private DefaultDrawingEditor sharedEditor;
    private ButtonGroup selectionButtonGroup;
    private ToolListener toolHandler;

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
        addCreationButtonsTo(tb, editor);
        tb.setName(labels.getString("window.drawToolBar.title"));
        list.add(tb);

        tb = new JToolBar();
        addDefaultFloorPlanButtonsTo(tb, editor);
        tb.setName(architectLabels.getString("window.floorPlanToolBar.title"));
        list.add(tb);

        tb = new JToolBar();
        addFurnitureButtonsTo(tb, editor);
        tb.setName(architectLabels.getString("window.furnitureToolBar.title"));
        list.add(tb);

        tb = new JToolBar();
        ButtonFactory.addAttributesButtonsTo(tb, editor);
        tb.setName(labels.getString("window.attributesToolBar.title"));
        list.add(tb);

        tb = new JToolBar();
        ButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(labels.getString("window.alignmentToolBar.title"));
        list.add(tb);

        return list;
    }

    private void addCreationButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultCreationButtonsTo(tb, editor,
                ButtonFactory.createDrawingActions(editor),
                ButtonFactory.createSelectionActions(editor));
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
        JToggleButton exportDrawingButton = new JToggleButton();
        labels.configureToolBarButton(exportDrawingButton, "edit.exportDrawing");
        exportDrawingButton.addActionListener(e -> exportDrawingAsImage(editor));
        exportDrawingButton.setFocusable(false);
        group.add(exportDrawingButton);
        tb.add(exportDrawingButton);
        tb.addSeparator();

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
        ButtonFactory.addSelectionToolTo(tb, editor, drawingActions, selectionActions);
        tb.addSeparator();

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
        addFurnitureButton.addActionListener(e -> addFurnitureAction(tb, editor, labels));
        addFurnitureButton.setFocusable(false);
        group.add(addFurnitureButton);
        tb.add(addFurnitureButton);
    }

    private void addFurnitureAction(JToolBar toolBar,
                                    DrawingEditor editor,
                                    ResourceBundleUtil resourceBundle) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter(
                "Images(.jpg, .png)", "jpg", "png");
        fileChooser.addChoosableFileFilter(fileNameExtensionFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName().substring(0, file.getName().indexOf("."));
            CustomFurnitureBean furnitureBean = new CustomFurnitureBean(file.getPath(), fileName);
            createFurnitureButtonFromBean(furnitureBean, toolBar, editor, resourceBundle);
        }
    }

    private void createFurnitureButtonFromBean(CustomFurnitureBean figure,
                                               JToolBar toolBar,
                                               DrawingEditor editor,
                                               ResourceBundleUtil resourceBundle) {
        FurnitureFigure furnitureFigure = new FurnitureFigure() {
            @Override
            protected String getIconPath() {
                return figure.getPath();
            }
        };
        JToggleButton jToggleButton = ButtonFactory.addToolTo(
                toolBar, editor, new CreationTool(furnitureFigure), "", resourceBundle);
        jToggleButton.setText(figure.getName());
        jToggleButton.setToolTipText("Add " + figure.getName());

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

    public void addDefaultCreationButtonsTo(JToolBar tb,
                                            final DrawingEditor editor,
                                            Collection<Action> drawingActions,
                                            Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");

        ButtonFactory.addSelectionToolTo(tb, editor, drawingActions, selectionActions);
        this.selectionButtonGroup = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
        this.toolHandler = (ToolListener) tb.getClientProperty("toolHandler");
        tb.addSeparator();

        AbstractAttributedFigure af;
        CreationTool ct;
        ConnectionTool cnt;
        ConnectionFigure lc;

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new RectangleFigure()), "edit.createRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new RoundRectangleFigure()), "edit.createRoundRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new EllipseFigure()), "edit.createEllipse", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new DiamondFigure()), "edit.createDiamond", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new TriangleFigure()), "edit.createTriangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LineFigure()), "edit.createLine", labels);
        ButtonFactory.addToolTo(tb, editor, ct = new CreationTool(new LineFigure()), "edit.createArrow", labels);
        af = (AbstractAttributedFigure) ct.getPrototype();
        af.set(END_DECORATION, new ArrowTip(0.35, 12, 11.3));
        ButtonFactory.addToolTo(tb, editor, new ConnectionTool(new LineConnectionFigure()), "edit.createLineConnection", labels);
        ButtonFactory.addToolTo(tb, editor, cnt = new ConnectionTool(new LineConnectionFigure()), "edit.createElbowConnection", labels);
        lc = cnt.getPrototype();
        lc.setLiner(new ElbowLiner());
        ButtonFactory.addToolTo(tb, editor, cnt = new ConnectionTool(new LineConnectionFigure()), "edit.createCurvedConnection", labels);
        lc = cnt.getPrototype();
        lc.setLiner(new CurvedLiner());
        ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure()), "edit.createScribble", labels);
        ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure(true)), "edit.createPolygon", labels);
        ButtonFactory.addToolTo(tb, editor, new TextCreationTool(new TextFigure()), "edit.createText", labels);
        ButtonFactory.addToolTo(tb, editor, new TextAreaCreationTool(new TextAreaFigure()), "edit.createTextArea", labels);
        ButtonFactory.addToolTo(tb, editor, new ImageTool(new ImageFigure()), "edit.createImage", labels);
    }

    /**
     * Export the drawing of the editor as a PNG.
     * Shows a <code>JFileChooser</code> to select the directory where the image will be saved.
     *
     * @param editor The editor containing the drawing to be exported
     */
    private void exportDrawingAsImage(final DrawingEditor editor) {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String name = "\\" + ZonedDateTime.now().toInstant().toEpochMilli() + ".png";
                File file = new File(fc.getSelectedFile().getAbsolutePath() + name);

                // CANVAS_WIDTH and CANVAS_HEIGHT are null. This causes an exception to be thrown internally
                // in the ImageOutputFormat class when attempting to convert the drawings to an image.
                // To solve this problem we use a hacky solution, where we explicitly set them to the width
                // and height of the drawing area and later restore them to null (so that the canvas size in
                // the application does not change).
                Drawing drawing = editor.getActiveView().getDrawing();
                drawing.set(AttributeKeys.CANVAS_WIDTH, drawing.getDrawingArea().getWidth());
                drawing.set(AttributeKeys.CANVAS_HEIGHT, drawing.getDrawingArea().getHeight());

                ImageOutputFormat imageOutputFormat = new ImageOutputFormat();
                imageOutputFormat.write(file, drawing);

                drawing.set(AttributeKeys.CANVAS_WIDTH, null);
                drawing.set(AttributeKeys.CANVAS_HEIGHT, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
