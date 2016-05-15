package vt.cs.smells.analyzer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Parser;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.SequencePatternCollectionMatch;
import vt.cs.smells.analyzer.visitor.TopDown;
import vt.cs.smells.analyzer.visitor.TopDownFragmentCollector;
import vt.cs.smells.analyzer.visitor.Try;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;
import vt.cs.smells.pattern.VisitablePatternCollection;

public class TestVisitableSquencePatternCollection {
	JSONParser jsonParser = new JSONParser();
	Parser parser = new Parser();
	List<ArrayList<Block>> fragmentList;
	private String projectSrc;
	private VisitablePatternCollection pattern;
	
	

	@Before
	public void setUp() throws Exception {
		projectSrc = Util.retrieveProjectOnline(96692734);
		Scriptable scriptable1 = Parser.loadScriptable(TestUtils.getJSONScriptable(projectSrc, "code1"));
		Visitor collector = new TopDownFragmentCollector(new Identity());
		scriptable1.accept(collector);
		fragmentList = ((TopDownFragmentCollector)collector).getFragmentList();
		pattern = new VisitablePatternCollection(fragmentList);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBlockSequenceMatcherMatchScript() throws ParseException, ParsingException, VisitFailure {
		VisitablePatternCollection pattern = new VisitablePatternCollection(fragmentList);
		SequencePatternCollectionMatch m = new SequencePatternCollectionMatch(pattern);
		Visitor topDownMatcher = new TopDown(new Try(m));
		
		Scriptable scriptable2 = Parser.loadScriptable(TestUtils.getJSONScriptable(projectSrc, "code2"));
		Script term = scriptable2.getScript(0);
		topDownMatcher.visitScript(term);
	}

	@Test
	public void testBlockSequenceMatcherMatchScriptable() throws ParseException, ParsingException, VisitFailure {
		SequencePatternCollectionMatch m = new SequencePatternCollectionMatch(pattern);
		Visitor topDownMatcher = new TopDown(new Try(m));
		Scriptable scriptable2 = Parser.loadScriptable(TestUtils.getJSONScriptable(projectSrc, "code2"));
		topDownMatcher.visitScriptable(scriptable2);
		assertEquals(2,m.getMatchedResults().size());
	}
	
	@Test
	public void matchingSequenceWithinNestedBlock() throws ParsingException, ParseException, VisitFailure {
		SequencePatternCollectionMatch m = new SequencePatternCollectionMatch(pattern);
		Visitor topDownMatcher = new TopDown(new Try(m));
		Scriptable scriptable2 = Parser.loadScriptable(TestUtils.getJSONScriptable(projectSrc, "code3"));
		topDownMatcher.visitScriptable(scriptable2);
		assertEquals(2, m.getMatchedResults().size());
		for (Object o : m.getMatchedResults()) {
			System.out.println(o);
		}
	}
	
	
	
	

}
