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

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class UnreachableCodeAnalyzerTest {
	
	
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
	public void testAnalysisVisitorPlugin() throws IOException, ParseException, ParsingException, AnalysisException{
		String projectSrc = Util.retrieveProjectOnline(TestConstant.UNREACHABLECODE_PROJECT_0);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		VisitorBasedAnalyzer analyzer = new VisitorBasedAnalyzer();
		analyzer.addAnalysisVisitor(new UnreachableAnalysisVisitor());
		analyzer.addProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getSummary());
		System.out.println(analyzer.getReport().getFullReport());
		
	}

}
