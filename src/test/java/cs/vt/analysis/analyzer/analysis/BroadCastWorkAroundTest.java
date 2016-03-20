package cs.vt.analysis.analyzer.analysis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.Util;

public class BroadCastWorkAroundTest {
	
	private ScratchProject project;
		
	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(97552510);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void detectFlagVariables() throws AnalysisException {
		Analyzer analyzer = new BroadCastWorkAroundAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();	
		System.out.println(analyzer);
		System.out.println(analyzer.getReport().getJSONReport());
		
	}

}
