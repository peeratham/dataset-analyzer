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
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class AnalysisUtilTest {

	private ScratchProject project;
	
	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(97900889);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void findBlock() throws IOException, ParseException, ParsingException {
		AnalysisUtil.findBlock(project, "doForever");
		ArrayList<Block> allForever = AnalysisUtil.findBlock(project, "doForever");
		assertEquals(1,allForever.size());
	}
	
	@Test
	public void blockSequenceContains() {
		ArrayList<Block> allForever = AnalysisUtil.findBlock(project, "doForever");
		ArrayList<Block> doUntil = AnalysisUtil.getBlockInSequence(allForever.get(0),"doWaitUntil");
		assertEquals(2,doUntil.size());
	}
	
	@Test
	public void getAllVariables() {
		
	}
	
	@Test
	public void getAllMessages() {
		
	}

}
