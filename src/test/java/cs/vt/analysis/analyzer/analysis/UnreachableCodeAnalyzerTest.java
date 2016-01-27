package cs.vt.analysis.analyzer.analysis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.Main;
import cs.vt.analysis.analyzer.TestConstant;
import cs.vt.analysis.analyzer.analysis.UnreachableCodeAnalyzer;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.Util;

public class UnreachableCodeAnalyzerTest {
	UnreachableCodeAnalyzer analyzer;
	
	File[] dataset;
	

	@Before
	public void setUp() throws Exception {

//		InputStream in = Main.class.getClassLoader()
//				.getResource("93160218.json").openStream();
//		inputString = IOUtils.toString(in);
//		in.close();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws ParseException, IOException {
		String projectSrc = Util.retrieveProjectOnline(TestConstant.UNREACHABLECODE_PROJECT_0);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		analyzer = new UnreachableCodeAnalyzer(project);
		analyzer.analyze();

	}
	
	@Test
	public void testOnRealDataset() throws IOException, ParseException {
		String projectSrc = Util.retrieveProjectOnline(TestConstant.UNREACHABLECODE_PROJECT_1);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		analyzer = new UnreachableCodeAnalyzer(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getSummary());
		System.out.println(analyzer.getReport().getFullReport());
	}

}
