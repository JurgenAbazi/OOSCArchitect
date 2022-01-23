package de.rwth_aachen.swc.oosc.group13.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Wrapper for the HTTP client functionalities.
 * Is implemented using the Bill-Pugh Singleton design pattern.
 */
public class HttpClientService {
    private static final int STATUS_SUCCESS = 200;
    private HttpClient httpClient;

    /**
     * Private constructor to prevent the initialization.
     */
    private HttpClientService() {
    }

    private static class LazyHolder {
        static final HttpClientService INSTANCE = new HttpClientService();
    }

    public static HttpClientService getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Sends a POST request to a specified URI and returns the response.
     *
     * @param uri The path where to send the POST request.
     * @param body The body of the request.
     * @return The response of the request.
     * @throws IOException Exceptions are expected to be handled by the caller.
     */
    public HttpResponse sendPostRequest(String uri,
                                        StringEntity body) throws IOException {
        HttpPost postRequest = new HttpPost(uri);
        body.setContentType("application/json");
        postRequest.setEntity(body);

        HttpResponse response = getHttpClient().execute(postRequest);
        if (response.getStatusLine().getStatusCode() != STATUS_SUCCESS) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }
        return response;
    }

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }
        return httpClient;
    }
}
