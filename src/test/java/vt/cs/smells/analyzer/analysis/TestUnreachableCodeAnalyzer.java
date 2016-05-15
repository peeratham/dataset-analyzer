package vt.cs.smells.analyzer.analysis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Main;
import vt.cs.smells.analyzer.TestConstant;
import vt.cs.smells.analyzer.analysis.UnreachableAnalysisVisitor;
import vt.cs.smells.analyzer.analysis.VisitorBasedAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestUnreachableCodeAnalyzer {
	File[] dataset;
	
	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAnalysisVisitorPlugin() throws IOException, ParseException, ParsingException, AnalysisException{
		String projectSrc = Util.retrieveProjectOnline(TestConstant.UNREACHABLECODE_PROJECT_0);//TestConstant.UNREACHABLECODE_PROJECT_0);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		VisitorBasedAnalyzer analyzer = new VisitorBasedAnalyzer();
		analyzer.addAnalysisVisitor(new UnreachableAnalysisVisitor());
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
	}

}
