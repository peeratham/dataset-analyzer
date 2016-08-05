package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

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
	}
	
	@Test
	public void testDuplicateValueWithSubStringSuffix(){
		
	}
	
	@Test
	public void testOutput() throws AnalysisException{
		DuplicateValueAnalyzer analyzer = new DuplicateValueAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		assertEquals(3, analyzer.count);
		assertEquals(7, analyzer.groupSizeStats.getSum(),0.01);
		assertEquals(2, analyzer.stringValueCount);
		assertEquals(1, analyzer.numberValueCount);
		System.out.println(analyzer.getReport().getConciseJSONReport());
		System.out.println(analyzer.getReport().getJSONReport());
	}

}
