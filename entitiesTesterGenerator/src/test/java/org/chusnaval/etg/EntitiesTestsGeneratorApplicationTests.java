package org.chusnaval.etg;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EntitiesTestsGeneratorApplicationTests {

	@Test
	void testInputClassWithPackageAndOutput() {
		File file = new File("\\foo\\test");
		assertAll(() -> EntitiesTestsGeneratorApplication.main(new String[]{"-class", file.getAbsolutePath(), "-output", "F:\\testGen\\", "-package", "org.test"}));
	}

	
	@Test
	void testInputClassWithOutput() {
		File file = new File("\\foo\\test");
		assertAll(() -> EntitiesTestsGeneratorApplication.main(new String[]{"-class", file.getAbsolutePath(), "-output", "F:\\testGen\\"}));
	}

	@Test
	void testInputPackage() {
		File file = new File("\\foo\\test");
		assertAll(() -> EntitiesTestsGeneratorApplication.main(new String[]{"-dir", file.getAbsolutePath(), "-output", "F:\\testGen\\", "-package", "com.foo.entity.id"}));
	}


	@Test
	void testInputPackageRecursive() {
		File file = new File("F:\\testGen\\desarrollo\\comple-webapp\\src\\ttec\\comple\\entity");
		assertAll(() -> EntitiesTestsGeneratorApplication.main(new String[]{"-dir", file.getAbsolutePath(), "-output", "F:\\testGen\\", "-r"}));
	}

	@Test
	void testAppendMethodRecursive() {
		File file = new File("F:\\testGen\\desarrollo\\comple-webapp\\src\\ttec\\comple\\entity");
		assertAll(() -> EntitiesTestsGeneratorApplication.main(new String[]{"-dir", file.getAbsolutePath(), "-output", "F:\\testGen\\", "-r"}));
	}

}
