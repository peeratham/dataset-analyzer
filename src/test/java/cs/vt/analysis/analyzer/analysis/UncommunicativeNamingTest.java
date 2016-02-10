package cs.vt.analysis.analyzer.analysis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class UncommunicativeNamingTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void detectDefaultSpriteDefaultNaming() throws IOException, ParseException, ParsingException, AnalysisException {
		String projectSrc = Util.retrieveProjectOnline(97396677);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		VisitorBasedAnalyzer analyzer = new VisitorBasedAnalyzer();
		analyzer.addAnalysisVisitor(new UncommunicativeNamingVisitor());
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		assertEquals(analyzer.getReport().getRecordCounts(),4);
	}

}
