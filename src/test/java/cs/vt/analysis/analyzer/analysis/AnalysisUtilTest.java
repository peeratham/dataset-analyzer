package cs.vt.analysis.analyzer.analysis;



import static org.junit.Assert.assertEquals;

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

public class AnalysisUtilTest {

	private ScratchProject project;
	
	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void findBlock() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(97900889);
		project = ScratchProject.loadProject(projectSrc);
		AnalysisUtil.findBlock(project, "doForever");
		ArrayList<Block> allForever = AnalysisUtil.findBlock(project, "doForever");
		assertEquals(1,allForever.size());
	}
	
	@Test
	public void blockSequenceContains() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(97900889);
		project = ScratchProject.loadProject(projectSrc);
		ArrayList<Block> allForever = AnalysisUtil.findBlock(project, "doForever");
		ArrayList<Block> doUntil = AnalysisUtil.getBlockInSequence(allForever.get(0),"doWaitUntil");
		assertEquals(2,doUntil.size());
	}
	
	@Test
	public void getVarDef() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(102419169);
		project = ScratchProject.loadProject(projectSrc);
		Scriptable sprite2 = project.getScriptable("Sprite1");
		Script script0 = sprite2.getScript(0);
		ArrayList<Block> varBlocks = AnalysisUtil.getVarDefBlocks(script0);
		assertEquals(2, varBlocks.size());
		System.out.println(varBlocks);
	}
	
	@Test
	public void getVarReference() throws IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(102419169);
		project = ScratchProject.loadProject(projectSrc);
		Scriptable sprite2 = project.getScriptable("Sprite1");
		Script script0 = sprite2.getScript(0);
		ArrayList<Block> varBlocks = AnalysisUtil.getVarRefBlocks(script0);
		assertEquals(1, varBlocks.size());
		System.out.println(varBlocks);
		
	}

	
	@Test
	public void getAllMessages() {
		
	}
	
	

}
