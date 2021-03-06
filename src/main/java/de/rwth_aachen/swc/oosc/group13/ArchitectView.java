package de.rwth_aachen.swc.oosc.group13;

import org.jhotdraw.app.AbstractView;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.io.*;
import org.jhotdraw.draw.print.DrawingPageable;
import org.jhotdraw.gui.PlacardScrollPaneLayout;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

public class ArchitectView extends AbstractView {
    /**
     * Each DrawView uses its own undo redo manager.
     * Allows for undoing and redoing actions per view.
     */
    private final UndoRedoManager undo;

    /**
     * Depending on the type of application, there may be one editor
     * per view, or a single shared editor for all views.
     */
    private DrawingEditor editor;

    public ArchitectView() {
        initComponents();

        scrollPane.setLayout(new PlacardScrollPaneLayout());
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        setEditor(new DefaultDrawingEditor());
        undo = new UndoRedoManager();
        view.setDrawing(createDrawing());
        view.getDrawing().addUndoableEditListener(undo);
        initActions();
        undo.addPropertyChangeListener(e -> setHasUnsavedChanges(undo.hasSignificantEdits()));

        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        JPanel placardPanel = new JPanel(new BorderLayout());

        AbstractButton pButton = ButtonFactory.createZoomButton(view);
        pButton.putClientProperty("Quaqua.Button.style", "placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, 0, 0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        placardPanel.add(pButton, BorderLayout.WEST);

        pButton = ButtonFactory.createToggleGridButton(view);
        pButton.putClientProperty("Quaqua.Button.style", "placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, 0, 0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        labels.configureToolBarButton(pButton, "view.toggleGrid.placard");
        placardPanel.add(pButton, BorderLayout.EAST);

        scrollPane.add(placardPanel, JScrollPane.LOWER_LEFT_CORNER);
    }

    /**
     * Creates a new Drawing for this view.
     */
    protected Drawing createDrawing() {
        Drawing drawing = new QuadTreeDrawing();
        DOMStorableInputOutputFormat ioFormat =
                new DOMStorableInputOutputFormat(new FigureFactory());

        drawing.addInputFormat(ioFormat);
        ImageFigure prototype = new ImageFigure();
        drawing.addInputFormat(new ImageInputFormat(prototype));
        drawing.addInputFormat(new TextInputFormat(new TextFigure()));
        TextAreaFigure taf = new TextAreaFigure();
        taf.setBounds(new Point2D.Double(10, 10), new Point2D.Double(60, 40));
        drawing.addInputFormat(new TextInputFormat(taf));

        drawing.addOutputFormat(ioFormat);
        drawing.addOutputFormat(new ImageOutputFormat());

        return drawing;
    }

    /**
     * Creates a Pageable object for printing the view.
     */
    @SuppressWarnings("unused")
    public Pageable createPageable() {
        return new DrawingPageable(view.getDrawing());
    }

    /**
     * Initializes view specific actions.
     */
    private void initActions() {
        getActionMap().put(UndoAction.ID, undo.getUndoAction());
        getActionMap().put(RedoAction.ID, undo.getRedoAction());
    }

    @Override
    protected void setHasUnsavedChanges(boolean newValue) {
        super.setHasUnsavedChanges(newValue);
        undo.setHasSignificantEdits(newValue);
    }

    /**
     * Writes the view to the specified uri.
     */
    @Override
    public void write(URI f, URIChooser fc) throws IOException {
        Drawing drawing = view.getDrawing();
        OutputFormat outputFormat = drawing.getOutputFormats().get(0);
        outputFormat.write(f, drawing);
    }

    /**
     * Reads the view from the specified uri.
     */
    @Override
    public void read(URI f, URIChooser fc) throws IOException {
        try {
            final Drawing drawing = createDrawing();
            boolean success = false;
            for (InputFormat sfi : drawing.getInputFormats()) {
                try {
                    sfi.read(f, drawing, true);
                    success = true;
                    break;
                } catch (Exception ignored) {
                }
            }
            if (!success) {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                throw new IOException(labels.getFormatted("file.open.unsupportedFileFormat.message", URIUtil.getName(f)));
            }
            SwingUtilities.invokeAndWait(() -> {
                view.getDrawing().removeUndoableEditListener(undo);
                view.setDrawing(drawing);
                view.getDrawing().addUndoableEditListener(undo);
                undo.discardAllEdits();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            InternalError error = new InternalError();
            e.initCause(e);
            throw error;
        }
    }


    /**
     * Sets a drawing editor for the view.
     */
    public void setEditor(DrawingEditor newValue) {
        if (editor != null) {
            editor.remove(view);
        }
        editor = newValue;
        if (editor != null) {
            editor.add(view);
        }
    }

    /**
     * Gets the drawing editor of the view.
     */
    public DrawingEditor getEditor() {
        return editor;
    }

    /**
     * Clears the view.
     */
    @Override
    public void clear() {
        final Drawing newDrawing = createDrawing();
        try {
            SwingUtilities.invokeAndWait(() -> {
                view.getDrawing().removeUndoableEditListener(undo);
                view.setDrawing(newDrawing);
                view.getDrawing().addUndoableEditListener(undo);
                undo.discardAllEdits();
            });
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canSaveTo(URI file) {
        return new File(file).getName().endsWith(".xml");
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new JScrollPane();
        view = new DefaultDrawingView();

        setLayout(new BorderLayout());

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(view);

        add(scrollPane, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane scrollPane;
    private DefaultDrawingView view;
    // End of variables declaration//GEN-END:variables

}
