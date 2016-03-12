package cs.vt.analysis.analyzer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cs.vt.analysis.analyzer.analysis.CloneUtil;
import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.CommandLoader;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.TopDownSubTreeCollector;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class ExtractSubTreeVisitorTest {
	private String projectSrc;
	Parser parser = new Parser();
	List<Block> subtreeList = null;
	
	@Before
	public void setUp() throws Exception {
		projectSrc = Util.retrieveProjectOnline(101357446);
		JSONObject sprite = TestUtil.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable s = Parser.loadScriptable(sprite);
		Visitor collector = new TopDownSubTreeCollector(new Identity());
		s.accept(collector);
		subtreeList = ((TopDownSubTreeCollector)collector).getSubTreeList();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testExtractFragmentVisitor() throws VisitFailure, ParseException, ParsingException {
		assertEquals(7,subtreeList.size());
	}
	
	@Test
	public void similarSubtreeAreInSameHashBucket(){
		System.out.println(subtreeList.get(1));
		System.out.println(CloneUtil.hashSubTree(subtreeList.get(1)));
		System.out.println(CommandLoader.COMMAND_TO_INDEX);
		assertEquals(CloneUtil.hashSubTree(subtreeList.get(1)), CloneUtil.hashSubTree(subtreeList.get(2)));
	}
}
