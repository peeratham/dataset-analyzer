package vt.cs.smells.analyzer;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.analysis.MasteryAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

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
