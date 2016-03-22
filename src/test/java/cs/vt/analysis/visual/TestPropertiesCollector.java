package cs.vt.analysis.visual;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Util;

public class TestPropertiesCollector {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(101548138);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCollectingVar() {
		Scriptable sprite = project.getScriptable("collectVars");
		Script script0 = sprite.getScript(0);
		HashSet<String> vars = PropertiesCollector.collectVariables(script0);
		System.out.println(vars);
		assertEquals(2,vars.size());
	}
	

	

}
