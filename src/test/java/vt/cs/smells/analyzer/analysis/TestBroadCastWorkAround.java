package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.analysis.BroadCastWorkAroundAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

public class TestBroadCastWorkAround {
	
	private ScratchProject project;
		
	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(97552510);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void detectFlagVariables() throws AnalysisException {
		BroadCastWorkAroundAnalyzer analyzer = new BroadCastWorkAroundAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();	
		System.out.println(analyzer.getReport().getJSONReport());
		System.out.println(analyzer.getReport().getConciseJSONReport());
		assertEquals(2, analyzer.count);
	}

}
