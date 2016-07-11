package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.analysis.BroadVarScopeAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

public class TestBroadVariableScope {

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
		assertEquals(1, analyzer.count);
	}
	
	

}
