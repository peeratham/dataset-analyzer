package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.visual.ScriptProperty;

public class TestTooFineGrainAnalyzer {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(114265366);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetGroupingOfSameHatBlock() throws AnalysisException {
		Analyzer analyzer = new TooFineGrainScriptAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
	}
	
	
	@Test
	public void testAnalysisProperty() throws AnalysisException{
		TooFineGrainScriptAnalyzer analyzer = new TooFineGrainScriptAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		assertEquals(1, analyzer.count);
		assertEquals(2, analyzer.sizeStats.getSum(), 0.01);
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}

}
