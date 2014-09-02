package net.pushover.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * encapsulate service response parsing / building
 */
public class PushoverResponseFactory {

    private static final Gson GSON = new Gson();

    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    public static Status createStatus(HttpResponse response) throws IOException {

        if (response == null || response.getEntity() == null) {
            throw new IOException("unreadable response!");
        }

        final String body = EntityUtils.toString(response.getEntity());

        ResponseModel m;

        try {
            m = GSON.fromJson(body, ResponseModel.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e.getCause());
        }

        final Status toReturn = new Status(m.status);
        final Header responseId = response.getFirstHeader(REQUEST_ID_HEADER);

        if (responseId != null) {
            toReturn.setRequestId(responseId.getValue());
        }

        return toReturn;
    }

    public static Set<PushOverSound> createSoundSet(HttpResponse response) throws IOException {

        if (response == null || response.getEntity() == null) {
            throw new IOException("unreadable response!");
        }

        final String body = EntityUtils.toString(response.getEntity());

        SoundResponse r;
        try {
            r = GSON.fromJson(body, SoundResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e.getCause());
        }

        final Set<PushOverSound> sounds = new HashSet<PushOverSound>();
        if (r.sounds != null) {
            for (Map.Entry<String, String> e : r.sounds.entrySet()) {
                sounds.add(new PushOverSound(e.getKey(), e.getValue()));
            }
        }
        return sounds;
    }

    // {"status":1}
    private static class ResponseModel {
        int status;
    }

    // {"sounds":{"id":"name",...},"status":1}
    private static class SoundResponse {
        Map<String, String> sounds;
    }
}
