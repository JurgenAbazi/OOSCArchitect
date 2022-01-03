package de.rwth_aachen.swc.oosc.architect;

import de.rwth_aachen.swc.oosc.architect.figures.furnitures.*;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.View;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.event.ToolListener;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.tool.*;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
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
        ArchitectView architectView = (ArchitectView) view;
        DrawingEditor editor = architectView == null ? getSharedEditor() : architectView.getEditor();

        LinkedList<JToolBar> list = new LinkedList<>();
        JToolBar tb = new JToolBar();
        addCreationButtonsTo(tb, editor);
        tb.setName(labels.getString("window.drawToolBar.title"));
        list.add(tb);

        addFurnitureButtonsTo(tb, editor);
        tb.setName("Furniture");
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

    private void addFurnitureButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultFurnitureButtonsTo(tb, editor,
                ButtonFactory.createDrawingActions(editor),
                ButtonFactory.createSelectionActions(editor));
    }

    public void addDefaultFurnitureButtonsTo(JToolBar tb,
                                            final DrawingEditor editor,
                                            Collection<Action> drawingActions,
                                            Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ArchitectResourceBundle.getLabels();
        ButtonFactory.addSelectionToolTo(tb, editor, drawingActions, selectionActions);
        tb.addSeparator();

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new BedFigure()),
                "edit.creatBed", labels);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new TableFigure()),
                "edit.createTable", labels);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new ChairFigure()),
                "edit.createChair", labels);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new PlantFigure()),
                "edit.createPlant", labels);

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new BathtubFigure()),
                "edit.createBathtub", labels);
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
