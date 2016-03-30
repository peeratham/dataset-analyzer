package cs.vt.analysis.analyzer.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.Util;

public class TestUnnecessaryBroadcast {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(103509016);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws AnalysisException {
		UnnecessaryBroadcastAnalyzer analyzer = new UnnecessaryBroadcastAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		ListAnalysisReport report = analyzer.getReport();
		assertEquals(1,report.result.size());
		System.out.println(report.getJSONReport());
	}

}
