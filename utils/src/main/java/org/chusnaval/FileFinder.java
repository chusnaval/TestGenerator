package org.chusnaval;

import static java.nio.file.Files.list;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FileFinder implements FinderService {

    /**
     * Defines a java file name pattern
     */
    private final PathMatcher matcher;

    /**
     * Default constructor
     */
    public FileFinder(String pattern) {
        matcher = FileSystems.getDefault().getPathMatcher(pattern);
    }


    /**
     * Obtain all java files in recursive mode from a url
     *
     * @param dir directory to search classes
     * @return a recursive list of java classes
     * @throws IOException if directory not exists
     */
    public List<String> obtainClassesRecursivePath(Path dir) throws IOException {
        List<String> files = new ArrayList<>();
        try (Stream<Path> stream = Files.find(dir, Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile() && matcher.matches(filePath.getFileName()))) {

            stream.forEach(n -> files.add(n.toString()));
        }
        return files;
    }

    /**
     * Obtain all java files in not recursive mode from a url
     *
     * @param dir directory to search classes
     * @return a list of files path that contains classes in the dir param
     * @throws IOException if directory not exists
     */
    public List<String> obtainClassesFromPath(Path dir) throws IOException {
        List<String> files = new ArrayList<>();

        try (Stream<Path> stream = list(dir)) {
            stream.forEach(path -> {
                if (Files.isRegularFile(path) && matcher.matches(path.getFileName())) {
                    files.add(path.toString());
                }
            });
        }

        return files;
    }

    /**
     * Obtain java className from url only if path is a regular java className
     *
     * @param className class name to find
     * @return a list of files path that is a java class and match with className
     */
    public List<String> obtainClassFromPath(Path className) {
        List<String> files = new ArrayList<>();
        if (Files.isRegularFile(className) && matcher.matches(className.getFileName())) {
            files.add(className.toString());
        }
        return files;
    }


}
