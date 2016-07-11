package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestDuplicateCodeAnalyzer {

	private ScratchProject project;
	private DuplicateCodeAnalyzer analyzer;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(101357446);
		project = ScratchProject.loadProject(projectSrc);
		analyzer = new DuplicateCodeAnalyzer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws AnalysisException {
		analyzer.setProject(project);
		analyzer.analyze();
		assertEquals(2, analyzer.cloneGroupCount);
		assertEquals(4, analyzer.cloneGroupSizeStats.getSum(), 0.01);
		assertEquals(8, analyzer.cloneInstanceSizeStats.getSum(), 0.01);
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}
}
