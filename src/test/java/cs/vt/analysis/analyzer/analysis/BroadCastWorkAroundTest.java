package cs.vt.analysis.analyzer.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.Util;

public class BroadCastWorkAroundTest {
	
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
		Analyzer analysisVisitor = new BroadCastWorkAround();
		analysisVisitor.setProject(project);
		analysisVisitor.analyze();	
		System.out.println(analysisVisitor);
		
	}

}
