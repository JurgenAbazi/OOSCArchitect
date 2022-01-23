package de.rwth_aachen.swc.oosc.group13.figures.toast;

import javax.swing.*;
import java.awt.*;

/**
 * Toast implementation extending <code>JDialog</code>.
 */
@SuppressWarnings("unused")
public class ToastMessage extends JDialog {
    public static final Color COLOR_ERROR = new Color(245, 105, 105);
    public static final Color COLOR_WARNING = new Color(234, 164, 43);
    public static final Color COLOR_SUCCESS = new Color(157, 215, 42);
    public static final int DURATION_SHORT = 2500;
    public static final int DURATION_LONG = 5000;

    private final int duration;

    public ToastMessage(String message, int duration, Color color) {
        this.duration = duration;
        setUndecorated(true);
        getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBackground(color);
        getContentPane().add(panel, BorderLayout.CENTER);

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Default", Font.BOLD, 12));
        setBounds(100, 100, label.getPreferredSize().width + 40, 30);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int y = dim.height / 2 - getSize().height / 2;
        setLocation(dim.width / 2 - getSize().width / 2, (int) (y * 1.5));

        setAlwaysOnTop(true);
        panel.add(label);
        setVisible(false);
    }

    public static void showSuccessMessage(String message) {
        new ToastMessage(message, DURATION_SHORT, COLOR_SUCCESS).showMessage();
    }

    public static void showErrorMessage(String message) {
        new ToastMessage(message, DURATION_SHORT, COLOR_ERROR).showMessage();
    }

    public static void showWarningMessage(String message) {
        new ToastMessage(message, DURATION_SHORT, COLOR_WARNING).showMessage();
    }

    public void showMessage() {
        new Thread(() -> {
            try {
                setOpacity(1);
                setVisible(true);
                Thread.sleep(duration);

                // Creates the gradual fade effect.
                for (float opacity = 1.0f; opacity > 0.2; opacity -= 0.1) {
                    Thread.sleep(100);
                    setOpacity(opacity);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                dispose();
            }
        }).start();
    }
}
