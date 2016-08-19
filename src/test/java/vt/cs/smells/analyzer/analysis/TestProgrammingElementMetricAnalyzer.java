package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

public class TestProgrammingElementMetricAnalyzer {
	private ScratchProject project;
	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(117648956);
		project = ScratchProject.loadProject(projectSrc);
	}
	
	@Test
	public void testTotalVariableCount() throws AnalysisException{
		ProgrammingElementMetricAnalyzer analyzer = new ProgrammingElementMetricAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();	
		assertEquals(4, analyzer.totalVariableCount);
	}
	
	@Test
	public void testTotalCustomBlockCount() throws AnalysisException {
		ProgrammingElementMetricAnalyzer analyzer = new ProgrammingElementMetricAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();	
		assertEquals(2, analyzer.totalCustomBlock);
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}

	@Test
	public void testTotalCommentCount() throws AnalysisException {
		ProgrammingElementMetricAnalyzer analyzer = new ProgrammingElementMetricAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();	
		assertEquals(2, analyzer.totalComment);
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}
}
