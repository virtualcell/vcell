package org.vcell.sedml.testsupport;

import java.nio.file.Path;
import java.util.List;

public class PathUtils {
    public static Path findCommonPrefix(List<Path> paths) {
        System.out.println(paths.get(0).subpath(0, 1));
        if (paths == null || paths.isEmpty()) {
            return null;
        }

        Path commonPrefix = paths.get(0);

        for (Path path : paths) {
            commonPrefix = findCommonPrefix(commonPrefix, path);
            if (commonPrefix == null) {
                break;
            }
        }

        return commonPrefix;
    }

    private static Path findCommonPrefix(Path path1, Path path2) {
        int minLength = Math.min(path1.getNameCount(), path2.getNameCount());
        Path commonPrefix = path1.getRoot();

        for (int i = 0; i < minLength; i++) {
            if (path1.getName(i).equals(path2.getName(i))) {
                if (commonPrefix == null) {
                    commonPrefix = path1.getName(i);
                } else {
                    commonPrefix = commonPrefix.resolve(path1.getName(i));
                }
            } else {
                break;
            }
        }

        return commonPrefix;
    }
}
