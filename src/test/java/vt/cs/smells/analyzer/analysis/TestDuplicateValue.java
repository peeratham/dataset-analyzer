package vt.cs.smells.analyzer.analysis;

import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

public class TestDuplicateValue {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(107902889);
		project = ScratchProject.loadProject(projectSrc);
	}

	@Test
	public void testNoSubString() throws AnalysisException {
		DuplicateValueAnalyzer analyzer = new DuplicateValueAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
	}
	
	@Test
	public void testDuplicateValueWithSubStringSuffix(){
		
	}	

}
