package vt.cs.smells.analyzer.analysis;

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
	public void testHashOfScriptProperties(){
		TooFineGrainScriptAnalyzer analyzer = new TooFineGrainScriptAnalyzer();
		Scriptable s = project.getScriptable("Sprite1");
		Script sc0 = s.getScript(0);
		Script sc1 = s.getScript(1);
		
		ScriptProperty sProp0 = new ScriptProperty(sc0);
		ScriptProperty sProp1 = new ScriptProperty(sc1);
		int sProp0Hash = sProp0.hashCode();
		int sProp1Hash = sProp1.hashCode();
		
		assertTrue(sProp0Hash==sProp1Hash);
	}

}
