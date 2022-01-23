package de.rwth_aachen.swc.oosc.group13;

import com.google.gson.Gson;
import de.rwth_aachen.swc.oosc.group13.http.CircuitBreaker;
import de.rwth_aachen.swc.oosc.group13.http.HttpClientService;
import de.rwth_aachen.swc.oosc.group13.http.ImagePublishRunnable;
import de.rwth_aachen.swc.oosc.group13.http.gson.GsonHelper;
import de.rwth_aachen.swc.oosc.group13.http.gson.ImageResource;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.io.ImageOutputFormat;

import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Contains the logic to export the drawings (as screenshot or publish them).
 */
public class ExportDrawingController {
    /**
     * Private constructor to prevent initialization.
     * Contains only static methods since the class has no state.
     */
    private ExportDrawingController() {
    }

    /**
     * Uses the <code>ImageOutputFormat</code> class of JHotDraw to write the current
     * drawing on the <code>OutputStream</code> which is passed as an argument to the
     * method.
     *
     * @param drawing The drawing to be exported.
     * @param stream  The output stream being written on.
     * @throws IOException Error writing on the stream. To be handled by the caller.
     */
    public static void writeImageOutputFormatOnOutputStream(final Drawing drawing,
                                                            OutputStream stream) throws IOException {
        // CANVAS_WIDTH and CANVAS_HEIGHT are null. This causes an exception to be thrown internally
        // in the ImageOutputFormat class when attempting to convert the drawings to an image.
        // To solve this problem we use a hacky solution, where we explicitly set them to the width
        // and height of the drawing area and later restore them to null (so that the canvas size in
        // the application does not change).
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
     *
     * @param drawing  The drawing to be exported.
     * @param path     The path to be exported.
     * @param fileName The name of the created file.
     * @throws IOException Error writing on the stream. To be handled by the caller.
     */
    public static void exportDrawingAsImage(final Drawing drawing,
                                            String path,
                                            String fileName) throws IOException {
        File file = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        writeImageOutputFormatOnOutputStream(drawing, bos);
    }

    /**
     * Sends the current drawing to the Floorplan publishing web app. It uses <code>CircuitBreaker</code>
     * to ensure resiliency and <code>ImagePublishRunnable</code> to perform 3 tries with 5-second delays
     * in between tries.
     *
     * @param drawing       The drawing to be published.
     * @param floorplanName The name of the floorplan being published.
     * @throws IOException Error writing on the stream. To be handled by the caller.
     */
    public static void publishFloorplan(final Drawing drawing,
                                        String floorplanName) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeImageOutputFormatOnOutputStream(drawing, bos);
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
    }

    /**
     * Uses a <code>HttpClientService</code> to send a POST request to the floorplan API, and returns
     * the answer from the service as a String.
     *
     * @param resource The image resource being published.
     * @return The response message.
     * @throws IOException Exceptions are to be handed by the caller.
     */
    private static String sendPublishImageRequestAndGetResponse(ImageResource resource) throws IOException {
        String uri = ArchitectResourceBundle.getFloorplanPublisherPath();

        Gson imageResourceSerializer = GsonHelper.getImageResourceGson();
        StringEntity body = new StringEntity(imageResourceSerializer.toJson(resource));
        body.setContentType("application/json");

        HttpResponse response = HttpClientService.getInstance().sendPostRequest(uri, body);
        InputStream content = response.getEntity().getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(content));
        return br.readLine();
    }
}
