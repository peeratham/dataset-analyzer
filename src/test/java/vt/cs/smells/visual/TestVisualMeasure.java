package vt.cs.smells.visual;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.visual.ScriptProperty;
import vt.cs.smells.visual.VisualMeasure;

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
