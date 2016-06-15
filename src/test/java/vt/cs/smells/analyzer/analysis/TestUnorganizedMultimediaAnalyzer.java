package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.analysis.UnorganizedMultimediaAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

public class TestUnorganizedMultimediaAnalyzer {

	private ScratchProject project;
	
	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(109963080);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMediaOrderingNotMatchedFrequentMediaReference() throws AnalysisException {
		Analyzer analyzer = new UnorganizedMultimediaAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();	
		System.out.println(analyzer);
		System.out.println(analyzer.getReport().getJSONReport());
		fail();
	}

}
