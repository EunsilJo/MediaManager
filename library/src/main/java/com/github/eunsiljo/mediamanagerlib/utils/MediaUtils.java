package com.github.eunsiljo.mediamanagerlib.utils;

import java.net.URI;

/**
 * Created by EunsilJo on 2017. 10. 16..
 */

public class MediaUtils {

    // =============================================================================
    // Format
    // =============================================================================

    public static boolean checkURLFormat(String path) {
        try {
            URI uri = new URI(path);
            return uri.getScheme().equals("http") || uri.getScheme().equals("https");
        }
        catch (Exception e) {
            return false;
        }
    }
}
