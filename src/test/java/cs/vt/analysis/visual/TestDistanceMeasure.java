package cs.vt.analysis.visual;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Util;

public class TestDistanceMeasure {

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
	public void testDistanceBasedOnCoordinates(){
		Scriptable sprite = project.getScriptable("CoordBasedDistance");
		Script a = sprite.getScript(0);
		assertEquals("forward:",a.getBlocks().get(0).getCommand());
		Script b = sprite.getScript(1);
		assertEquals("turnRight:",b.getBlocks().get(0).getCommand());
		Script c = sprite.getScript(2);
		assertEquals("changeXposBy:",c.getBlocks().get(0).getCommand());
		ScriptProperty pA = new ScriptProperty(a);
		ScriptProperty pB = new ScriptProperty(b);
		ScriptProperty pC = new ScriptProperty(c);
		double distA2B = new DistanceMeasure.CoordinateBased().getDist(pA, pB);
		double distA2C = new DistanceMeasure.CoordinateBased().getDist(pA, pC);
		assertTrue(distA2B < distA2C);
		
	}
	
	@Test
	public void testDistanceBasedOnSharedVariable(){
		Scriptable sprite = project.getScriptable("sharedVarBasedDistance");
		Script a = sprite.getScript(0);
		Script b = sprite.getScript(1);
		Script c = sprite.getScript(2);
		Script d = sprite.getScript(3);
		ScriptProperty pA = new ScriptProperty(a);
		ScriptProperty pB = new ScriptProperty(b);
		ScriptProperty pC = new ScriptProperty(c);
		ScriptProperty pD = new ScriptProperty(d);
		double dist0 = new DistanceMeasure.SharedVariableBased().getDist(pA, pA);
		double dist1 = new DistanceMeasure.SharedVariableBased().getDist(pA, pB);
		assertEquals(0,dist0,0.1);
		assertEquals(1,dist1,0.1);
		double dist2 = new DistanceMeasure.SharedVariableBased().getDist(pC, pD);
		assertEquals(0.5, dist2, 0.1);
	}
	

	

}
