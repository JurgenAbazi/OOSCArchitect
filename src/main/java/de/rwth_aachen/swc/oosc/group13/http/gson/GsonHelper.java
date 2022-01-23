package de.rwth_aachen.swc.oosc.group13.http.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {
    /**
     * @return A Gson object with ImageResourceGson adapter.
     */
    public static Gson getImageResourceGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ImageResource.class, new ImageResourceGson())
                .create();
    }
}
