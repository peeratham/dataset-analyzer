package vt.cs.smells.analyzer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Parser;
import vt.cs.smells.analyzer.parser.Util;

public class TestAugmentedInfo {
	JSONParser jsonParser = new JSONParser();
	Parser parser = new Parser();
	ScratchProject project;
	
	@Before
	public void setUp() throws Exception {
		String stringInput = Util.retrieveProjectOnline(102015008);
		project = ScratchProject.loadProject(stringInput);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void testCollectingVariables() {
		Scriptable sprite1 = project.getScriptable("Sprite1");
		Script script0 = sprite1.getScript(0);
		assertEquals(new HashSet(Arrays.asList("a", "b")), script0.getVariables());
	}

}
