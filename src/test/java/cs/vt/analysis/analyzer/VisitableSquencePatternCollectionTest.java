package cs.vt.analysis.analyzer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.analysis.TestUtil;
import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.nodes.VisitablePatternCollection;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.SequencePatternCollectionMatch;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.TopDownFragmentCollector;
import cs.vt.analysis.analyzer.visitor.Try;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class VisitableSquencePatternCollectionTest {
	JSONParser jsonParser = new JSONParser();
	Parser parser = new Parser();
	List<ArrayList<Block>> fragmentList;
	private String projectSrc;
	private VisitablePatternCollection pattern;
	
	

	@Before
	public void setUp() throws Exception {
		projectSrc = Util.retrieveProjectOnline(96692734);
		Scriptable scriptable1 = Parser.loadScriptable(TestUtil.getJSONScriptable(projectSrc, "code1"));
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
		
		Scriptable scriptable2 = Parser.loadScriptable(TestUtil.getJSONScriptable(projectSrc, "code2"));
		Script term = scriptable2.getScript(0);
		topDownMatcher.visitScript(term);
	}

	@Test
	public void testBlockSequenceMatcherMatchScriptable() throws ParseException, ParsingException, VisitFailure {
		SequencePatternCollectionMatch m = new SequencePatternCollectionMatch(pattern);
		Visitor topDownMatcher = new TopDown(new Try(m));
		Scriptable scriptable2 = Parser.loadScriptable(TestUtil.getJSONScriptable(projectSrc, "code2"));
		topDownMatcher.visitScriptable(scriptable2);
		assertEquals(2,m.getMatchedResults().size());
	}
	
	@Test
	public void matchingSequenceWithinNestedBlock() throws ParsingException, ParseException, VisitFailure {
		SequencePatternCollectionMatch m = new SequencePatternCollectionMatch(pattern);
		Visitor topDownMatcher = new TopDown(new Try(m));
		Scriptable scriptable2 = Parser.loadScriptable(TestUtil.getJSONScriptable(projectSrc, "code3"));
		topDownMatcher.visitScriptable(scriptable2);
		assertEquals(2, m.getMatchedResults().size());
		for (Object o : m.getMatchedResults()) {
			System.out.println(o);
		}
	}
	
	
	
	

}
