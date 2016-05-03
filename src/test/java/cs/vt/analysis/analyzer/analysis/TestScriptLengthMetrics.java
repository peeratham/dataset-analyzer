package cs.vt.analysis.analyzer.analysis;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class TestScriptLengthMetrics {
	private ScratchProject project;
	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(107302951);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testScriptLengthMetric() throws IOException, ParseException, ParsingException, AnalysisException {
		Analyzer analyzer = new ScriptLengthMetricAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();	
		HashMap result = (HashMap)analyzer.getReport().getResult();
		assertEquals(4.75,result.get("mean"));
		assertEquals(19.0,result.get("sum"));
		System.out.println(analyzer.getReport().getJSONReport());
	}

}
