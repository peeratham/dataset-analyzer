package cs.vt.analysis.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class ParserTest {
	
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
		boolean test = true; int projectID; if(test){projectID=93160273;}else{projectID=43026762;}
		String stringInput = Util.retrieveProjectOnline(projectID);
		project = ScratchProject.loadProject(stringInput);
		System.out.println(project.getScriptable("TestCustomBlock"));
		
	}
	
	@Test
	public void testNestedToString() throws Exception{
		boolean test = true; int projectID; if(test){projectID=93160273;}else{projectID=43026762;}
		
		String stringInput = Util.retrieveProjectOnline(projectID); //real:43026762, test:93160273
		JSONArray scriptableInput = TestUtil.getScripts(stringInput,"testNestedToString");
		Script script = parser.loadScript(scriptableInput.get(0));
		int endCount = 	TestUtil.count("end", script.toString());
		assertEquals(2,endCount);
		int elseCount = 	TestUtil.count("else", script.toString());
		assertEquals(1,elseCount);
	}
	
	@Test
	public void testForever() throws Exception {
		boolean test = true; int projectID; if(test){projectID=93160273;}else{projectID=43026762;}
		String stringInput = Util.retrieveProjectOnline(projectID); //real:43026762, test:93160273
		JSONArray scriptableInput = TestUtil.getScripts(stringInput,"testForever");
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
		assert(project.toString().contains("UNDEFINED"));
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
		JSONObject sprite = TestUtil.getScriptable(projectSrc, "Sprite1");
		Scriptable s = Parser.loadScriptable(sprite);
		assertEquals(s.getScript(0).getBlocks().size(),2);
		
		
	}
		
}
