package cs.vt.analysis.analyzer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.TopDownCollector;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class ExtractFragmentVisitorTest {
	private String projectSrc;
	Parser parser = new Parser();
//	private ScratchProject project;
	
	
	@Before
	public void setUp() throws Exception {
		projectSrc = Util.retrieveProjectOnline(96692734);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testExtractFragmentVisitor() throws VisitFailure, ParseException, ParsingException {
		JSONObject sprite = TestUtil.getJSONScriptable(projectSrc, "code1");
		Scriptable s = Parser.loadScriptable(sprite);
		Visitor collector = new TopDownCollector(new Identity());
		s.accept(collector);
		List<ArrayList<Block>> fragmentList = ((TopDownCollector)collector).getFragmentList();
		assertEquals(fragmentList.size(),4);
	}
	
	@Ignore
	@Test
	public void testExtractFragmentFromNestedBlocks(){
		//TODO
	}
}
