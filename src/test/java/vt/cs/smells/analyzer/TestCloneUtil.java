package vt.cs.smells.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.HashSet;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import vt.cs.smells.analyzer.CloneUtil;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.CommandLoader;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestCloneUtil {

	private String projectSrc;
	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		projectSrc = Util.retrieveProjectOnline(101357446);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void commandToIndexIncludeCustomCommands() throws IOException, ParseException, ParsingException {

		assertEquals(CommandLoader.COMMAND_TO_BLOCKSPEC.size()+CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC.size(), CommandLoader.COMMAND_TO_INDEX.size());
	}
	
	@Test
	public void eachCommandHasDistinctIndex() {
		HashSet indexSet = new HashSet(CommandLoader.COMMAND_TO_INDEX.values());
		assertEquals(CommandLoader.COMMAND_TO_INDEX.keySet().size(), indexSet.size());
	}
	
	@Test
	public void sameHashValueForIdenticalSubtree() {
		Scriptable sprite3 = project.getScriptable("Sprite3");
		Script s0 = sprite3.getScript(0);
		Script s1 = sprite3.getScript(1);
		assertEquals(CloneUtil.hashSubTree(s0.getBlocks().get(0)),CloneUtil.hashSubTree(s1.getBlocks().get(0)));
	}	
	
	
	@Test
	public void differentHashForDifferentSubtree(){
		Scriptable sprite3 = project.getScriptable("Sprite3");
		Script s0 = sprite3.getScript(0);
		Script s1 = sprite3.getScript(1);
		Script s2 = sprite3.getScript(2);
		int hash0 = CloneUtil.hashSubTree(s0.getBlocks().get(0));
		int hash2 = CloneUtil.hashSubTree(s2.getBlocks().get(0)); 
		assertNotEquals(hash0,hash2);
	}
	
	@Test
	public void differentHashForDifferentOrderingOfCommands() {
		Scriptable sprite3 = project.getScriptable("Sprite3");
		Script s0 = sprite3.getScript(0);
		Script s3 = sprite3.getScript(3);
		
		int hash0 = CloneUtil.hashSubTree(s0.getBlocks().get(0));
		int hash3 = CloneUtil.hashSubTree(s3.getBlocks().get(0));
	
		assertNotEquals(hash0,hash3);
	}
	
	@Test
	public void subtreeSize() {
		Scriptable sprite5 = project.getScriptable("Sprite5");
		Script s0 = sprite5.getScript(0);
		Script s1 = sprite5.getScript(1);
		assertEquals(6, CloneUtil.getSubTreeSize(s0.getBlocks().get(0)));
		assertEquals(4, CloneUtil.getSubTreeSize(s1.getBlocks().get(0)));
	}

}
