package vt.cs.smells.analyzer.analysis;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestCloneAnalyzer {

	private ScratchProject project;
	private CloneAnalyzer analyzer;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(101357446);
		project = ScratchProject.loadProject(projectSrc);
		analyzer = new CloneAnalyzer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws AnalysisException {
		
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
	}
	@Test
	public void testRealProject() throws IOException, ParseException, ParsingException, AnalysisException{
		String projectSrc = Util.retrieveProjectOnline(10094466);
		project = ScratchProject.loadProject(projectSrc);
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		
	}

}
