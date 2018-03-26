package com.mungolab.djvm.common;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class PathUtils {
    public static String toString(Collection<String> path) {
        // always building absolute path
        return "/" + LanguageUtils.join(path, "/");
    }

    public static Collection<String> fromString(String path) {
        return LanguageUtils.rest(Arrays.asList(path.split("/")));
    }

    public static URL toURL(Collection<String> path) {
        try {
            return new URL("file://" + PathUtils.toString(path));
        } catch (Exception e) {
            throw new RuntimeException("Unable to create URL", e);
        }
    }

    public static File toFile(Collection<String> path) {
        return new File(PathUtils.toString(path));
    }
}
