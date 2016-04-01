package cs.vt.analysis.analyzer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class PathTest {
	Scriptable scriptable;
	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(TestConstant.PATH_TEST_PROJECT);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		scriptable = project.getScriptable("Sprite1");
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFlatBlockSequence() throws ParseException, ParsingException, IOException {
		
		Script script = scriptable.getScript(0);
		assertEquals(scriptable, script.getParent());
		Block b = script.getBlocks().get(0);
		assertEquals(script, b.getParent());
		assertEquals(b.getNextBlock().getCommand(),"turnRight:");
		assertEquals(b.getNextBlock().getPreviousBlock(),b);
	}
	
	@Test
	public void testNestedBlockSequence() throws ParseException, ParsingException, IOException {
		Script script = scriptable.getScript(1);
		Block repeatBlock = script.getBlocks().get(0);
		ArrayList<Object> args = (ArrayList<Object>) repeatBlock.getArgs();
		ArrayList<Block> blockSequence  = (ArrayList<Block>) args.get(1); 
		Block moveBlock = blockSequence.get(0);
		assertEquals(repeatBlock,moveBlock.getParent());
	}
	
	@Test
	public void testNestedIFELSEBlock(){
		Script script = scriptable.getScript(2);
		Block ifelseBlock = script.getBlocks().get(0);
		ArrayList<Object> args = (ArrayList<Object>) ifelseBlock.getArgs();
		ArrayList<Block> blockSequence  = (ArrayList<Block>) args.get(2); 
		Block moveBlock = blockSequence.get(0);
		assertEquals(ifelseBlock,moveBlock.getParent());
	}
	
	@Test
	public void testDoubleNestedIFELSEBlock(){
		Script script = scriptable.getScript(3);
		Block ifelseBlock = script.getBlocks().get(0);
		ArrayList<Object> args = (ArrayList<Object>) ifelseBlock.getArgs();
		ArrayList<Block> blockSequence  = (ArrayList<Block>) args.get(2); 
		Block repeatBlock = blockSequence.get(1);
		ArrayList<Object> args2 = (ArrayList<Object>) repeatBlock.getArgs();
		ArrayList<Block> blockSequence2  = (ArrayList<Block>) args2.get(1); 
		Block moveBlock = blockSequence2.get(0); 
		assertEquals(repeatBlock,moveBlock.getParent());
		assertEquals(ifelseBlock,repeatBlock.getParent());
		assertEquals("Sprite1|Script@x101 y450[doIfElse('0' > '2')]|doIfElse|doRepeat|move 10 steps", moveBlock.getBlockPath().toString());
		
	}
	
	@Test
	public void testPathOnCustomBlock(){
		Script script = scriptable.getScript(4);
		Block moveBlock = script.getBlocks().get(1);
		assertEquals(script.getBlocks().get(0),moveBlock.getPreviousBlock());
		assertEquals(script.getBlocks().get(2), moveBlock.getNextBlock());
		assertEquals("Sprite1|Script@x93 y669[procDef[move-and-turn]]|move 10 steps",moveBlock.getBlockPath().toString());
	}
	
	@Test
	public void testScriptPath(){
		Script script = scriptable.getScript(4);
		assertEquals("Sprite1|Script@x93 y669[procDef[move-and-turn]]", script.getPath());
	}
	
	@Test
	public void testReadVarBlock(){
		Script script = scriptable.getScript(5);
		Block wait = script.getBlocks().get(0);
		Block readVar = (Block) wait.getArgs().get(0);
		assertEquals("Sprite1|Script@x95 y817[wait:elapsed:from:]|wait 'a' secs|'a'",readVar.getBlockPath().toString());
	}
	
}
