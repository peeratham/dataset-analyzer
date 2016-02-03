package cs.vt.analysis.analyzer.analysis;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class TooLongScriptAnalysisTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTooLongScriptAnalysis() throws IOException, ParseException, ParsingException, AnalysisException {
		String projectSrc = Util.retrieveProjectOnline(96547076);//TestConstant.UNREACHABLECODE_PROJECT_0);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		VisitorBasedAnalyzer analyzer = new VisitorBasedAnalyzer();
		analyzer.addAnalysisVisitor(new LongScriptVisitor());
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
	}

}
