package vt.cs.smells.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Parser;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestParser {
	
	JSONParser jsonParser = new JSONParser();
	Parser parser = new Parser();
	ScratchProject project;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
		project = null;
	}
	
	@Test
	public void testParseScript() throws Exception {
		
	}
	
	@Test
	public void testParseBlock() throws Exception {
		
	}
	
	@Test
	public void testBlockNextChild() throws Exception {
		String stringInput = "[57,161,[[\"whenGreenFlag\"],[\"say:duration:elapsed:from:\", \"message\", \"sec\"]]]";
		JSONArray jsonInput = (JSONArray) jsonParser.parse(stringInput);
		Script script = parser.loadScript(jsonInput);
		Block first = script.getBlocks().get(0);
		Block second = script.getBlocks().get(1);
		assert(second ==  first.getNextBlock());
	}
	
	@Test
	public void testNextChildOnNestedBlock() throws Exception {
		String stringInput = "[57,161,[[\"whenGreenFlag\"],[\"say:duration:elapsed:from:\",\"Hello!\",2],[\"doIf\",[\"<\",\"1\",\"2\"],[[\"broadcast:\",\"message1\"],[\"doIf\",[\"<\",\"1\",\"2\"],[[\"broadcast:\",\"message1\"],[\"changeGraphicEffect:by:\",\"color\",25]]],[\"changeGraphicEffect:by:\",\"color\",25]]]]]";
		JSONArray jsonInput = (JSONArray) jsonParser.parse(stringInput);
		Script script = parser.loadScript(jsonInput);
		Block first = script.getBlocks().get(0);
		assertEquals(first.getCommand(), "whenGreenFlag");
		
		Block next = first.getNextBlock();
		assertEquals(next.getCommand(), "say:duration:elapsed:from:");
		
		next = next.getNextBlock();
		assertEquals(next.getCommand(), "doIf");
		
		assertTrue(next.hasNestedBlocks());
		Block firstChild = next.getNestedGroup().get(0).get(0);
		assertEquals(firstChild.getCommand(), "broadcast:");
		
		next = firstChild.getNextBlock();
		assertEquals(next.getCommand(), "doIf");
		
		next = next.getNextBlock();
		assertEquals(next.getCommand(), "changeGraphicEffect:by:");
	}
	
	@Test
	public void testCustomBlock() throws IOException, ParseException, ParsingException{
		String stringInput = Util.retrieveProjectOnline(93160273);
		project = ScratchProject.loadProject(stringInput);
		System.out.println(project.getScriptable("TestCustomBlock"));
		
	}
	
	@Test
	public void testNestedToString() throws Exception{
		boolean test = true; int projectID; if(test){projectID=93160273;}else{projectID=43026762;}
		
		String stringInput = Util.retrieveProjectOnline(projectID); //real:43026762, test:93160273
		JSONArray scriptableInput = TestUtils.getScripts(stringInput,"testNestedToString");
		Script script = parser.loadScript(scriptableInput.get(0));
		int endCount = 	TestUtils.count("end", script.toString());
		assertEquals(2,endCount);
		int elseCount = 	TestUtils.count("else", script.toString());
		assertEquals(1,elseCount);
	}
	
	@Test
	public void testForever() throws Exception {
		boolean test = true; int projectID; if(test){projectID=93160273;}else{projectID=43026762;}
		String stringInput = Util.retrieveProjectOnline(projectID); //real:43026762, test:93160273
		JSONArray scriptableInput = TestUtils.getScripts(stringInput,"testForever");
		Script script = parser.loadScript(scriptableInput.get(0));
	}
	

	
	@Test
	public void testOnSpriteWithNoScript() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(96035727);
		project = ScratchProject.loadProject(projectSrc);
	}
	
	@Test
	public void testCustomBlockDependency() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(TestConstant.CUSTOMBLOCK_DEPENDENCY);
		project = ScratchProject.loadProject(projectSrc);
	}
	
	@Test
	public void testParsingUndefinedBlock() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(96033699);
		project = ScratchProject.loadProject(projectSrc);
//		assertTrue(project.toString().contains("UNDEFINED"));
	}

	
	@Test(expected=ParsingException.class)
	public void testCustomBlockWithNoDefinition() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(96072285);
		project = ScratchProject.loadProject(projectSrc);
	}
	
	@Test
	public void testOnRealDataset() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(92821254);
		try{
			project = ScratchProject.loadProject(projectSrc);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLoadScriptable() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(96725247);
		JSONObject sprite = TestUtils.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable s = Parser.loadScriptable(sprite);
		assertEquals(s.getScript(0).getBlocks().size(),2);	
	}
	
	@Test
	public void testParseStageShouldCollectGlobalVariables() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(93160273);
		
		JSONObject stageJSON = TestUtils.getJSONScriptable(projectSrc, "Stage");
		ScratchProject proj = Parser.loadProject((JSONObject) jsonParser.parse(projectSrc));
		Scriptable stage = Parser.loadScriptable(stageJSON);
		assertTrue(stage.getAllVariables().keySet().contains("global1"));
	}
	
	@Test
	public void testParseSpritesShouldCollectLocalVariables() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(93160273);
		JSONObject spriteJSON = TestUtils.getJSONScriptable(projectSrc, "testLocalGlobalVar");
		Scriptable sprite= Parser.loadScriptable(spriteJSON);
		assertEquals(sprite.getAllVariables().size(),1);
		assertTrue(sprite.getAllVariables().keySet().contains("local1"));
		
	}
	
	@Test
	public void bug() throws ParseException, IOException, ParsingException{
		String projectSrc = Util.retrieveProjectOnline(96033699);
		JSONObject spriteJSON = TestUtils.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable sprite= Parser.loadScriptable(spriteJSON);
	}
	
	@Ignore
	@Test
	public void bugEmptyStringCustomBlock() throws ParseException, IOException, ParsingException{
		String projectSrc = Util.retrieveProjectOnline(106930272);
		JSONObject spriteJSON = TestUtils.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable sprite= Parser.loadScriptable(spriteJSON);
 		sprite.toString();
	}
		
}
