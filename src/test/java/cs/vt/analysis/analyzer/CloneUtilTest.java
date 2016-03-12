package cs.vt.analysis.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.HashSet;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cs.vt.analysis.analyzer.analysis.CloneUtil;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.CommandLoader;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class CloneUtilTest {

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
		Script s2 = sprite3.getScript(2);
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
		Scriptable sprite4 = project.getScriptable("Sprite4");
		Script s0 = sprite4.getScript(0);
		Script s1 = sprite4.getScript(1);
		Script s2 = sprite4.getScript(2);
		int hash0 = CloneUtil.hashSubTree(s0.getBlocks().get(0));
		int hash1 = CloneUtil.hashSubTree(s1.getBlocks().get(0));
		int hash2 = CloneUtil.hashSubTree(s2.getBlocks().get(0));
		assertEquals(hash0,hash1);
		assertNotEquals(hash0,hash2);
	}

}
