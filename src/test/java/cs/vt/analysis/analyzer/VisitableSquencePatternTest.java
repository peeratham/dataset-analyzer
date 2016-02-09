package cs.vt.analysis.analyzer;

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
import cs.vt.analysis.analyzer.nodes.VisitableSequencePattern;
import cs.vt.analysis.analyzer.nodes.VisitablePattern;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.Match;
import cs.vt.analysis.analyzer.visitor.TopDownCollector;
import cs.vt.analysis.analyzer.visitor.TopDownGreedy;
import cs.vt.analysis.analyzer.visitor.Try;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class VisitableSquencePatternTest {
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
	public void testBlockSequenceMatcher() throws ParseException, ParsingException, VisitFailure {
		ArrayList<Block> sequencePattern = fragmentList.get(0);
		VisitablePattern pattern = new VisitableSequencePattern(sequencePattern);
		Match m = new Match(pattern);
		Visitor topDownMatcher = new TopDownGreedy(new Try(m));
		
		Scriptable scriptable2 = Parser.loadScriptable(TestUtil.getJSONScriptable(projectSrc, "code2"));
		Script term = scriptable2.getScript(0);
		Block firstTermBlock = term.getBlocks().get(0);

		topDownMatcher.visitBlock(firstTermBlock);
		System.out.println(m.getMaps());
	}
	
	
	
	

}
