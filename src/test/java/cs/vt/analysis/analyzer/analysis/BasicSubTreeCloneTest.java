package cs.vt.analysis.analyzer.analysis;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class BasicSubTreeCloneTest {

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
