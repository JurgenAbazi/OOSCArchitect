package de.rwth_aachen.swc.oosc.group13.http.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Serializer for <code>ImageResource</code>.
 */
public class ImageResourceGson implements JsonSerializer<ImageResource> {
    @Override
    public JsonElement serialize(ImageResource imageResource,
                                 Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        String filename = imageResource.getFileName().replace(" ", "_");
        jsonObject.addProperty("fileName", filename);

        Base64.Encoder encoder = Base64.getEncoder();
        String encodedData = encoder.encodeToString(imageResource.getImageData());
        jsonObject.addProperty("imageData", "data:image/jpeg;base64," + encodedData);

        return jsonObject;
    }
}
