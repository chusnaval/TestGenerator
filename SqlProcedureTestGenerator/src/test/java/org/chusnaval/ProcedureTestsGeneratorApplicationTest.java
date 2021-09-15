package org.chusnaval;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class ProcedureTestsGeneratorApplicationTest {


	@Test
	void testAppendMethodRecursive() {
		File file = new File("F:\\testGen\\desarrollo\\comple-webapp\\src\\ttec\\comple\\procedures");
		assertAll(() -> ProcedureTestsGeneratorApplication.main(new String[]{"-dir", file.getAbsolutePath(), "-output", "F:\\testGen\\", "-r"}));
	}

}
