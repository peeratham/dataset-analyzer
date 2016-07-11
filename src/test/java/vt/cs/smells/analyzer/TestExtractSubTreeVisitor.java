package vt.cs.smells.analyzer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import vt.cs.smells.analyzer.CloneUtil;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.CommandLoader;
import vt.cs.smells.analyzer.parser.Parser;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.TopDownSubTreeCollector;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class TestExtractSubTreeVisitor {
	private String projectSrc;
	Parser parser = new Parser();
	List<Block> subtreeList = null;
	private Scriptable s;
	private Visitor collector;
	
	@Before
	public void setUp() throws Exception {
		projectSrc = Util.retrieveProjectOnline(101357446);
		JSONObject sprite = TestUtils.getJSONScriptable(projectSrc, "Sprite2");
		s = Parser.loadScriptable(sprite);
		collector = new TopDownSubTreeCollector(new Identity());
		s.accept(collector);
		subtreeList = ((TopDownSubTreeCollector)collector).getSubTreeList();
	}
	
	@Test
	public void testExtractFragmentVisitor() throws VisitFailure, ParseException, ParsingException {
		assertEquals(2,subtreeList.size());
	}
	
	@Test
	public void similarSubtreeAreInSameHashBucket(){
		System.out.println(subtreeList.get(1));
		System.out.println(CommandLoader.COMMAND_TO_INDEX);
		assertEquals(CloneUtil.hashSubTree(subtreeList.get(0)), CloneUtil.hashSubTree(subtreeList.get(1)));
	}
}
