package vt.cs.smells.analyzer;

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

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Parser;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestBlockQuery {

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
		JSONObject spriteJSON = TestUtils.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable sprite= Parser.loadScriptable(spriteJSON);
		ArrayList<Block> results = sprite.getScript(0).containsBlock("doWaitUntil");
		assertEquals(2,results.size());
	}
	
	@Test
	public void ifBlockContainsBlockReturnMatchedBlock() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(97601625);
		JSONObject spriteJSON = TestUtils.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable sprite= Parser.loadScriptable(spriteJSON);
		ArrayList<Block> results = sprite.getScript(0).containsBlock("doRepeat");
		Block repeatBlock = results.get(0);
		ArrayList<Block> result = repeatBlock.containsBlock("doWaitUntil");
		assertEquals(1,result.size());
		
	}
	

}
