package org.chusnaval;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class TestGenerator {

    protected final boolean recursiveMode;

    final FinderService fileFinder;

    static final String CLASS_FILE = "class";

    static final String DIRECTORY = "dir";

    /**
     * Default constructor
     *
     * @param recursiveMode Establish the recursive mode
     */
    protected TestGenerator(boolean recursiveMode, FinderService finder) {
        this.recursiveMode = recursiveMode;
        this.fileFinder = finder;
    }

    /**
     * Get the real output folder, if is empty or null we use app folder as destination
     *
     * @param outputFolder output folder indicated from user
     * @return real output folder
     */
    protected static String getRealOutputFolder(Path outputFolder) {
        String destinationDir = ".";
        if (outputFolder != null && !outputFolder.toString().isEmpty() && Files.isDirectory(outputFolder)) {
            destinationDir = outputFolder.toString();
        }
        return destinationDir;
    }

    /**
     * Generates the test files to the proper output folder
     *
     * @param mainOption    define if generation is about a class or directory
     * @param path          path where classes are
     * @param outputFolder  dir were we write the result
     * @param outputPackage package to write in test
     * @throws IOException exception where the route not exists or is not accessible
     */
    public abstract void generateTestFiles(String mainOption, String path, String outputFolder, String outputPackage) throws IOException, GeneratorException;



    /**
     * Obtain all files from a path
     *
     * @param value     establish if we find in a dir or a single class
     * @param pathValue the path where are the files
     * @return list of all files
     * @throws IOException error if path not exists or is not accessible
     */
    public List<String> findAllFiles(String value, String pathValue) throws IOException {
        List<String> files = null;

        if (recursiveMode) {
            files = fileFinder.obtainClassesRecursivePath(Path.of(pathValue));
        } else {
            if (value.equals(DIRECTORY)) {
                files = fileFinder.obtainClassesFromPath(Path.of(pathValue));
            } else if (value.equals(CLASS_FILE)) {
                files = fileFinder.obtainClassFromPath(Path.of(pathValue));
            }
        }
        return files;
    }

    /**
     * Obtain the class name from a Java File
     *
     * @param inputFile input java file
     * @return class name
     */
    public static String obtainClassName(String inputFile) {
        return inputFile.substring(inputFile.lastIndexOf('\\') + 1, inputFile.indexOf("."));
    }

    /**
     * Gets the real output package
     *
     * @param classFile     class path file
     * @param outputPackage output package to write tests
     * @return the real output package
     */
    protected static String getRealOutputPackage(String classFile, String outputPackage) {
        int displacement = 4;
        String realOutputPackage = classFile.contains("src")
                ? classFile.substring(classFile.indexOf("src") + displacement, classFile.lastIndexOf('\\')).replace('\\', '.')
                : "";
        if (outputPackage != null && !outputPackage.isEmpty()) {
            realOutputPackage = outputPackage;
        }
        return realOutputPackage;
    }
}
