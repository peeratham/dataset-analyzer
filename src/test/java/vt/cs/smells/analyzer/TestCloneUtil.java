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
	public void differentHashForDifferentSubtree(){
		Scriptable sprite = project.getScriptable("CloneUtil1");
		Script s0 = sprite.getScript(0);
		Script s1 = sprite.getScript(1);
		Script s2 = sprite.getScript(2);
		int hash0 = CloneUtil.hashSubTree(s0.getBlocks().get(0));
		int hash2 = CloneUtil.hashSubTree(s2.getBlocks().get(0));
		assertEquals(CloneUtil.hashSubTree(s0.getBlocks().get(0)),CloneUtil.hashSubTree(s1.getBlocks().get(0)));
		assertNotEquals(hash0,hash2);
	}
	
	@Test
	public void differentHashForDifferentOrderingOfCommands() {
		Scriptable sprite3 = project.getScriptable("CloneUtil2");
		Script s0 = sprite3.getScript(0);
		Script s1 = sprite3.getScript(1);
		
		int hash0 = CloneUtil.hashSubTree(s0.getBlocks().get(0));
		int hash1 = CloneUtil.hashSubTree(s1.getBlocks().get(0));
	
		assertNotEquals(hash0,hash1);
	}
	
	@Test
	public void subtreeSize() {
		Scriptable sprite2 = project.getScriptable("CloneUtil0");
		Script s0 = sprite2.getScript(0);
		Script s1 = sprite2.getScript(1);
		assertEquals(3, CloneUtil.getSubTreeSize(s0.getBlocks().get(0)));
		assertEquals(2, CloneUtil.getSubTreeSize(s1.getBlocks().get(0)));
	}

}
