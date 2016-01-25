package cs.vt.analysis.analyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.analysis.UnreachableCodeAnalyzer;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.Util;

public class UnreachableCodeAnalyzerTest {
	UnreachableCodeAnalyzer analyzer;
	ScratchProject project;
	File[] dataset;
	String inputString;

	@Before
	public void setUp() throws Exception {

		InputStream in = Main.class.getClassLoader()
				.getResource("93160218.json").openStream();
		inputString = IOUtils.toString(in);
		in.close();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws ParseException, IOException {
		// for (File file : dataset) {
		// project = ScratchProject.loadProject(Util.readFile(file.toString()));
		// analyzer = new UnreachableCodeAnalyzer(project);
		// analyzer.analyze();
		// }

		project = ScratchProject.loadProject(inputString);
		analyzer = new UnreachableCodeAnalyzer(project);
		analyzer.analyze();
	}

}
