package org.chusnaval;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FinderService {

    /**
     * Obtain all java files in recursive mode from a url
     *
     * @param dir directory to search classes
     * @return a recursive list of java classes
     * @throws IOException if directory not exists
     */
    List<String> obtainClassesRecursivePath(Path dir) throws IOException;

    /**
     * Obtain all java files in not recursive mode from a url
     *
     * @param dir directory to search classes
     * @return a list of files path that contains classes in the dir param
     * @throws IOException if directory not exists
     */
    List<String> obtainClassesFromPath(Path dir) throws IOException;

    /**
     * Obtain java className from url only if path is a regular java className
     *
     * @param className class name to find
     * @return a list of files path that is a java class and match with className
     */
    List<String> obtainClassFromPath(Path className);
}
