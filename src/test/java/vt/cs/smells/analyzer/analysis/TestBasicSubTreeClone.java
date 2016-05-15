package vt.cs.smells.analyzer.analysis;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Parser;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestBasicSubTreeClone {

	private String projectSrc;
	@Before
	public void setUp() throws Exception {
		int projectID = 101357446;
		projectSrc = Util.retrieveProjectOnline(projectID);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void extractSubtree() throws ParseException, ParsingException {
		JSONObject sprite = TestUtils.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable s = Parser.loadScriptable(sprite);
		
	}
	@Test
	public void addRemoveSubtree() {
		
	}

}
