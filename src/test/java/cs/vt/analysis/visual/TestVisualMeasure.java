package cs.vt.analysis.visual;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Util;

public class TestVisualMeasure {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(102796854);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIfTwoScriptAlignedVertically() {
		Scriptable sprite = project.getScriptable("Sprite1");
		ScriptProperty a = new ScriptProperty(sprite.getScript(0));
		ScriptProperty b = new ScriptProperty(sprite.getScript(1));
		assertTrue(VisualMeasure.isVerticalAligned(a, b));
	}
	
	@Test
	public void testIfTwoScriptAlignedHorizontally() {
		Scriptable sprite = project.getScriptable("Sprite1");
		ScriptProperty a = new ScriptProperty(sprite.getScript(2));
		ScriptProperty b = new ScriptProperty(sprite.getScript(3));
		assertTrue(VisualMeasure.isHorizontalAligned(a, b));
	}

}
