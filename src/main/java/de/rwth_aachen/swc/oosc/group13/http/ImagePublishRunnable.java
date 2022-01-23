package de.rwth_aachen.swc.oosc.group13.http;

import javax.swing.*;
import java.awt.*;

/**
 * Runnable for executing three times the <code>CircuitBreaker</code>.
 */
public class ImagePublishRunnable implements Runnable {
    private final CircuitBreaker circuitBreaker;
    private final Component parentComponent;

    /**
     * Constructor.
     *
     * @param circuitBreaker The circuit breaker whose call method will be executed.
     */
    public ImagePublishRunnable(CircuitBreaker circuitBreaker) {
        this(circuitBreaker, null);
    }

    public ImagePublishRunnable(CircuitBreaker circuitBreaker,
                                Component parentComponent) {
        this.circuitBreaker = circuitBreaker;
        this.parentComponent = parentComponent;
    }

    /**
     * Executes the <code>circuitBreaker.call()</code> method up to 3 times.
     * If any of the calls returns a result it stops, otherwise it waits 5
     * seconds and retries. If none of the call return a result, it displays
     * the last error message and stops.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            showSuccessMessage(circuitBreaker.call());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Retrying in 5000ms...");
        try {
            Thread.sleep(5000);
            showSuccessMessage(circuitBreaker.call());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Retrying again in another 5000ms...");
        try {
            Thread.sleep(5000);
            showSuccessMessage(circuitBreaker.call());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Display a success message.
     *
     * @param response The response from the service.
     */
    private void showSuccessMessage(String response) {
        JOptionPane.showMessageDialog(
                parentComponent,
                "Response from service:\n" + response,
                "Response",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
