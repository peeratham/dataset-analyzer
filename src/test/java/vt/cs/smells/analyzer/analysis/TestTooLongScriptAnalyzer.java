package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.analysis.TooLongScriptAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

public class TestTooLongScriptAnalyzer {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(96547076);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void test() throws AnalysisException {
		TooLongScriptAnalyzer analyzer = new TooLongScriptAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		assertEquals(1, analyzer.count);
		assertEquals(17, analyzer.sizeStats.getMean(),0.01);
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}

}
