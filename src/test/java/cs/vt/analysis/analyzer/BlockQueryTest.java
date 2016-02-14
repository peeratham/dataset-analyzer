package cs.vt.analysis.analyzer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class BlockQueryTest {

	@Before
	public void setUp() throws Exception {
		JSONParser jsonParser = new JSONParser();
		Parser parser = new Parser();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void ifScriptContainsBlockReturnMatchedBlock() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(97601625);
		JSONObject spriteJSON = TestUtil.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable sprite= Parser.loadScriptable(spriteJSON);
		ArrayList<Block> results = sprite.getScript(0).containsBlock("doWaitUntil");
		assertEquals(2,results.size());
	}
	
	@Test
	public void ifBlockContainsBlockReturnMatchedBlock() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(97601625);
		JSONObject spriteJSON = TestUtil.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable sprite= Parser.loadScriptable(spriteJSON);
		ArrayList<Block> results = sprite.getScript(0).containsBlock("doRepeat");
		Block repeatBlock = results.get(0);
		ArrayList<Block> result = repeatBlock.containsBlock("doWaitUntil");
		assertEquals(1,result.size());
		
	}
	

}
