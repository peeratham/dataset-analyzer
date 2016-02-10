package cs.vt.analysis.analyzer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.nodes.VisitablePattern;
import cs.vt.analysis.analyzer.nodes.VisitablePatternCollection;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.SequencePatternCollectionMatch;
import cs.vt.analysis.analyzer.visitor.TopDownCollector;
import cs.vt.analysis.analyzer.visitor.TopDownGreedy;
import cs.vt.analysis.analyzer.visitor.Try;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class VisitableSquencePatternCollectionTest {
	JSONParser jsonParser = new JSONParser();
	Parser parser = new Parser();
	List<ArrayList<Block>> fragmentList;
	private String projectSrc;
	
	

	@Before
	public void setUp() throws Exception {
		projectSrc = Util.retrieveProjectOnline(96692734);
		Scriptable scriptable1 = Parser.loadScriptable(TestUtil.getJSONScriptable(projectSrc, "code1"));
		Visitor collector = new TopDownCollector(new Identity());
		scriptable1.accept(collector);
		fragmentList = ((TopDownCollector)collector).getFragmentList();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBlockSequenceMatcherMatchScript() throws ParseException, ParsingException, VisitFailure {
		VisitablePatternCollection pattern = new VisitablePatternCollection(fragmentList);
		SequencePatternCollectionMatch m = new SequencePatternCollectionMatch(pattern);
		Visitor topDownMatcher = new TopDownGreedy(new Try(m));
		
		Scriptable scriptable2 = Parser.loadScriptable(TestUtil.getJSONScriptable(projectSrc, "code2"));
		Script term = scriptable2.getScript(0);
		topDownMatcher.visitScript(term);
		for (Object o : m.getMatchedResults()) {
			System.out.println(o);
			
		}
	}

	@Test
	public void testBlockSequenceMatcherMatchScriptable() throws ParseException, ParsingException, VisitFailure {
		VisitablePatternCollection pattern = new VisitablePatternCollection(fragmentList);
		SequencePatternCollectionMatch m = new SequencePatternCollectionMatch(pattern);
		Visitor topDownMatcher = new TopDownGreedy(new Try(m));
		Scriptable scriptable2 = Parser.loadScriptable(TestUtil.getJSONScriptable(projectSrc, "code2"));
		topDownMatcher.visitScriptable(scriptable2);
		for (Object o : m.getMatchedResults()) {
			System.out.println(o);
		}
		assertEquals(m.getMatchedResults().size(),2);
	}
	
	
	
	
	
	

}
