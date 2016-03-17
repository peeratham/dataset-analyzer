package cs.vt.analysis.analyzer.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.Util;

public class BroadVariableScopeTest {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(97231677);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDetectIfGlobalIsUsedOnlyInOneScriptable() throws AnalysisException{
		BroadVarScopeAnalyzer analyzer = new BroadVarScopeAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		assertEquals(analyzer.getReport().getSummary().get("count"), 1);
		
	}

}
