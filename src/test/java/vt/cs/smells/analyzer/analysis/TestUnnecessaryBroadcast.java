package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.analysis.UnnecessaryBroadcastAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

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
		assertEquals(1,report.resultJSON.size());
		System.out.println(report.getJSONReport());
	}

}
