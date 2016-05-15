package vt.cs.smells.analyzer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Parser;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.TopDownFragmentCollector;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class TestExtractFragmentVisitor {
	private String projectSrc;
	Parser parser = new Parser();
//	private ScratchProject project;
	
	
	@Before
	public void setUp() throws Exception {
		projectSrc = Util.retrieveProjectOnline(101357446);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testExtractFragmentVisitor() throws VisitFailure, ParseException, ParsingException {
		JSONObject sprite = TestUtils.getJSONScriptable(projectSrc, "Sprite1");
		Scriptable s = Parser.loadScriptable(sprite);
		Visitor collector = new TopDownFragmentCollector(new Identity());
		s.accept(collector);
		List<ArrayList<Block>> fragmentList = ((TopDownFragmentCollector)collector).getSubTreeList();
		
		System.out.println(fragmentList);
	}
	
	@Ignore
	@Test
	public void testExtractFragmentFromNestedBlocks(){
		//TODO
	}
}
