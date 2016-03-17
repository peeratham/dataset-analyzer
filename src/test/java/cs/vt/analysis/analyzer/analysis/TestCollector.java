package cs.vt.analysis.analyzer.analysis;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.select.Collector;
import cs.vt.analysis.select.Evaluator;

public class TestCollector {

	ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(102015008);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCollectCommandMatches() {
		Script sc0 = project.getScriptable("Sprite1").getScript(0);
		ArrayList<Block> allVars = Collector.collect(new Evaluator.BlockCommand("setVar:to:"), project);
		ArrayList<Block> script0Vars = Collector.collect(new Evaluator.BlockCommand("setVar:to:"), sc0);
		assertEquals(3, allVars.size());
		assertEquals(2, script0Vars.size());
	}
	
}
