package org.chusnaval;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;


import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import org.junit.Before;
import org.junit.Test;

public class FileFinderTest {

	final static String PARENT_PATH = "c:\\\\foo";
	final static String CHILD_PATH = "\\child";
	final static String PARENT_PATH_CLASS = "ParentPathClass.java";
	final static String PARENT_PATH_NOT_CLASS = "ParentPathClass.txt";
	final static String CHILD_PATH_CLASS = "ChildPathClass.java";
	final static String CHILD_PATH_NOT_CLASS = "ChildPathClass.txt";
	private FileFinder fileFinder;

	@Before
	public void init() {
		fileFinder = new FileFinder("glob:*.java");
	}

	@Test
	public void testObtainClassesRecursivePath() throws IOException {
		FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
		Path parentDir = fs.getPath(PARENT_PATH);
		Path childDir = fs.getPath(PARENT_PATH + CHILD_PATH);
		Files.createDirectory(parentDir);
		Files.createDirectory(childDir);

		Path parentClass = fs.getPath(PARENT_PATH + fs.getSeparator() + PARENT_PATH_CLASS);
		Path parentNotClass = fs.getPath(PARENT_PATH + fs.getSeparator() + PARENT_PATH_NOT_CLASS);
		Files.createFile(parentClass);
		Files.createFile(parentNotClass);

		Path childClass = fs.getPath(PARENT_PATH + CHILD_PATH + fs.getSeparator() + CHILD_PATH_CLASS);
		Path childNotClass = fs.getPath(PARENT_PATH + CHILD_PATH + fs.getSeparator() + CHILD_PATH_NOT_CLASS);
		Files.createFile(childClass);
		Files.createFile(childNotClass);

		assertEquals(2, fileFinder.obtainClassesRecursivePath(parentDir).size());
	}

	
    @Test
	public void testObtainClassesFromPath() throws IOException {
		FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
		Path parentDir = fs.getPath(PARENT_PATH);
		Path childDir = fs.getPath(PARENT_PATH + CHILD_PATH);
		Files.createDirectory(parentDir);
		Files.createDirectory(childDir);
		
		Path parentClass = fs.getPath(PARENT_PATH + fs.getSeparator() + PARENT_PATH_CLASS);
		Path parentNotClass = fs.getPath(PARENT_PATH + fs.getSeparator() + PARENT_PATH_NOT_CLASS);
		Files.createFile(parentClass);
		Files.createFile(parentNotClass);

		Path childClass = fs.getPath(PARENT_PATH + CHILD_PATH + fs.getSeparator() + CHILD_PATH_CLASS);
		Path childNotClass = fs.getPath(PARENT_PATH + CHILD_PATH + fs.getSeparator() + CHILD_PATH_NOT_CLASS);
		Files.createFile(childClass);
		Files.createFile(childNotClass);

		assertEquals(1, fileFinder.obtainClassesFromPath(parentDir).size());
	}

	@Test
	public void testObtainClassFromPath() throws IOException {
		FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
		Path parentDir = fs.getPath(PARENT_PATH);
		Files.createDirectory(parentDir);
		
		Path parentClass = fs.getPath(PARENT_PATH + fs.getSeparator() + PARENT_PATH_CLASS);
		Files.createFile(parentClass);

		assertEquals(1, fileFinder.obtainClassFromPath(parentClass).size());
	}
}
