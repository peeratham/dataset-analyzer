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

public class TestDictAnalysisReport {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException, ParseException, ParsingException, AnalysisException {
		String projectSrc = Util.retrieveProjectOnline(102707386);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		Analyzer analyzer = new MasteryAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();	
		System.out.println(analyzer.getReport().getJSONReport());
		
	}

}
