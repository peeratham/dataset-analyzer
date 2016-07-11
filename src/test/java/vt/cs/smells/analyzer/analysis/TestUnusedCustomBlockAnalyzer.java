package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.analysis.UnusedCustomBlockAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

public class TestUnusedCustomBlockAnalyzer {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(115983109);
		project = ScratchProject.loadProject(projectSrc);
	}

	@Test
	public void test() throws AnalysisException {
		UnusedCustomBlockAnalyzer analyzer = new UnusedCustomBlockAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		assertEquals(1, analyzer.count);
		System.out.println(analyzer.getReport().getJSONReport());
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}

}
